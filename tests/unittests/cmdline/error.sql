--------------------------------------------------------------------------
-- command argument test
--------------------------------------------------------------------------
.run ../common/sqlite_setup.sql
.open sqlite::memory:

SELECT 1 AS Test;

.close

