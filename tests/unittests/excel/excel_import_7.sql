--------------------------------------------------------------------------
-- .import excel test
--------------------------------------------------------------------------
.run ../common/sqlite_setup.sql
.open sqlite::memory:

CREATE TABLE MyTable (col1 INTEGER, col2 REAL, col3 REAL, col4 REAL);

.import excel -h data/file4.xlsx
INSERT INTO MyTable VALUES ({{b}}, {{a}}, {{d}}, {{c}});
SELECT * FROM MyTable ORDER BY 1;

DELETE FROM MyTable;
.import excel -h data/file4.xlsx
INSERT INTO MyTable VALUES ({{b}}, {{a}}, {{d}}, {{d}});
SELECT * FROM MyTable ORDER BY 1;

DELETE FROM MyTable;
.import excel -h data/file4.xlsx
INSERT INTO MyTable VALUES ({{b}}, {{a}}, {{d}}, {{f}});
SELECT * FROM MyTable ORDER BY 1;

DROP TABLE MyTable;
