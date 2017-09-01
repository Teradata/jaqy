--------------------------------------------------------------------------
-- .schema command test
--------------------------------------------------------------------------
.help schema
.schema

.run ../common/sqlite_setup.sql
.open sqlite::memory:

CREATE TABLE SqliteTypes
(
	a	INTEGER,
	b	REAL,
	c	TEXT,
	d	BLOB
);
.schema
.schema dummy
.schema SqliteTypes

CREATE TABLE SqliteTypes2
(
        a       INTEGER,
        b       DECIMAL(10,2),
        c       VARCHAR(100),
        d       BLOB(100)
);
.schema SqliteTypes2

DROP TABLE SqliteTypes;
.close

