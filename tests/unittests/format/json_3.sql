--------------------------------------------------------------------------
-- .format json test
--------------------------------------------------------------------------
.run ../common/postgresql_setup.sql

-- Test array
CREATE TABLE ArrTable (a INT, b boolean, c boolean[]);

INSERT INTO ArrTable VALUES (1, true, NULL);
INSERT INTO ArrTable VALUES (2, false, '{}');
INSERT INTO ArrTable VALUES (3, true, '{true}');
INSERT INTO ArrTable VALUES (4, null, '{true,false}');
INSERT INTO ArrTable VALUES (5, true, '{true,true,false}');

.format json
SELECT * FROM ArrTable ORDER BY a;

DROP TABLE ArrTable;

.close

