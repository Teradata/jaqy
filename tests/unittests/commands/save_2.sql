--------------------------------------------------------------------------
-- .save command test
--------------------------------------------------------------------------
.run ../common/postgresql_setup.sql

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
.project table_schem, table_name
.save
.list

.repeat ${save.size()}
DROP TABLE "${save.get (iteration, 1)}"."${save.get (iteration, 2)}";

-- verify t2, t3, and t4 were dropped.
.list

.close
