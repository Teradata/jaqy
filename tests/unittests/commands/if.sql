--------------------------------------------------------------------------
-- .if command test
--------------------------------------------------------------------------
.help if

.run ../common/sqlite_setup.sql
.open sqlite::memory:

CREATE TABLE MyTable (a INTEGER, b INTEGER);

-- basic if test
SELECT * FROM MyTable ORDER BY a;
.if activityCount == 0
.rem Test
SELECT 1;
.rem Test
.end if

INSERT INTO MyTable VALUES (1, 1);

SELECT * FROM MyTable ORDER BY a;
.if activityCount == 0
.rem Test
SELECT 2;
.rem Test
.end if

-- nested ifs

SELECT 3 AS Test;
.if activityCount == 1
.if activityCount == 0
SELECT 4;
.end if
.if activityCount == 1
SELECT 5;
.end if
.end if

SELECT 6;

-- invalid if condition
.if asdf + asdf
SELECT 7;
.end if

.if activityCount
SELECT 8;
.end if

DROP TABLE MyTable;

.close
