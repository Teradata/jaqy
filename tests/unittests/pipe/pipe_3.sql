--------------------------------------------------------------------------
-- test pipe
--------------------------------------------------------------------------
.run ../common/postgresql_setup.sql

.import pipe

.export csv test.csv
.import pipe

.export pipe
.export pipe
.import pipe

.os rm -f test.csv
.close
