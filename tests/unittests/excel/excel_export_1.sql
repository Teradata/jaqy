--------------------------------------------------------------------------
-- .export excel test
--------------------------------------------------------------------------
.run ../common/sqlite_setup.sql
.open sqlite::memory:

CREATE TABLE MyTable(a INTEGER PRIMARY KEY, b VARCHAR(200), c VARCHAR(200));

INSERT INTO MyTable VALUES (1, 'abc', 'def');
INSERT INTO MyTable VALUES (2, 'john', 'doe');
INSERT INTO MyTable VALUES (3, 'a"b', 'c"d');
INSERT INTO MyTable VALUES (4, 'a,b', 'c,d');
INSERT INTO MyTable VALUES (5, 'a''b', 'c''d');
INSERT INTO MyTable VALUES (6, 'a''",b', 'c''",d');
INSERT INTO MyTable VALUES (7, 'a	b', 'c,d');

SELECT * FROM MyTable ORDER BY a;

.export excel
.export excel file1.xlsx
.export
SELECT * FROM MyTable ORDER BY a;

.os ${SCRIPTDIR}/cmpexcel.sh data/file1.xlsx file1.xlsx && rm -f file1.xlsx

DROP TABLE MyTable;

