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

.export json
.export json -b dummy
.export json -f dummy
.export json -f text -b base64 -p off bin.json
SELECT * FROM BinTable ORDER BY a;

.os diff -b bin.json data/bin.json
.os rm -f bin.json

.close
