--------------------------------------------------------------------------
-- test SQL types
--------------------------------------------------------------------------
.run ../common/derby_setup.sql
.open derby:memory:ansiDB;create=true
-- Because Apache Derby requires a table to be present in SELECT
-- statements.  This test was modified a bit.

CREATE TABLE MyTable (a INTEGER, b INTEGER);
INSERT INTO MyTable VALUES (1, 1);

--------------------------------------------------------------------------
-- SQL92 types
--------------------------------------------------------------------------

-- Bit (SQL 99 removed it)

-- bit string literal
SELECT B'1100' AS Test FROM MyTable;
SELECT CAST(B'1100' AS BIT(16)) AS Test FROM MyTable;
SELECT CAST(B'1100' AS BIT VARYING(16)) AS Test FROM MyTable;

-- Numerical types
SELECT CAST(12 AS SMALLINT) AS Test FROM MyTable;
SELECT CAST(123456789 AS INTEGER) AS Test FROM MyTable;
SELECT CAST(123456789 AS NUMERIC) AS Test FROM MyTable;
SELECT CAST(-1234567.89 AS DEC(10,2)) AS Test FROM MyTable;
SELECT CAST(-1234567.89 AS DECIMAL(10,2)) AS Test FROM MyTable;
SELECT CAST(-1234567.89 AS DECIMAL) AS Test FROM MyTable;

SELECT CAST(-1234567.89 AS FLOAT) AS Test FROM MyTable;
SELECT CAST(-1234567.89 AS FLOAT(2)) AS Test FROM MyTable;
SELECT CAST(-1234567.89 AS REAL) AS Test FROM MyTable;
SELECT CAST(-1234567.89 AS DOUBLE PRECISION) AS Test FROM MyTable;

-- Characters

-- character string literal
SELECT 'A quick brown fox' AS Test FROM MyTable;
-- natioinal character string literal
SELECT N'A quick brown fox' AS Test FROM MyTable;
-- hex string literal
SELECT X'DEADBEEF' AS Test FROM MyTable;

SELECT CAST('A quick brown fox' AS CHARACTER(20)) AS Test FROM MyTable;
SELECT CAST('A quick brown fox' AS CHAR(20)) AS Test FROM MyTable;

SELECT CAST('A quick brown fox' AS CHARACTER VARYING(20)) AS Test FROM MyTable;
SELECT CAST('A quick brown fox' AS CHAR VARYING(20)) AS Test FROM MyTable;
SELECT CAST('A quick brown fox' AS VARCHAR(20)) AS Test FROM MyTable;

SELECT CAST('A quick brown fox' AS NATIONAL CHARACTER(20)) AS Test FROM MyTable;
SELECT CAST('A quick brown fox' AS NATIONAL CHAR(20)) AS Test FROM MyTable;
SELECT CAST('A quick brown fox' AS NCHAR(20)) AS Test FROM MyTable;

SELECT CAST('A quick brown fox' AS NATIONAL CHARACTER VARYING(20)) AS Test FROM MyTable;
SELECT CAST('A quick brown fox' AS NATIONAL CHAR VARYING(20)) AS Test FROM MyTable;
SELECT CAST('A quick brown fox' AS NCHAR VARYING(20)) AS Test FROM MyTable;

SELECT '你好，世界' AS Test FROM MyTable;

-- Date/Time/Timestamp
SELECT CAST('2001-02-03' AS DATE) AS Test FROM MyTable;
SELECT CAST('12:03:04' AS TIME) AS Test FROM MyTable;
SELECT CAST('12:03:04.12' AS TIME(2)) AS Test FROM MyTable;
SELECT CAST('12:03:04-08:00' AS TIME WITH TIME ZONE) AS Test FROM MyTable;
SELECT CAST('12:03:04.12-08:00' AS TIME(2) WITH TIME ZONE) AS Test FROM MyTable;
SELECT CAST('2001-02-03 12:03:04' AS TIMESTAMP) AS Test FROM MyTable;
SELECT CAST('2001-02-03 12:03:04.12' AS TIMESTAMP(2)) AS Test FROM MyTable;
SELECT CAST('2001-02-03 12:03:04-08:00' AS TIMESTAMP WITH TIME ZONE) AS Test FROM MyTable;
SELECT CAST('2001-02-03 12:03:04.12-08:00' AS TIMESTAMP(2) WITH TIME ZONE) AS Test FROM MyTable;

--------------------------------------------------------------------------
-- SQL99 types
--------------------------------------------------------------------------

-- Boolean type
SELECT TRUE AS Test FROM MyTable;
SELECT FALSE AS Test FROM MyTable;

-- Distinct Types
CREATE TYPE MyInt AS INTEGER FINAL;

SELECT CAST(123 AS MyINT) AS Test FROM MyTable;

DROP TYPE MyInt;

--------------------------------------------------------------------------
-- SQL2003 types
--------------------------------------------------------------------------

-- XML
SELECT CAST('<a>123</a>' AS XML) AS Test FROM MyTable;
SELECT XMLELEMENT(NAME 'Customer', XMLATTRIBUTES('123' AS id), XMLFOREST('Joe' AS Name)) AS Test FROM MyTable;

--------------------------------------------------------------------------
-- ST_GEOMETRY
--------------------------------------------------------------------------
SELECT CAST('POINT(1 1)' AS ST_GEOMETRY) AS Test FROM MyTable;
SELECT CAST('POINT(1 1)' AS ST_GEOMETRY).ST_MBR () AS Test FROM MyTable;

DROP TABLE MyTable;
.close

