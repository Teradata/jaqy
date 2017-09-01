--------------------------------------------------------------------------
-- .pwd command test
--------------------------------------------------------------------------
.help pwd
.pwd

.run ../common/postgresql_setup.sql
.pwd
.close

.run ../common/mysql_setup.sql
.pwd
USE vagrant;
.pwd
.close

.run ../common/derby_setup.sql
.open derby:memory:pwdDB;create=true
.pwd
.close

.run ../common/sqlite_setup.sql
.open sqlite::memory:
.pwd
.close
