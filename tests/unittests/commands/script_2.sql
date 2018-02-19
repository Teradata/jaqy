--------------------------------------------------------------------------
-- .script command test
--------------------------------------------------------------------------
.run ../common/sqlite_setup.sql
.open sqlite::memory:

SELECT 1;

SELECT ${activityCount} AS activityCount;
SELECT SUBSTR ('${display}', 1, 33) AS display;
SELECT SUBSTR ('${esc}', 1, 31) AS esc;
SELECT SUBSTR ('${globals}', 1, 26) AS globals;
SELECT SUBSTR ('${interpreter}', 1, 34) AS interpreter;
SELECT SUBSTR ('${parent}', 1, 34) AS parent;
SELECT '${session}' AS session;

.close
