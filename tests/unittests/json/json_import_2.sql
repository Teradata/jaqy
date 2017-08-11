--------------------------------------------------------------------------
-- test JSON binary outputs etc
--------------------------------------------------------------------------

.run ../common/derby_setup.sql
.open derby:memory:jsonDB;create=true

CREATE TABLE BinTable
(
        a       INTEGER,
        b       LONG VARCHAR FOR BIT DATA
);

INSERT INTO BinTable VALUES (1, X'DEADBEEF');
INSERT INTO BinTable VALUES (2, X'FACEDEAD');

.format csv
SELECT * FROM BinTable ORDER BY a;

.export json -f text -b base64 bin.json
SELECT * FROM BinTable ORDER BY a;
DELETE FROM BinTable;

.import json -f text -b base64 bin.json
INSERT INTO BinTable VALUES ({{A}}, {{B}});
SELECT * FROM BinTable ORDER BY a;

.os rm -f bin.json

.export json -f text -b hex bin.json
SELECT * FROM BinTable ORDER BY a;
DELETE FROM BinTable;

.import json -f text -b hex bin.json
INSERT INTO BinTable VALUES ({{A}}, {{B}});
SELECT * FROM BinTable ORDER BY a;

.os rm -f bin.json

.close
