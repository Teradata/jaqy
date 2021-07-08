--------------------------------------------------------------------------
-- .info command test
--------------------------------------------------------------------------
.help info
.info
.info dummy
.format csv

.run ../common/postgresql_setup.sql
.info behavior
.info behaviors
.info client
.info feature
.info features
.info function
.info functions
.info keyword
.info keywords
.info limit
.info limits
.info table
.info user
.close

.run ../common/mysql_setup.sql
.open sqlite::memory:
.info catalog
.info catalogs
.info schema
.info schemas
.close

.run ../common/sqlite_setup.sql
.open sqlite::memory:
.info server
.info type
.info types
.close

