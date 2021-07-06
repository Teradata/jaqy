#!/bin/bash

if [ ! -f ../testpath.txt ]; then
	echo "test path not found."
	exit 1
fi

if [ $# -eq 0 ]; then
	echo "control files not specified."
fi

TESTPATH=`cat ../testpath.txt`

function filecopy ()
{
	FILE=$1
	BASE=${FILE%.*}
	CONTROL=${TESTPATH}/control/${BASE}.control

	cp ${FILE} ${CONTROL}
}

for f in $@; do
	filecopy "$f"
done
