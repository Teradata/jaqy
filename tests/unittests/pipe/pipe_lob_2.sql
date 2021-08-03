--------------------------------------------------------------------------
-- test pipe
--------------------------------------------------------------------------

-- artificially sets the copy threshold to be very small for testing purposes.
.set lobcachesize 4

.run ../common/mysql_setup.sql
USE vagrant;

CREATE TABLE LobTable
(
	a  INTEGER,
	c1 VARCHAR(10000),
	c2 VARBINARY(10000)
);

INSERT INTO LobTable VALUES (1, '1', X'de');
INSERT INTO LobTable VALUES (2, '12', X'dead');
INSERT INTO LobTable VALUES (3, '123', X'deadbe');
INSERT INTO LobTable VALUES (4, '1234', X'deadbeef');
INSERT INTO LobTable VALUES (5, '12345', X'deadbeefde');
INSERT INTO LobTable VALUES (6, '123456', X'deadbeefdead');
INSERT INTO LobTable VALUES (7, '1234567', X'deadbeefdeadbe');
INSERT INTO LobTable VALUES (8, '12345678', X'deadbeefdeadbeef');
INSERT INTO LobTable VALUES (9, NULL, NULL);

.session new
.run ../common/derby_setup.sql
.open derby:memory:pipeDB;create=true

CREATE TABLE LobTable
(
	a  INTEGER,
	c1 CLOB(1K),
	c2 BLOB(1K)
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

.session 0
DROP TABLE LobTable;
.close
