--------------------------------------------------------------------------
-- .wasb command test
--------------------------------------------------------------------------
.help wasb
.wasb
.wasb asdf

.wasb endpoint
.wasb endpoint http://127.0.0.1:10000/devstoreaccount1
.wasb account
.wasb account devstoreaccount1
.wasb key Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==

.run ../common/sqlite_setup.sql
.open sqlite::memory:
.format csv

CREATE TABLE MyTable(a INTEGER PRIMARY KEY, b VARCHAR(200), c VARCHAR(200));

INSERT INTO MyTable VALUES (1, 'abc', 'def');
INSERT INTO MyTable VALUES (2, 'john', 'doe');
INSERT INTO MyTable VALUES (3, 'a"b', 'c"d');
INSERT INTO MyTable VALUES (4, 'a,b', 'c,d');
INSERT INTO MyTable VALUES (5, 'a''b', 'c''d');
INSERT INTO MyTable VALUES (6, 'a''",b', 'c''",d');
INSERT INTO MyTable VALUES (7, 'a       b', 'c,d');

.wasb create testcontainer
.wasb create
.wasb create A#!@#$

.export csv -c utf-8 wasb://testcontainer@/file1.csv
SELECT * FROM MyTable ORDER BY a;

.wasb list testcontainer
.wasb list
.wasb list asdf

DELETE FROM MyTable;
.import csv -h wasb://testcontainer@/file1.csv
INSERT INTO MyTable VALUES ({{a}}, {{b}}, {{c}});
SELECT * FROM MyTable ORDER BY a;

.wasb remove wasb://testcontainer@/file1.csv
.wasb remove
.wasb remove wasb://asdf@/
.wasb remove wasb://testcontainer@/
.wasb remove wasb://testcontainer@/file2.csv
.wasb remove adl://testcontainer@/file1.csv

.wasb delete testcontainer
.wasb delete asdf
.wasb delete
.wasb delete A#@%
.close
