--------------------------------------------------------------------------
-- .import csv command test
--------------------------------------------------------------------------
.run ../common/postgresql_setup.sql

-- Statistics downloaded from https://www.flysfo.com/media/facts-statistics/air-traffic-statistics

.import csv -h lib/Air_Traffic_Passenger_Statistics.csv
.importtable AirTraffic
SELECT COUNT(*) FROM AirTraffic;
DROP TABLE AirTraffic;

.close

.run ../common/mysql_setup.sql
USE vagrant;

.import csv -h lib/Air_Traffic_Passenger_Statistics.csv
.importtable AirTraffic
SELECT COUNT(*) FROM AirTraffic;
DROP TABLE AirTraffic;

.close
