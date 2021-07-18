--------------------------------------------------------------------------
-- testing variables
--------------------------------------------------------------------------

.run ../common/sqlite_setup.sql
.open sqlite::memory:

.eval print(message);
.eval print(error);
.eval print(sqlError);
SELECT asdf;
.eval print(error);
.eval print(message);
.eval print(sqlError);

.eval interpreter.setVariableValue("error", "esdf");
.eval interpreter.setVariableValue("message", "esdf");
.eval interpreter.setVariableValue("sqlError", "esdf");

.eval error = "esdf";
.eval message = "esdf";
.eval sqlError = "esdf";

.eval print(error);
.eval print(message);
.eval print(sqlError);

