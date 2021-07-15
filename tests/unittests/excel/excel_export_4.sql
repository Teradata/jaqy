--------------------------------------------------------------------------
-- .export excel test
--------------------------------------------------------------------------
.run ../common/postgresql_setup.sql

CREATE TABLE MyTable(a INTEGER, b DECIMAL, c DECIMAL(9,2), d DECIMAL(9,5));

INSERT INTO MyTable VALUES (1, 1.15, 2.25, 333.33);
INSERT INTO MyTable VALUES (2, 2.20, 2.30, 333.34000);
INSERT INTO MyTable VALUES (3, 3.35, 2.45, 333.35);
INSERT INTO MyTable VALUES (4, 4.45, 2.55, 333.36);
INSERT INTO MyTable VALUES (5, 5.55, 2.65, 333.37);

SELECT * FROM MyTable ORDER BY a;

.export excel file4.xlsx
SELECT * FROM MyTable ORDER BY a;

.os ${SCRIPTDIR}/cmpexcel.sh data/file4.xlsx file4.xlsx && rm -f file4.xlsx

DROP TABLE MyTable;

