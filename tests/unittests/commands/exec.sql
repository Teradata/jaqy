--------------------------------------------------------------------------
-- .exec command test
--------------------------------------------------------------------------
.help exec
.exec lib/count.spl
.exec dummy.sql

.run ../common/mysql_setup.sql
USE vagrant;

CREATE TABLE MyTable
(
	a	INTEGER PRIMARY KEY,
	b	VARCHAR(200)
);

INSERT INTO MyTable VALUES (1, '1');
INSERT INTO MyTable VALUES (2, '2');

.exec -c utf-8 lib/count.spl

CALL simpleproc(@a);
SELECT @a;
DROP PROCEDURE simpleproc;

.exec
CREATE PROCEDURE simpleproc (OUT c INT)
BEGIN
        SELECT COUNT(*) INTO c FROM MyTable;
END;
.end exec

CALL simpleproc(@a);
SELECT @a;
DROP PROCEDURE simpleproc;

DROP TABLE MyTable;
.close

