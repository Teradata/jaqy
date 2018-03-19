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
INSERT INTO MyTable VALUES (13, CAST(X'de' AS BLOB), '1');
INSERT INTO MyTable VALUES (14, CAST(X'de' AS BLOB), '1');
INSERT INTO MyTable VALUES (15, CAST(X'dead' AS BLOB), '1');
INSERT INTO MyTable VALUES (16, CAST(X'dead' AS BLOB), '2');
INSERT INTO MyTable VALUES (17, CAST(X'dead' AS BLOB), '12');
INSERT INTO MyTable VALUES (18, CAST(X'dead' AS BLOB), '123');
INSERT INTO MyTable VALUES (19, CAST(X'dead' AS BLOB), '223');
INSERT INTO MyTable VALUES (20, CAST(X'dead' AS BLOB), '1234');
INSERT INTO MyTable VALUES (21, CAST(X'dead' AS BLOB), '12345');
INSERT INTO MyTable VALUES (22, CAST(X'dead' AS BLOB), '12');
INSERT INTO MyTable VALUES (23, CAST(X'dead' AS BLOB), '123');
INSERT INTO MyTable VALUES (24, CAST(X'deadbe' AS BLOB), '1');
INSERT INTO MyTable VALUES (25, CAST(X'deadbe' AS BLOB), '2');
INSERT INTO MyTable VALUES (26, CAST(X'deadef' AS BLOB), '1');
INSERT INTO MyTable VALUES (27, CAST(X'deadef' AS BLOB), '2');
INSERT INTO MyTable VALUES (28, CAST(X'deadefbe' AS BLOB), '1');
INSERT INTO MyTable VALUES (29, CAST(X'deadefbe' AS BLOB), '2');

-- artificially sets the cache size to be very small for testing purposes.
.script
interpreter.setCacheSize (3);
.end script

.sort 3 desc, 2 asc, 1 asc
SELECT * FROM MyTable ORDER BY a;

.set nullsort low
.sort 3 desc, 2 asc, 1 asc
SELECT * FROM MyTable ORDER BY a;

.set nullsort high
.sort 3 desc, 2 asc, 1 asc
SELECT * FROM MyTable ORDER BY a;

.set nullsort low
.sort c desc, b desc, 1 asc
SELECT * FROM MyTable ORDER BY a;

.sort 2, 3, 1
SELECT * FROM MyTable ORDER BY a;

.set nullsort high
.sort 2, 3, 1
SELECT * FROM MyTable ORDER BY a;

DROP TABLE MyTable;

.close
