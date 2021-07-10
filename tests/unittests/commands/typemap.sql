--------------------------------------------------------------------------
-- .typemap command test
--------------------------------------------------------------------------
.help typemap
.typemap
.typemap -i
.typemap -j

.run ../common/sqlite_setup.sql
.open sqlite::memory:
.typemap
.typemap -i
.typemap -d
.exit
