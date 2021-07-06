--------------------------------------------------------------------------
-- .export csv test
--------------------------------------------------------------------------
.run ../common/postgresql_setup.sql

CREATE TABLE xmlTable (a INTEGER, b XML);

INSERT INTO xmlTable VALUES (1, '<a>1</a>');
INSERT INTO xmlTable VALUES (2, '<a>2</a>');

.format csv
SELECT * FROM xmlTable ORDER BY a;

.export csv xml.csv
SELECT * FROM xmlTable ORDER BY a;

.os cat xml.csv
.os rm -f xml.csv

DROP TABLE xmlTable;

.close
