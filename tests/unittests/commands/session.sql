--------------------------------------------------------------------------
-- session command test
--------------------------------------------------------------------------
.run ../common/sqlite_setup.sql
.help session

-- session 0
.open sqlite::memory:

CREATE TABLE MyTable (a INTEGER, b INTEGER);
INSERT INTO MyTable VALUES (0, 0);
SELECT * FROM MyTable ORDER BY a;

-- session 1
.session new
.open sqlite::memory:

CREATE TABLE MyTable (a INTEGER, b INTEGER);
INSERT INTO MyTable VALUES (1, 1);
SELECT * FROM MyTable ORDER BY a;

-- list current sessions
.session
-- switch session
.session 0
SELECT * FROM MyTable ORDER BY a;
.close

-- switch session
.session 1
SELECT * FROM MyTable ORDER BY a;
.close
