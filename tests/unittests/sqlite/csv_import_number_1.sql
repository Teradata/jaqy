--------------------------------------------------------------------------
-- CSV number test
--------------------------------------------------------------------------
.run ../common/sqlite_setup.sql
.open sqlite::memory:

.import csv -h ../common/data/number_1.csv
.importtable numTable1

.desc numTable1
SELECT * FROM numTable1 ORDER BY 1;
DROP TABLE numTable1;

CREATE TABLE numTable1
(
    "id"    INTEGER,
    "int1"  INTEGER,
    "int2"  INTEGER,
    "int4"  INTEGER,
    "int8"  BIGINT,
    "dec"   REAL,
    "f4"    REAL,
    "f8"    REAL
);
.import csv -h ../common/data/number_1.csv
INSERT INTO numTable1 VALUES (?, ?, ?, ?, ?, ?, ?, ?);

.desc numTable1
SELECT * FROM numTable1 ORDER BY 1;
DROP TABLE numTable1;

.quit

