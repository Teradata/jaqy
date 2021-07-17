--------------------------------------------------------------------------
-- command argument test
--------------------------------------------------------------------------
.run ../common/sqlite_setup.sql
.open sqlite::memory:

SELECT 1 AS Test;

SELECT 1 FROM noTable ORDER BY 1;

.close

