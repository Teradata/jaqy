--------------------------------------------------------------------------
-- .debug resultset command test
--------------------------------------------------------------------------
.run ../common/sqlite_setup.sql
.help .debug
.debug

.open sqlite::memory:

CREATE TABLE MyTable (a INTEGER, b INTEGER);
INSERT INTO MyTable VALUES (1, 1);

SELECT a AS Test, b FROM MyTable ORDER BY a;

.debug resultset on
.debug
SELECT a AS Test, b FROM MyTable ORDER BY a;

.debug resultset off
.debug
SELECT a AS Test, b FROM MyTable ORDER BY a;

.exit
