--------------------------------------------------------------------------
-- .rem command test
--------------------------------------------------------------------------
.help rem

.rem
SELECT 1234;
.end rem

.help rem

.rem asdfasdfasdf
asdfasdf;
asd;
a//z/
.end rem

.set echo off
.quiet on
.rem
This is a test
.end rem
.quiet off
.set echo auto

.help rem

