--------------------------------------------------------------------------
-- test PostGIS data type
--------------------------------------------------------------------------
.run ../common/postgresql_setup.sql
.debug resultset on

CREATE TABLE GeoTable
(
	a  INTEGER,
	c1 Geography
);
.format table
.desc GeoTable
.desc -s GeoTable

INSERT INTO GeoTable VALUES (1, 'POINT(0 0)');
INSERT INTO GeoTable VALUES (2, 'POINT(1 1 1)');
INSERT INTO GeoTable VALUES (3, 'POINT EMPTY');
INSERT INTO GeoTable VALUES (4, 'LINESTRING(0 0,1 1)');
INSERT INTO GeoTable VALUES (5, 'SRID=4269;POINT(1.5 1.5)');

.format csv
SELECT * FROM GeoTable ORDER BY a;
SELECT a, ST_AsText(c1) FROM GeoTable ORDER BY a;
SELECT a, ST_AsEWKT(c1) FROM GeoTable ORDER BY a;

DROP TABLE GeoTable;
.close
