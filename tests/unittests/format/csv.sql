--------------------------------------------------------------------------
-- .format csv test
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

.format csv
SELECT * FROM MyTable ORDER BY a;
.format csv -type default
SELECT * FROM MyTable ORDER BY a;
.format csv -type default -d |
SELECT * FROM MyTable ORDER BY a;
.format csv -d |
SELECT * FROM MyTable ORDER BY a;
.format csv -type excel
SELECT * FROM MyTable ORDER BY a;
.format csv -type rfc4180
SELECT * FROM MyTable ORDER BY a;
.format csv -type mysql
SELECT * FROM MyTable ORDER BY a;
.format csv -type tdf
SELECT * FROM MyTable ORDER BY a;

.format csv -type dummy

DROP TABLE MyTable;
.close

