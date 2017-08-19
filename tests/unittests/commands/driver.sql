--------------------------------------------------------------------------
-- .driver command test
--------------------------------------------------------------------------
.help driver
.driver

.run ../common/sqlite_setup.sql
.open sqlite::memory:
.close

.diver

.run ../common/mysql_setup.sql
.close

.driver

.run ../common/postgresql_setup.sql
.close

.driver

