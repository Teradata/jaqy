#!/bin/bash
#
# A simple file based test framework.
#

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
jq="java -Xmx256m -jar ${JAQY_HOME}/dist/jaqy-1.1.0.jar"

function run ()
{
	INIT=
	if [ -f initrc ]; then
		INIT="--rcfile initrc"
	fi
	FILE=$1
	BASE=${FILE%.sql}
	CONTROL="${CONTROLDIR}/${BASE}.control"
	OUTPUT="${OUTPUTDIR}/${BASE}.txt"
	ERROR="${OUTPUTDIR}/${BASE}.diff"
	echo "$jq $INIT < ${FILE} > ${OUTPUT}"
	$jq $INIT < ${FILE} > ${OUTPUT}
	if [ -f "$CONTROL" ]; then
		diff "$CONTROL" "$OUTPUT" > $ERROR 2>/dev/null
		if [ $? -eq 0 ]; then
			echo "$FILE passed."
			rm -f "$ERROR"
			rm -f "$OUTPUT"
		else
			echo "$FILE failed."
			return 1
		fi
	else
		echo "Control file for $FILE is not found."
		return 1
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
mv ${INPUTDIR}/control ${CONTROLDIR}
pwd > ${TESTDIR}/testpath.txt

cd "$INPUTDIR"

HASERROR=0
for file in `cat FILELIST`; do
	if [ -f "su_$file" ]; then
		run "su_$file" || HASERROR=1
	fi
	run $file || HASERROR=1
	if [ -f "cu_$file" ]; then
		run "cu_$file" || HASERROR=1
	fi
done

if [ $HASERROR -eq 0 ]; then
	echo "test passed."
	cd /tmp
	rm -rf $TESTDIR
else
	echo "test directory is at $TESTDIR"
fi
