--------------------------------------------------------------------------
-- test SQL types
--------------------------------------------------------------------------
.run ../common/sqlite_setup.sql
.format csv
.open sqlite::memory:
.run ../common/ansi_types.sql
.close

