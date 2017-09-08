--------------------------------------------------------------------------
-- PERIOD data type test
--------------------------------------------------------------------------
.run setup.sql
CREATE DATABASE vagrant AS PERM=1e8;
DATABASE vagrant;

CREATE TABLE pdtTable
(
	a  INTEGER,
	p1 PERIOD(DATE),
	p2 PERIOD(TIME(6)),
	p3 PERIOD(TIME(6) WITH TIME ZONE),
	p4 PERIOD(TIMESTAMP(6)),
	p5 PERIOD(TIMESTAMP(6) WITH TIME ZONE)
);

INSERT INTO pdtTable VALUES (1, PERIOD(DATE'2001-02-03', DATE'2002-03-04'), PERIOD(TIME'01:02:03.123456', TIME'02:03:04.123456'), PERIOD(TIME'01:02:03.123456-08:00', TIME'02:03:04.123456-08:00'), PERIOD(TIMESTAMP'2001-02-03 01:02:03.123456', TIMESTAMP'2002-03-04 02:03:04.123456'), PERIOD(TIMESTAMP'2001-02-03 01:02:03.123456-08:00', TIMESTAMP'2002-03-04 02:03:04.123456-08:00'));
INSERT INTO pdtTable VALUES (2, NULL, NULL, NULL, NULL, NULL);

.format table
SELECT * FROM pdtTable ORDER BY a;
.format json
SELECT * FROM pdtTable ORDER BY a;

.debug resultset on
.debug preparedstatement on
.export json t.json
SELECT * FROM pdtTable ORDER BY a;
DELETE FROM pdtTable;
.import json t.json
INSERT INTO pdtTable VALUES ({{a}}, {{p1}}, {{p2}}, {{p3}}, {{p4}}, {{p5}});
.debug resultset off
.debug preparedstatement off
SELECT * FROM pdtTable ORDER BY a;

.export csv t.csv
SELECT * FROM pdtTable ORDER BY a;
.import csv t.csv
INSERT INTO pdtTable VALUES ({{a}}, {{p1}}, {{p2}}, {{p3}}, {{p4}}, {{p5}});
SELECT * FROM pdtTable ORDER BY a;

DELETE DATABASE vagrant;
DROP DATABASE vagrant;
.close
.os rm -f t.json
.os rm -f t.csv

