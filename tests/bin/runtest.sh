#!/bin/bash
#
# A simple file based test framework.
#

CODECOV=0
SHOWCMD=0

usage ()
{
cat <<<EOF
$0 [options]

options:
    -h		show this help message.
	-c		run with code coverage offline instrumentation.
	-s		show the testing command.
EOF
}

while getopts "hcs" opt; do
	case "${opt}" in
		h)
			usage
			exit 0
			;;
		c)
			CODECOV=1
			;;
		s)
			SHOWCMD=1
			;;
		*)
			usage
			exit 1
			;;
	esac
done
shift $((OPTIND-1))

if [ -z "$JAQY_HOME" ]; then
	JAQY_HOME=`dirname $0`/../..
fi
JAQY_HOME=`readlink -f "$JAQY_HOME"`

if [ ! -f FILELIST ]; then
	echo "Current directory is not a test directory."
	exit 1;
fi

UNITTESTDIR=`readlink -f ..`
TESTDIR=/tmp/test.$$
INPUTDIR=${TESTDIR}/input
OUTPUTDIR=${TESTDIR}/output
CONTROLDIR=${TESTDIR}/control

JAVA=java
if [ -f /usr/lib/jvm/java-8-openjdk-amd64/bin/java ]; then
	JAVA=/usr/lib/jvm/java-8-openjdk-amd64/bin/java
fi

CODECOV_STR="${JAQY_HOME}/lib/clover-runtime-4.4.1.jar"
JAQY_STR="-classpath ${JAQY_HOME}/dist/jaqy-1.2.0.jar:${JAQY_HOME}/jaqy-s3/target/jaqy-s3-1.2.0.jar:${JAQY_HOME}/jaqy-azure/target/jaqy-azure-1.2.0.jar:${JAQY_HOME}/jaqy-avro/target/jaqy-avro-1.2.0.jar"
MAIN_STR="com.teradata.jaqy.Main"

export JAQY_CMD="${JAVA} -Xmx256m ${JAQY_STR} ${MAIN_STR}"
if [ $CODECOV -eq 1 ]; then
	export JAQY_CMD="${JAVA} -Xmx256m -Dfile.encoding=UTF-8 ${JAQY_STR}:${CODECOV_STR} ${MAIN_STR}"
fi

function compare ()
{
	CONTROL=$1
	OUTPUT=$2
	ERROR=$3
	sed -i '/-- ignore begin/,/-- ignore end/d' $OUTPUT
	diff "$CONTROL" "$OUTPUT" > $ERROR 2>/dev/null
	return $?
}

function run ()
{
	FILE=$1

	BASE=${FILE%.sql}
	FILE_SHELL=${BASE}.sh
	CONTROL="${CONTROLDIR}/${BASE}.control"
	OUTPUT="${OUTPUTDIR}/${BASE}.txt"
	ERROR="${OUTPUTDIR}/${BASE}.diff"

	CMD="$JAQY_CMD < ${FILE} > ${OUTPUT}"
	if [ -f $FILE_SHELL ]; then
		CMD="./$FILE_SHELL $FILE $OUTPUT"
	fi

	if [ $SHOWCMD -eq 1 ]; then
		echo "$CMD"
	else
		echo "... ${FILE}"
	fi
	if [ -f $FILE_SHELL ]; then
		$CMD
	else
		$JAQY_CMD < ${FILE} > ${OUTPUT}
	fi
	if [ -f "$CONTROL" ]; then
		compare "$CONTROL" "$OUTPUT" "$ERROR"
		if [ $? -eq 0 ]; then
			rm -f "$ERROR"
			rm -f "$OUTPUT"
		else
			echo ">>> $FILE failed. <<<"
			return 1
		fi
	else
		echo ">>> $FILE control not found. <<<"
		return 1
	fi

	return 0
}

function runTest ()
{
	file=$1
	if [ -f "su_$file" ]; then
		run "su_$file" || return 1
	fi
	run $file || return 1
	if [ -f "cu_$file" ]; then
		run "cu_$file" || return 1
	fi
	return 0
}

# setup the test directory
rm -rf "$TESTDIR"

mkdir $TESTDIR
mkdir $INPUTDIR
mkdir $OUTPUTDIR

# copy all the files into output direct
cp -r * $INPUTDIR

ln -s "${UNITTESTDIR}/common/" $TESTDIR/common
ln -s "${UNITTESTDIR}/drivers/" $TESTDIR/drivers
ln -s ${JAQY_HOME} $TESTDIR/home
mv ${INPUTDIR}/control ${CONTROLDIR}
pwd > ${TESTDIR}/testpath.txt

cd "$INPUTDIR"

HASERROR=0

if [ $# -gt 0 ]; then
	for file in $@; do
		runTest $file || HASERROR=1 || break
	done
else
	for file in `cat FILELIST`; do
		runTest $file || HASERROR=1 || break
	done
fi

if [ $HASERROR -eq 0 ]; then
	echo "test passed."
	cd /tmp
	rm -rf $TESTDIR
else
	echo "test directory is at $TESTDIR"
	exit 1
fi
