--------------------------------------------------------------------------
-- .import csv command test
--------------------------------------------------------------------------
.run ../common/derby_setup.sql
.help import
.import

.open derby:memory:csvDB;create=true

-- VARCHAR type
CREATE TABLE MyTable(a VARCHAR(200) PRIMARY KEY, b VARCHAR(200));

.debug preparedstatement on

-- test csv with header
.import csv -h on lib/import1.csv
INSERT INTO MyTable VALUES ({{a}}, {{b}});

SELECT * FROM MyTable ORDER BY a;
DELETE FROM MyTable;

.import csv -h on lib/import1.csv
INSERT INTO MyTable VALUES ({{a}}, {{c}});

SELECT * FROM MyTable ORDER BY a;
DELETE FROM MyTable;

-- test csv without header
.import csv -h off lib/import2.csv
INSERT INTO MyTable VALUES ({{col1}}, {{col2}});

SELECT * FROM MyTable ORDER BY a;
DELETE FROM MyTable;

DROP TABLE MyTable;

-- test csv with more columns
DROP TABLE MyTable;

CREATE TABLE MyTable(a VARCHAR(200) PRIMARY KEY, b VARCHAR(200), c INTEGER);

.import csv -h off lib/import2.csv
INSERT INTO MyTable VALUES ({{col1}}, {{col2}}, {{col3}});

SELECT * FROM MyTable ORDER BY a;
DELETE FROM MyTable;

DROP TABLE MyTable;
.close

