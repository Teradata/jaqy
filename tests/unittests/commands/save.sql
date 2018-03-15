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

-- client side filtering using javascript
.script
{
	save.getResultSet ().getRows ().removeIf (
		function(item) {
			return item[0] < 3;
		}
	);
}
.end script

.print save


DROP TABLE MyTable;

.close
