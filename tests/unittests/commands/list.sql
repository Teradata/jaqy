--------------------------------------------------------------------------
-- .list command test
--------------------------------------------------------------------------
.run ../common/sqlite_setup.sql
.help list
.list
.format json
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
.list .
.list %
.list . .
.list % .
.list . %
.list . . %
.list . . MyTable
.list . . %MyTable%
.list . % %

DROP TABLE MyTable;
DROP TABLE MyTable2;
.close

.run ../common/mysql_setup.sql
USE vagrant;
CREATE TABLE MyTable
(
        a       INTEGER PRIMARY KEY,
        b       VARCHAR(200)
);

CREATE TABLE MyTable2
(
        a       INTEGER PRIMARY KEY,
        b       VARCHAR(200)
);

.list
.list .
.list %
.list . .
.list % .
.list . %
.list . . %
.list . . MyTable
.list . . %MyTable%
.list . % %

DROP TABLE MyTable;
DROP TABLE MyTable2;
.close

