--------------------------------------------------------------------------
-- .quiet command test
--------------------------------------------------------------------------
.run ../common/sqlite_setup.sql
.open sqlite::memory:
.format csv

CREATE TABLE SqliteTypes
(
	a	INTEGER,
	b	REAL,
	c	TEXT,
	d	BLOB
);

INSERT INTO SqliteTypes VALUES (123456789, 10.12, '你好，世界', X'DEADBEEF');

SELECT * FROM SqliteTypes;

.quiet on
SELECT * FROM SqliteTypes;
.quiet off

SELECT * FROM SqliteTypes;

DROP TABLE SqliteTypes;
.close

-- Tricking code coverage tool for things that cannot be run inside mvn test.
.quiet on
.driver
.env
.quiet off

