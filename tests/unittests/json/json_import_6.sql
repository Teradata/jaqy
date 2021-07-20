--------------------------------------------------------------------------
-- .import json command test
--------------------------------------------------------------------------
.run ../common/derby_setup.sql
.open derby:memory:jsonDB;create=true

CREATE TABLE MyTable(a VARCHAR(100));

.import json data/import3.json
INSERT INTO MyTable VALUES ({{}});

SELECT * FROM MyTable ORDER BY 1;

DROP TABLE MyTable;
.close
