--------------------------------------------------------------------------
-- .import json command test
--------------------------------------------------------------------------
.run ../common/derby_setup.sql
.open derby:memory:jsonDB;create=true

CREATE TABLE MyTable(a INTEGER PRIMARY KEY, b INTEGER);

.import json data/import1.json
INSERT INTO MyTable VALUES (?, ?);

.import json data/bad1.json
INSERT INTO MyTable VALUES ({{a}}, {{b}});

DROP TABLE MyTable;
.close

