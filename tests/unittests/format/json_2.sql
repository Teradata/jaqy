--------------------------------------------------------------------------
-- .format json test
--------------------------------------------------------------------------
.run ../common/postgresql_setup.sql

-- Test array
CREATE TABLE ArrTable (a INT, b INT[]);

INSERT INTO ArrTable VALUES (1, NULL);
INSERT INTO ArrTable VALUES (2, '{}');
INSERT INTO ArrTable VALUES (3, '{1}');
INSERT INTO ArrTable VALUES (4, '{1,2}');
INSERT INTO ArrTable VALUES (5, '{1,2,3}');

.format json
SELECT * FROM ArrTable ORDER BY a;

DROP TABLE ArrTable;

CREATE TABLE ArrTable (a INT, b INT[][]);

INSERT INTO ArrTable VALUES (1, NULL);
INSERT INTO ArrTable VALUES (2, '{}');
INSERT INTO ArrTable VALUES (3, '{1}');
INSERT INTO ArrTable VALUES (4, '{1,2}');
INSERT INTO ArrTable VALUES (5, '{1,2,3}');

.format json
SELECT * FROM ArrTable ORDER BY a;

DROP TABLE ArrTable;

.close

