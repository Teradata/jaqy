--------------------------------------------------------------------------
-- .close command test
--------------------------------------------------------------------------
.help close
.close

.run ../common/sqlite_setup.sql
.open sqlite::memory:

.close

