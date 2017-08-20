--------------------------------------------------------------------------
-- .unalias command test
--------------------------------------------------------------------------
.help unalias
.unalias

.alias ins
INSERT INTO $0 VALUES (${1-});
INSERT INTO $0 VALUES (1 + ${1} ${2});
.end alias

.alias
.unalias asdf
.unalias ins
.alias
