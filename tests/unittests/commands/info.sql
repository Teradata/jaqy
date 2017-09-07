--------------------------------------------------------------------------
-- .list command test
--------------------------------------------------------------------------
.help info
.info
.info dummy
.format csv

.run ../common/postgresql_setup.sql
.info behavior
.info client
.info feature
.info function
.info keyword
.info limit
.info table
.info user
.close

.run ../common/mysql_setup.sql
.open sqlite::memory:
.info catalog
.info schema
.close

.run ../common/sqlite_setup.sql
.open sqlite::memory:
.info server
.info type
.close

