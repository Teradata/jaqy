--------------------------------------------------------------------------
-- .sort command test
--------------------------------------------------------------------------
.run ../common/derby_setup.sql
.open derby:memory:sortDB;create=true
CREATE TABLE MyTable (a INTEGER, b BLOB, c CLOB);

INSERT INTO MyTable VALUES (1, CAST(X'dead' AS BLOB), 'A quick brown');
INSERT INTO MyTable VALUES (2, CAST(X'deadbeef' AS BLOB), 'A quick brown');
INSERT INTO MyTable VALUES (3, CAST(X'facefeed' AS BLOB), 'A quick brown fox');
INSERT INTO MyTable VALUES (4, CAST(X'face' AS BLOB), 'A quick brown fox');
INSERT INTO MyTable VALUES (5, CAST(X'deadbeef' AS BLOB), 'A quick brown');
INSERT INTO MyTable VALUES (6, CAST(X'dead' AS BLOB), 'A quick brown');
INSERT INTO MyTable VALUES (7, CAST(X'facefeed' AS BLOB), 'A quick brown fox');
INSERT INTO MyTable VALUES (8, CAST(X'face' AS BLOB), 'A quick brown fox');
INSERT INTO MyTable VALUES (9, CAST(X'facefeed' AS BLOB), null);
INSERT INTO MyTable VALUES (10, CAST(X'facefeed' AS BLOB), null);
INSERT INTO MyTable VALUES (11, null, 'A quick brown fox');
INSERT INTO MyTable VALUES (12, null, null);

.sort -d 3 -a 2 -a 1
SELECT * FROM MyTable ORDER BY a;

.sort -l -d 3 -a 2 -a 1
SELECT * FROM MyTable ORDER BY a;

.sort -h -d 3 -a 2 -a 1
SELECT * FROM MyTable ORDER BY a;

.sort -d=c -d=b -a 1
SELECT * FROM MyTable ORDER BY a;

.sort -l -a 2 -a 3 -a 1
SELECT * FROM MyTable ORDER BY a;

.sort -h -a 2 -a 3 -a 1
SELECT * FROM MyTable ORDER BY a;

DROP TABLE MyTable;

.close
