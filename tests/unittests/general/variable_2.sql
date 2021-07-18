--------------------------------------------------------------------------
-- testing variables
--------------------------------------------------------------------------

.run ../common/sqlite_setup.sql
.open sqlite::memory:

.eval print(interpreter);
.eval print(session);
.eval print(activityCount);
SELECT 1234 AS Test;
.eval print(interpreter);
.eval print(session);
.eval print(activityCount);

.eval session = "esdf";
.eval interpreter = "esdf";
.eval activityCount = 1234;

.eval print(interpreter);
.eval print(session);
.eval print(activityCount);

SELECT 222 AS Test;
.eval print(interpreter);
.eval print(session);
.eval print(activityCount);

