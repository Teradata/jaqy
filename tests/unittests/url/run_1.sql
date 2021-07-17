--------------------------------------------------------------------------
-- input test
--------------------------------------------------------------------------
.run ../common/sqlite_setup.sql
.open sqlite::memory:

.run https://github.com/Teradata/jaqy/raw/master/tests/unittests/url/data/dir1/script1.sql
