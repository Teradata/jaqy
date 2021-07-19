--------------------------------------------------------------------------
-- .debug log command test
--------------------------------------------------------------------------
-- ignore begin
.debug log info
.debug
SELECT 1234;
.debug log warning
.debug
SELECT 1234;
.debug log all
.debug
SELECT 1234;
-- ignore end
.debug log off
.debug
SELECT 1234;
.debug log asdf
