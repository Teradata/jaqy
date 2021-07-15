#!/bin/bash
#
# A very simple comparison routine for 2 Excel files
#
# Author: Heng Yuan
# Date:   7/13/2021
#

if [ $# -ne 2 ]; then
	exit 1
fi

TMPDIR=/tmp/excel.$$

CONTROLFILE=$1
TESTFILE=$2

if [ ! -f $CONTROLFILE ]; then
	echo $CONTROLFILE does not exist.
	exit 1
fi
if [ ! -f $TESTFILE ]; then
	echo $TESTFILE does not exist.
	exit 1
fi

CONTROLFILE=`readlink -f $CONTROLFILE`
TESTFILE=`readlink -f $TESTFILE`

mkdir $TMPDIR

mkdir $TMPDIR/f1
mkdir $TMPDIR/f2

(cd $TMPDIR/f1 ; unzip -qq $CONTROLFILE)
(cd $TMPDIR/f2 ; unzip -qq $TESTFILE)

cleanup ()
{
	rm -rf $TMPDIR
}

trap cleanup EXIT

rm -f ${TMPDIR}/f1/docProps/core.xml ${TMPDIR}/f2/docProps/core.xml
diff -b -r ${TMPDIR}/f1 ${TMPDIR}/f2
