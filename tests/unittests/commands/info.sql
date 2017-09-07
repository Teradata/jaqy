--------------------------------------------------------------------------
-- .list command test
--------------------------------------------------------------------------
.help info
.info
.info dummy

.run ../common/postgresql_setup.sql
.format csv

.info behavior
.info client
.info feature
.info function
.info keyword
.info limit
.info schema
.info table
.info user

.close

.run ../common/sqlite_setup.sql
.open sqlite::memory:
.info server
.info catalog
.info type
.close

