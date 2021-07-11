#!/bin/bash
$JAQY_CMD .run ../common/sqlite_setup.sql \; .open sqlite::memory:< $1 > $2
