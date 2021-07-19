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

-- drop t1 if it exists
.list . . t1
.if activityCount > 0
DROP TABLE t1;
.end if

-- same query again.  DROP statement is skipped.
.list . . t1
.if activityCount > 0
DROP TABLE t1;
.end if

-- drop all the remaining tables
.project TABLE_CAT, TABLE_NAME
.save
.list

.repeat ${save.size()}
DROP TABLE `${save.get (iteration, 1)}`.`${save.get (iteration, 2)}`;

-- verify t2, t3, and t4 were dropped.
.list

-- In case they are not dropped, drop these tables.
DROP TABLE t1;
DROP TABLE t2;
DROP TABLE t3;
DROP TABLE t4;

.close
