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

-- disable prompts
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
.scripts
.end scripts
.repeat 2
SELECT 1;

.close
