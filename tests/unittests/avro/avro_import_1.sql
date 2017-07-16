--------------------------------------------------------------------------
-- .import json command test
--------------------------------------------------------------------------
.run ../common/derby_setup.sql

.open derby:memory:avroDB;create=true
.format csv

CREATE TABLE MyTable(a INTEGER PRIMARY KEY, b VARCHAR(200) NOT NULL, c VARCHAR(200), d VARCHAR(200) FOR BIT DATA);

.debug preparedstatement on

.import avro lib/file1.avro
INSERT INTO MyTable VALUES ({{a}}, {{b}}, {{c}}, {{d}});
SELECT * FROM MyTable ORDER BY a;
DELETE FROM MyTable;

.import avro lib/file2.avro
INSERT INTO MyTable VALUES ({{a}}, {{b}}, {{c}}, {{d}});
SELECT * FROM MyTable ORDER BY a;
DELETE FROM MyTable;

.import avro lib/file3.avro
INSERT INTO MyTable VALUES ({{a}}, {{b}}, {{c}}, {{d}});
SELECT * FROM MyTable ORDER BY a;
DELETE FROM MyTable;

.import avro lib/file4.avro
INSERT INTO MyTable VALUES ({{a}}, {{b}}, {{c}}, {{d}});
SELECT * FROM MyTable ORDER BY a;
DELETE FROM MyTable;

.import avro lib/file5.avro
INSERT INTO MyTable VALUES ({{a}}, {{b}}, {{c}}, {{d}});
SELECT * FROM MyTable ORDER BY a;
DELETE FROM MyTable;

.import avro lib/file6.avro
INSERT INTO MyTable VALUES ({{a}}, {{b}}, {{c}}, {{d}});
SELECT * FROM MyTable ORDER BY a;
DELETE FROM MyTable;

DROP TABLE MyTable;
.close

