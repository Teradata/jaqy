--------------------------------------------------------------------------
-- .format table test
--------------------------------------------------------------------------
.help format

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

.format table --border asdf
.format table --border on
SELECT * FROM MyTable ORDER BY a;
.format table --border off
SELECT * FROM MyTable ORDER BY a;
.format

.format table --autosize asdf
.format table --autosize on
SELECT * FROM MyTable ORDER BY a;
.format table --autosize off
SELECT * FROM MyTable ORDER BY a;

DROP TABLE MyTable;
.close

