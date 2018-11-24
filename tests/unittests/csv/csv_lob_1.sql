--------------------------------------------------------------------------
-- .export csv test
--------------------------------------------------------------------------
.run ../common/sqlite_setup.sql
.open sqlite::memory:
.format csv

CREATE TABLE MyTable(a INTEGER PRIMARY KEY, b TEXT, c BLOB);

INSERT INTO MyTable VALUES (1, 'a', X'de');
INSERT INTO MyTable VALUES (2, 'ab', X'dead');
INSERT INTO MyTable VALUES (3, 'abc', X'deadbe');
INSERT INTO MyTable VALUES (4, 'abcd', X'deadbeef');
INSERT INTO MyTable VALUES (5, 'abcde', X'deadbeeffa');
INSERT INTO MyTable VALUES (6, 'abcdef', X'deadbeefface');
INSERT INTO MyTable VALUES (7, 'abcdefg', X'deadbeeffacefe');
INSERT INTO MyTable VALUES (8, NULL, NULL);

.export csv -n %02d.bin -e UTF-16LE -f2 -f3 file1.csv
SELECT * FROM MyTable ORDER BY a;

.os /usr/bin/hexdump -C 13.bin
.os /usr/bin/hexdump -C 14.bin
.os cat file1.csv

DELETE FROM MyTable;

.import csv -h -e UTF-16LE -j2 -k3 file1.csv
INSERT INTO MyTable VALUES (?, ?, ?);

SELECT * FROM MyTable ORDER BY a;

DROP TABLE MyTable;
.close
.os rm -f file1.csv *.bin
