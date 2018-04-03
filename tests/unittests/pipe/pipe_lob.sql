--------------------------------------------------------------------------
-- test pipe
--------------------------------------------------------------------------

-- artificially sets the copy threshold to be very small for testing purposes.
.set lobcachesize 4

.run ../common/derby_setup.sql
.open derby:memory:pipeDB;create=true
CREATE TABLE LobTable
(
	a  INTEGER,
	c1 CLOB(1K),
	c2 BLOB(1K)
);

INSERT INTO LobTable VALUES (1, '1', CAST(X'de' AS BLOB));
INSERT INTO LobTable VALUES (2, '12', CAST(X'dead' AS BLOB));
INSERT INTO LobTable VALUES (3, '123', CAST(X'deadbe' AS BLOB));
INSERT INTO LobTable VALUES (4, '1234', CAST(X'deadbeef' AS BLOB));
INSERT INTO LobTable VALUES (5, '12345', CAST(X'deadbeefde' AS BLOB));
INSERT INTO LobTable VALUES (6, '123456', CAST(X'deadbeefdead' AS BLOB));
INSERT INTO LobTable VALUES (7, '1234567', CAST(X'deadbeefdeadbe' AS BLOB));
INSERT INTO LobTable VALUES (8, '12345678', CAST(X'deadbeefdeadbeef' AS BLOB));
INSERT INTO LobTable VALUES (9, NULL, NULL);

.session new
.run ../common/mysql_setup.sql
USE vagrant;

-- MySQL Test
.session new
.run ../common/mysql_setup.sql
USE vagrant;
CREATE TABLE LobTable
(
	a  INTEGER,
	c1 VARCHAR(10000),
	c2 VARBINARY(10000)
);

.session 0

.export pipe
.set fetchsize 2
SELECT * FROM LobTable ORDER BY a;

.session 1
.import pipe
.set batchsize 2
INSERT INTO LobTable VALUES (?, ?, ?);

SELECT * FROM LobTable ORDER BY a;

DROP TABLE LobTable;
.close

-- SQLite Test
.run ../common/sqlite_setup.sql
.open sqlite::memory:

CREATE TABLE LobTable
(
	a  INTEGER,
	c1 TEXT,
	c2 BLOB
);

.session 0

.export pipe
.set fetchsize 2
SELECT * FROM LobTable ORDER BY a;

.session 1
.import pipe
.import
.debug preparedstatement on
.set batchsize 3000
INSERT INTO LobTable VALUES (?, ?, ?);

SELECT * FROM LobTable ORDER BY a;

DROP TABLE LobTable;
.close

.session 0
DROP TABLE LobTable;
.close
