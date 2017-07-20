--------------------------------------------------------------------------
-- test SQL types
--------------------------------------------------------------------------
.run ../common/mysql_setup.sql
--------------------------------------------------------------------------
-- SQL92 types
--------------------------------------------------------------------------

-- Bit (SQL 99 removed it)

-- bit string literal
SELECT B'1100' AS Test;
SELECT CAST(B'1100' AS BIT(16)) AS Test;
SELECT CAST(B'1100' AS BIT VARYING(16)) AS Test;

-- Numerical types
SELECT CAST(12 AS SMALLINT) AS Test;
SELECT CAST(123456789 AS INTEGER) AS Test;
SELECT CAST(123456789 AS NUMERIC) AS Test;
SELECT CAST(-1234567.89 AS DEC(10,2)) AS Test;
SELECT CAST(-1234567.89 AS DECIMAL(10,2)) AS Test;
SELECT CAST(-1234567.89 AS DECIMAL) AS Test;

SELECT CAST(-1234567.89 AS FLOAT) AS Test;
SELECT CAST(-1234567.89 AS FLOAT(2)) AS Test;
SELECT CAST(-1234567.89 AS REAL) AS Test;
SELECT CAST(-1234567.89 AS DOUBLE PRECISION) AS Test;

-- Characters

-- character string literal
SELECT 'A quick brown fox' AS Test;
-- natioinal character string literal
SELECT N'A quick brown fox' AS Test;
-- hex string literal
SELECT X'DEADBEEF' AS Test;

SELECT CAST('A quick brown fox' AS CHARACTER(20)) AS Test;
SELECT CAST('A quick brown fox' AS CHAR(20)) AS Test;

SELECT CAST('A quick brown fox' AS CHARACTER VARYING(20)) AS Test;
SELECT CAST('A quick brown fox' AS CHAR VARYING(20)) AS Test;
SELECT CAST('A quick brown fox' AS VARCHAR(20)) AS Test;

SELECT CAST('A quick brown fox' AS NATIONAL CHARACTER(20)) AS Test;
SELECT CAST('A quick brown fox' AS NATIONAL CHAR(20)) AS Test;
SELECT CAST('A quick brown fox' AS NCHAR(20)) AS Test;

SELECT CAST('A quick brown fox' AS NATIONAL CHARACTER VARYING(20)) AS Test;
SELECT CAST('A quick brown fox' AS NATIONAL CHAR VARYING(20)) AS Test;
SELECT CAST('A quick brown fox' AS NCHAR VARYING(20)) AS Test;

SELECT '你好，世界' AS Test;

-- Date/Time/Timestamp
SELECT CAST('2001-02-03' AS DATE) AS Test;
SELECT CAST('12:03:04' AS TIME) AS Test;
-- SELECT CAST('12:03:04.12' AS TIME(2)) AS Test;
-- SELECT CAST('12:03:04-08:00' AS TIME WITH TIME ZONE) AS Test;
-- SELECT CAST('12:03:04.12-08:00' AS TIME(2) WITH TIME ZONE) AS Test;
-- SELECT CAST('2001-02-03 12:03:04' AS TIMESTAMP) AS Test;
-- SELECT CAST('2001-02-03 12:03:04.12' AS TIMESTAMP(2)) AS Test;
-- SELECT CAST('2001-02-03 12:03:04-08:00' AS TIMESTAMP WITH TIME ZONE) AS Test;
-- SELECT CAST('2001-02-03 12:03:04.12-08:00' AS TIMESTAMP(2) WITH TIME ZONE) AS Test;

--------------------------------------------------------------------------
-- SQL99 types
--------------------------------------------------------------------------

-- Boolean type
SELECT TRUE AS Test;
SELECT FALSE AS Test;

-- Distinct Types
CREATE TYPE MyInt AS INTEGER FINAL;

SELECT CAST(123 AS MyINT) AS Test;

DROP TYPE MyInt;

--------------------------------------------------------------------------
-- SQL2003 types
--------------------------------------------------------------------------

-- XML
-- SELECT CAST('<a>123</a>' AS XML) AS Test;
-- SELECT XMLELEMENT(NAME 'Customer', XMLATTRIBUTES('123' AS id), XMLFOREST('Joe' AS Name)) AS Test;

--------------------------------------------------------------------------
-- ST_GEOMETRY
--------------------------------------------------------------------------
SELECT CAST('POINT(1 1)' AS ST_GEOMETRY) AS Test;
SELECT CAST('POINT(1 1)' AS ST_GEOMETRY).ST_MBR () AS Test;
