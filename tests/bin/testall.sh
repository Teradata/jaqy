#!/bin/bash
#
# Run all tests.
#

usage ()
{
cat <<<EOF
$0 [options]

options:
    -h		help
	-j		run with jacoco offline instrumentation.
EOF
}

JACOCO=0

while getopts "hj" opt; do
	case "${opt}" in
		h)
			usage
			exit 0
			;;
		j)
			JACOCO=1
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
export JAQY_HOME=`readlink -f "$JAQY_HOME"`

RUNNER=${JAQY_HOME}/tests/bin/runtest.sh

if [ $JACOCO -eq 1 ]; then
	RUNNER="${JAQY_HOME}/tests/bin/runtest.sh -j"
fi

runTest ()
{
	cd $1
	$RUNNER
	return $?
}

cd ${JAQY_HOME}/tests/unittests

for dir in `cat DIRLIST`; do
	cd ${JAQY_HOME}/tests/unittests
	echo "== testing ${dir} =="
	if [ -f ${dir}/FILELIST ]; then
		runTest ${dir}
		if [ $? -ne 0 ]; then
			exit 1
		fi
	fi
done

echo 'All tests are successful.'
