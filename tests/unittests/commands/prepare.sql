--------------------------------------------------------------------------
-- .prepare command test
--------------------------------------------------------------------------
.run ../common/sqlite_setup.sql
.help .prepare

.open sqlite::memory:

CREATE TABLE MyTable (a INTEGER, b INTEGER);

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

.exit
