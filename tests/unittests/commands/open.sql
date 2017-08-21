--------------------------------------------------------------------------
-- .open command test
--------------------------------------------------------------------------
.help open
.open

.run ../common/sqlite_setup.sql
.open sqlite::memory:
.open sqlite::memory:

.close

