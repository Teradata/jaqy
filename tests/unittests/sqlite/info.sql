--------------------------------------------------------------------------
-- .info command test
--------------------------------------------------------------------------
.run ../common/sqlite_setup.sql
.open sqlite::memory:
.info behavior
.info catalog
.info client
.info feature
.info function
.info keyword
.info limit
.info schema
.info table
.info typemap
.info importmap
.format csv
.info type
.format table
.info server
.info user
.quit

