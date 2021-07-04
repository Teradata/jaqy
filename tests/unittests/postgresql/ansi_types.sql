--------------------------------------------------------------------------
-- test SQL types
--------------------------------------------------------------------------
.run ../common/postgresql_setup.sql
SET TIME ZONE '-04:00';
.run ../common/ansi_types.sql
.close

