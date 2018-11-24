--------------------------------------------------------------------------
-- test Apache Derby identity column behavior
--------------------------------------------------------------------------
.run ../common/derby_setup.sql
.open derby:memory:identityDB;create=true

CREATE TABLE MyOrder
(
	orderID INT GENERATED ALWAYS AS IDENTITY,
	item VARCHAR(2000),
	time TIMESTAMP
);

INSERT INTO MyOrder (item, time) VALUES ('Dummy', TIMESTAMP('2009-10-16 14:24:43'));

SELECT * FROM MyOrder;

DROP TABLE MyOrder;
.close

