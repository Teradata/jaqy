--------------------------------------------------------------------------
-- .save command test
--------------------------------------------------------------------------
.help save
.save

.run ../common/postgresql_setup.sql

CREATE TABLE MyTable (a INTEGER, b VARCHAR(10000));

INSERT INTO MyTable VALUES (1, 'POINT(1 1)');
INSERT INTO MyTable VALUES (2, 'POINT(2 2)');
INSERT INTO MyTable VALUES (3, 'POINT(3 3)');
INSERT INTO MyTable VALUES (4, 'POINT(4 4)');

.save
SELECT * FROM MyTable ORDER BY a;

-- a simple way of printing a saved resultset
.alias print
.@eval interpreter.print ($(0))
.end alias

.print save

-- use with repeat command
.repeat ${save.size()}
SELECT '${save.get (iteration)[1]}' AS geo;

-- rewind before printing
.eval save.beforeFirst ();
.filter a > 2
.project b AS Test

-- rewind before printing
.eval save.beforeFirst ();
.print save

DROP TABLE MyTable;

.close
