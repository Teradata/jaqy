--------------------------------------------------------------------------
-- CSV number test
--------------------------------------------------------------------------
.run ../common/postgresql_setup.sql

.import csv -h ../common/data/number_1.csv
.importtable numTable1

.desc numTable1
SELECT * FROM numTable1 ORDER BY 1;
DROP TABLE numTable1;

CREATE TABLE numTable1
(
    "id"    INTEGER,
    "int1"  SMALLINT,
    "int2"  SMALLINT,
    "int4"  INTEGER,
    "int8"  BIGINT,
    "dec"   DECIMAL(5,2),
    "f4"    REAL,
    "f8"    DOUBLE PRECISION
);
.import csv -h ../common/data/number_1.csv
INSERT INTO numTable1 VALUES (?, ?, ?, ?, ?, ?, ?, ?);

.desc numTable1
SELECT * FROM numTable1 ORDER BY 1;
DROP TABLE numTable1;

.quit

