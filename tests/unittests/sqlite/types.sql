--------------------------------------------------------------------------
-- test SQLite specific types
-- See https://www.sqlite.org/datatype3.html
--------------------------------------------------------------------------
.run ../common/sqlite_setup.sql
.open sqlite::memory:

-- Null Type
.debug resultset on
SELECT NULL AS Test;
.debug resultset off

CREATE TABLE SqliteTypes
(
	a	INTEGER,
	b	REAL,
	c	TEXT,
	d	BLOB
);

INSERT INTO SqliteTypes VALUES (123456789, 10.12, '你好，世界', X'DEADBEEF');

.debug resultset on
SELECT * FROM SqliteTypes;
.debug resultset off

DROP TABLE SqliteTypes;
.close

