--------------------------------------------------------------------------
-- Transaction test
--------------------------------------------------------------------------
.run ../common/postgresql_setup.sql

CREATE TABLE JsonTable
(
	a	INTEGER PRIMARY KEY,
	b	VARCHAR(200),
	j	JSON
);

BEGIN;
INSERT INTO JsonTable VALUES (1, '1','{"c":1,"d":1}');
INSERT INTO JsonTable VALUES (2, '2',null);
INSERT INTO JsonTable VALUES (3, null,'{"c":3,"d":[1,2,null]}');
INSERT INTO JsonTable VALUES (4, null,'{}');
INSERT INTO JsonTable VALUES (5, '5','[]');
COMMIT;

.format json
SELECT * FROM JsonTable ORDER BY a, b;

.export json test.json
SELECT * FROM JsonTable;

DELETE FROM JsonTable;

-- We need to cast text to JSON, since the PostgreSQL JDBC driver is a
-- bit overly strict in type matching text and JSON type.
.import json test.json
INSERT INTO JsonTable VALUES ({{a}}, {{b}}, {{j}}::json);

SELECT * FROM JsonTable  ORDER BY a, b;

DROP TABLE JsonTable;

.os rm -f test.json
.close

