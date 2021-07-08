--------------------------------------------------------------------------
-- .import csv command test
--------------------------------------------------------------------------
.run ../common/postgresql_setup.sql

.import csv -h lib/utf8.csv
.importtable tableutf8
SELECT * FROM tableutf8 ORDER BY 1;
DROP TABLE tableutf8;

.import csv -h lib/utf16le.csv
.importtable tableutf16le
SELECT * FROM tableutf16le ORDER BY 1;
DROP TABLE tableutf16le;

.import csv -h lib/utf16be.csv
.importtable tableutf16be
SELECT * FROM tableutf16be ORDER BY 1;
DROP TABLE tableutf16be;

.quit

