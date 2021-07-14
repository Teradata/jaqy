#!/bin/bash
#
# A simple script to generate FILELIST for unit tests
#

/bin/ls -C1 *.sql > FILELIST
sed -i '/^su_/d' FILELIST
sed -i '/^cu_/d' FILELIST

