--------------------------------------------------------------------------
-- .import json command test
--------------------------------------------------------------------------
.run ../common/derby_setup.sql
.open derby:memory:jsonDB;create=true

CREATE TABLE MyTable(a INTEGER PRIMARY KEY, b INTEGER);

.debug preparedstatement on

.import dummy
.import json
.import json -f dummy
.import json -b dummy

.import json data/import1.json
.import
.importschema
INSERT INTO MyTable VALUES ({{a}}, {{b}});

SELECT * FROM MyTable ORDER BY a;

DELETE FROM MyTable;

.import json -f text -c dummy data/import1.json

.import json -f text data/import1.json
INSERT INTO MyTable VALUES ({{a}}, {{b}});

SELECT * FROM MyTable ORDER BY a;

DELETE FROM MyTable;

.import json -a -f bson data/import1.bson
INSERT INTO MyTable VALUES ({{a}}, {{b}});

SELECT * FROM MyTable ORDER BY a;

DROP TABLE MyTable;

CREATE TABLE MyTable(a INTEGER PRIMARY KEY, b INTEGER, c INTEGER);

.import json -a -f bson data/import1.bson
INSERT INTO MyTable VALUES ({{a}}, {{b}}, {{c}});

SELECT * FROM MyTable ORDER BY a;
DROP TABLE MyTable;

.close

