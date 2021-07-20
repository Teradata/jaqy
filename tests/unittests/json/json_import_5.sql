--------------------------------------------------------------------------
-- .import json command test
--------------------------------------------------------------------------
.run ../common/mysql_setup.sql
USE vagrant;

-- Cartographic Boundary Shapefiles - States
-- https://www.census.gov/geo/maps-data/data/cbf/cbf_state.html
-- Converted to GeoJSON using https://mapshaper.org/

CREATE TABLE stateTable(geoId INTEGER, state VARCHAR(4), name VARCHAR(100) CHARACTER SET UTF8, shape MEDIUMTEXT);
.import json -r features data/cb_2017_us_state_500k.json
INSERT INTO stateTable VALUES ({{properties.GEOID}}, {{properties.STUSPS}}, {{properties.NAME}}, {{geometry}});

-- Cartographic Boundary Shapefiles - Urban Areas
-- https://www.census.gov/geo/maps-data/data/cbf/cbf_ua.html
-- Converted to GeoJSON using https://mapshaper.org/

CREATE TABLE cityTable(geoId INTEGER, name VARCHAR(100) CHARACTER SET UTF8, shape MEDIUMTEXT);
.import json -r features data/cb_2017_us_ua10_500k.json
INSERT INTO cityTable VALUES ({{properties.GEOID10}}, {{properties.NAME10}}, {{geometry}});

SELECT COUNT(*) FROM stateTable;
SELECT geoId, state, name FROM stateTable ORDER BY 1;

SELECT COUNT(*) FROM cityTable;
SELECT geoId, name FROM cityTable ORDER BY 1, 2;

DROP TABLE stateTable;
DROP TABLE cityTable;
.close
