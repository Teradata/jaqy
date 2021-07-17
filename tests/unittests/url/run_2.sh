#!/bin/bash
$JAQY_CMD .run ../common/sqlite_setup.sql \; .open sqlite::memory: \; .run https://github.com/Teradata/jaqy/raw/master/tests/unittests/url/data/dir1/script1.sql < $1 > $2
