--------------------------------------------------------------------------
-- .script command test
--------------------------------------------------------------------------
.run ../common/sqlite_setup.sql
.open sqlite::memory:

.script
var dummyHandler = new Packages.com.teradata.jaqy.interfaces.StateHandler ()
{
    getString: function () { return null; }
};
.end script

.repeat 2
SELECT 1;

-- disable handlers
.script
display.setIterationHandler (dummyHandler);
display.setSuccessHandler (dummyHandler);
display.setActivityCountHandler (dummyHandler);
display.setErrorHandler (dummyHandler);
display.setUpdateHandler (dummyHandler);
.end script
.repeat 5
SELECT 1;

-- restore handlers
.script
display.setIterationHandler (null);
display.setSuccessHandler (null);
display.setActivityCountHandler (null);
display.setErrorHandler (null);
display.setUpdateHandler (null);
.end script
.repeat 2
SELECT 1;

.close
