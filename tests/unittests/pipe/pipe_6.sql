--------------------------------------------------------------------------
-- test pipe
--------------------------------------------------------------------------

.run ../common/postgresql_setup.sql
CREATE TABLE XmlTable
(
	a  INTEGER,
	c1 XML
);

INSERT INTO XmlTable VALUES (1, '<abc>1</abc>');
INSERT INTO XmlTable VALUES (2, '<abc>12</abc>');
INSERT INTO XmlTable VALUES (3, '<abc>123</abc>');
INSERT INTO XmlTable VALUES (4, '<abc>1234</abc>');
INSERT INTO XmlTable VALUES (5, '<abc>12345</abc>');
INSERT INTO XmlTable VALUES (6, NULL);

CREATE TABLE XmlTable2
(
	a  INTEGER,
	c1 XML
);

-- same session pipe test for simplicity

.export pipe
.export
.fetchsize 2
SELECT * FROM XmlTable ORDER BY a;
.import pipe
.import
.importschema
.batchsize 2
INSERT INTO XmlTable2 VALUES (?, ?);

SELECT * FROM XmlTable2 ORDER BY a;

DROP TABLE XmlTable;
DROP TABLE XmlTable2;
.close
