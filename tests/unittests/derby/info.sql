--------------------------------------------------------------------------
-- .info command test
--------------------------------------------------------------------------
.run ../common/derby_setup.sql
.open derby:memory:typesDB;create=true
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

