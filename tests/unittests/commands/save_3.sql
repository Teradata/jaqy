--------------------------------------------------------------------------
-- .save command test
--------------------------------------------------------------------------
.run ../common/mysql_setup.sql
USE vagrant;

-- setup
CREATE TABLE t1 (a INT);
CREATE TABLE t2 (a INT);
CREATE TABLE t3 (a INT);
CREATE TABLE t4 (a INT);

-- create an alias that drop a table if it exists in the current path
.alias dropifexists
.list . . $(0)
.if activityCount > 0
DROP TABLE $(0);
.end if
.end alias

.dropifexists t1
.dropifexists t1

-- create an alias that drop all the tables in the current path
.alias dropall
.project TABLE_CAT, TABLE_NAME
.save
.list
.repeat ${save.size()}
DROP TABLE `${save.get (iteration, 1)}`.`${save.get (iteration, 2)}`;
.end alias

.dropall

-- verify t2, t3, and t4 were dropped.
.list

.close
