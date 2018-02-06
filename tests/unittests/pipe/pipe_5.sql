--------------------------------------------------------------------------
-- test pipe
--------------------------------------------------------------------------

-- artificially sets the copy threshold to be very small for testing purposes.
.script
interpreter.setCopyThreshold (4);
.end script

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

CREATE TABLE LobTable
(
	a  INTEGER,
	c1 MEDIUMTEXT,
	c2 BLOB
);

.session 0

.export pipe
SELECT * FROM LobTable ORDER BY a;

.session 1

.import pipe
.batchsize 2
INSERT INTO LobTable VALUES ({{A}}, {{C1}}, {{C2}});

SELECT COUNT(*) FROM LobTable;

SELECT * FROM LobTable ORDER BY a;

.session 0
DROP TABLE LobTable;
.close
.session 1
DROP TABLE LobTable;
.close
