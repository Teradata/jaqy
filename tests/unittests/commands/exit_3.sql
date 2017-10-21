--------------------------------------------------------------------------
-- .exit command test
--------------------------------------------------------------------------
.close

.run ../common/sqlite_setup.sql
.open sqlite::memory:

SELECT asdf;

.close

.debug log info
.exit
