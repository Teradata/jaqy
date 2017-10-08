--------------------------------------------------------------------------
-- .debug preparedstatement on command test
--------------------------------------------------------------------------
.run ../common/derby_setup.sql
.help .debug
.debug

.open derby:memory:debugDB;create=true

CREATE TABLE MyTable (a INTEGER, b INTEGER);

.debug preparedstatement on
.debug resultset on
.debug

.prepare
INSERT INTO MyTable VALUES (1, 1);

.prepare
INSERT INTO MyTable VALUES (?, ?);

.prepare
SELECT a AS Test, b FROM MyTable ORDER BY a;

.prepare
SELECT a AS Test, b FROM MyTable WHERE a = ?;

-- Testing ambiguous parameters
-- The support for ambiguous parameters vary greatly.
.prepare
SELECT ? AS Test FROM MyTable;

.prepare
SELECT CAST(? AS VARCHAR(200)) AS Test FROM MyTable;

.prepare
SELECT 1 AS Test FROM MyTable WHERE ? IS NOT NULL;

DROP TABLE MyTable;
.close

