--------------------------------------------------------------------------
-- CSV unicode test
--------------------------------------------------------------------------
.run ../common/sqlite_setup.sql
.open sqlite::memory:

.import csv -h ../common/data/unicode_1.csv
.importtable unicodeTable1

.desc unicodeTable1
SELECT * FROM unicodeTable1 ORDER BY 1;
DROP TABLE unicodeTable1;

.import csv -h ../common/data/unicode_2.csv
.importtable unicodeTable2

.desc unicodeTable2
SELECT * FROM unicodeTable2 ORDER BY 1;
DROP TABLE unicodeTable2;

.import csv -h ../common/data/unicode_3.csv
.importtable unicodeTable3

.desc unicodeTable3
SELECT * FROM unicodeTable3 ORDER BY 1;
DROP TABLE unicodeTable3;

.quit

