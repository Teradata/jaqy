--------------------------------------------------------------------------
-- test PostgreSQL various data types
--------------------------------------------------------------------------
.run ../common/postgresql_setup.sql
.debug resultset on
.debug preparedstatement on

CREATE TYPE Complex AS
(
	r DOUBLE PRECISION,
	i DOUBLE PRECISION
);

CREATE TABLE ComplexTable
(
	a  INTEGER,
	c1 Complex
);

INSERT INTO ComplexTable VALUES (1, '(1,1)');
INSERT INTO ComplexTable VALUES (2, '(2,2)');
INSERT INTO ComplexTable VALUES (3, '(2,)');
INSERT INTO ComplexTable VALUES (4, NULL);

.export avro t.avro
SELECT * FROM ComplexTable ORDER BY a;
.import avro t.avro
INSERT INTO ComplexTable ({{a}}, {{c1}});
.format json -p on
SELECT * FROM ComplexTable ORDER BY a;

DROP TABLE ComplexTable;

CREATE TYPE Complex2 AS
(
	c1 Complex,
	c2 Complex[]
);

CREATE TABLE ComplexTable2
(
	a  INTEGER,
	c1 Complex2
);

INSERT INTO ComplexTable2 VALUES (1, '("(1,1)","{""(2,2)""}")');
INSERT INTO ComplexTable2 VALUES (2, '("(2,2)","{""(2,2)"",null}")');
INSERT INTO ComplexTable2 VALUES (3, '("(1,1)",)');
INSERT INTO ComplexTable2 VALUES (4, NULL);

.export avro t.avro
SELECT * FROM ComplexTable2 ORDER BY a;
.import avro t.avro
INSERT INTO ComplexTable2 ({{a}}, {{c1}});
.format json -p on
SELECT * FROM ComplexTable2 ORDER BY a;

DROP TABLE ComplexTable2;
DROP TYPE Complex2;
DROP TYPE Complex;
.close
.os rm -f t.avro

