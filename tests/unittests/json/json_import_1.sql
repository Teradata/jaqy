--------------------------------------------------------------------------
-- .import json command test
--------------------------------------------------------------------------
.run ../common/derby_setup.sql
.help import
.import

.open derby:memory:myDB;create=true

CREATE TABLE MyTable(a INTEGER PRIMARY KEY, b INTEGER);

.debug preparedstatement on

.import json lib/import1.json
INSERT INTO MyTable VALUES ({{a}}, {{b}});

SELECT * FROM MyTable ORDER BY a;

DELETE FROM MyTable;

.import json -a -f bson lib/import1.bson
INSERT INTO MyTable VALUES ({{a}}, {{b}});

SELECT * FROM MyTable ORDER BY a;

DELETE FROM MyTable;
