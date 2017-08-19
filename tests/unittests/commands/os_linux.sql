--------------------------------------------------------------------------
-- .os command test
--------------------------------------------------------------------------
.help os

.if !globals.os.windows
.os echo asdf && echo ddd > dummy.txt
.os cat dummy.txt
.os rm -f dummy.txt
.end if
