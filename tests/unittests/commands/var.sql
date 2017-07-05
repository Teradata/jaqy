--------------------------------------------------------------------------
-- .list command test
--------------------------------------------------------------------------
.run ../common/sqlite_setup.sql
.help var
.var

.format csv
.open sqlite::memory:

.var

