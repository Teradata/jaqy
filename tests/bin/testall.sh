#!/bin/bash
#
# Run all tests.
#

if [ -z "$JAQY_HOME" ]; then
	JAQY_HOME=`dirname $0`/../..
fi
export JAQY_HOME=`readlink -f "$JAQY_HOME"`

RUNNER=${JAQY_HOME}/tests/bin/runtest.sh

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
