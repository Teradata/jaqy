--------------------------------------------------------------------------
-- test JSON binary outputs etc
--------------------------------------------------------------------------

.run ../common/sqlite_setup.sql
.open sqlite::memory:

CREATE TABLE BinTable
(
        a       INTEGER,
        b       BLOB
);

INSERT INTO BinTable VALUES (1, X'DEADBEEF');
INSERT INTO BinTable VALUES (2, X'FACEDEAD');

.format json -p dummy
.format json -b dummy
.format json -b base64 -p off
SELECT * FROM BinTable ORDER BY a;

.format json -b hex
SELECT * FROM BinTable ORDER BY a;

.close
