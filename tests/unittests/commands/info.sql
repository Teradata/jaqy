--------------------------------------------------------------------------
-- .list command test
--------------------------------------------------------------------------
.run ../common/postgresql_setup.sql
.format csv

.help info
.info

.info behavior
.info catalog
.info client
.info feature
.info function
.info keyword
.info limit
.info schema
.info server
.info table
.info user

.close

.run ../common/sqlite_setup.sql
.open sqlite::memory:
.info type
.close

