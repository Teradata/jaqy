--------------------------------------------------------------------------
-- .list command test
--------------------------------------------------------------------------
.run ../common/sqlite_setup.sql
.help list

.format csv
.open sqlite::memory:

CREATE TABLE MyTable
(
	a	INTEGER PRIMARY KEY,
	b	VARCHAR(200)
);

CREATE TABLE MyTable2
(
	a	INTEGER PRIMARY KEY,
	b	VARCHAR(200)
);

.list
.list . . %
.list . . MyTable
.list . . %MyTable%

DROP TABLE MyTable;
DROP TABLE MyTable2;
.close

