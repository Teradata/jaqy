--------------------------------------------------------------------------
-- .import json command test
--------------------------------------------------------------------------
.run ../common/derby_setup.sql
.open derby:memory:jsonDB;create=true

CREATE TABLE MyTable(po INTEGER PRIMARY KEY, customerId INTEGER, date VARCHAR(20), date2 VARCHAR(20), row VARCHAR(100));

.import json -r=customers lib/import2.json
INSERT INTO MyTable VALUES ({{purchase.po}}, {{customerId}}, {{date}}, {{date}}, {{}});

SELECT * FROM MyTable ORDER BY 1, 2, 3;

DROP TABLE MyTable;
.close
