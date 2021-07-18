--------------------------------------------------------------------------
-- .config command test
--------------------------------------------------------------------------
.help config
.config

.config []
.end config

.config
[
.end config

.config
[]
.end config

.config
[{"a":"b"}]
.end config

.config
[{"protocol":""}]
.end config

.config
[{"protocol":"dummy"}]
.end config

.config
[{
"protocol":"dummy",
"typeMap": [{ "type" : "NULL", "name" : "INTEGER"}]
}]
.end config

.config
[{
"protocol":"dummy",
"typeMap": [{ "type" : "dummy", "name" : "INTEGER"}]
}]
.end config

