--------------------------------------------------------------------------
-- os command test
--------------------------------------------------------------------------
.help os

.if globals.os.windows
.os help help > dummy.txt
.os more dummy.txt
.os del dummy.txt
.os dir /B dummy.txt
.end if
