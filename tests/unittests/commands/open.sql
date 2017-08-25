--------------------------------------------------------------------------
-- .open command test
--------------------------------------------------------------------------
.help open
.open

.run ../common/sqlite_setup.sql
.open sqlite::memory:
.open sqlite::memory:
.close

.open dummy::memory:
.open -u dbc -p dbc teradata://dummy
.open -Duser=dbc teradata://dummy

.protocol dummy com.dummy.Driver
.classpath dummy /root/dummyjdbc.jar
.open dummy::memory:

