Handler Configurations
======================

Various handlers in Jaqy can be configured using
`.handler <../command/handler.html>`__ command using JavaScript.

Currently, the following types of handlers can be configured.

+----------+-----------------------------------------------+
| type     | description                                   |
+==========+===============================================+
| prompt   | the prompt after each command or SQL          |
+----------+-----------------------------------------------+
| title    | the terminal title                            |
+----------+-----------------------------------------------+
| success  | the success message after SQL execution       |
+----------+-----------------------------------------------+
| update   | the update message after SQL update           |
+----------+-----------------------------------------------+
| error    | the error message after JDBC or command error |
+----------+-----------------------------------------------+
| activity | the activity count of the ResultSet           |
+----------+-----------------------------------------------+

Class Types
-----------

The following JavaScript handlers are based on
`ScriptStateHandler <https://github.com/Teradata/jaqy/blob/master/jaqy-console/src/main/java/com/teradata/jaqy/utils/ScriptStateHandler.java>`__
.

* prompt
* title
* success
* update
* activity

The following JavaScript handlers are based on
`ScriptErrorStateHandler <https://github.com/Teradata/jaqy/blob/master/jaqy-console/src/main/java/com/teradata/jaqy/utils/ScriptErrorStateHandler.java>`__
.

* error

The ScriptErrorStateHandler basically sets up three additional objects:
``message``, ``error`` and ``sqlex`` to simplify the error message generation.

Examples
--------

.. code-block:: text

	-- displays the current prompt
	.handler prompt
	-- restores default prompt
	.handler prompt default
	-- set a new prompt handler using javascript
	.handler prompt display.fill ("-- javascript prompt: " + session.id + ": " + interpreter.sqlCount + "/" + interpreter.commandCount + " ") + "\n"
	-- set a new title handler using javascript
	.handler title "-- title --"
	-- set a new success handler using javascript
	.handler success "-- javascript success --"
	-- set a new update handler using javascript
	.handler update "-- javascript update: " + session.activityCount
	-- set a new error handler using javascript
	.handler error "-- javascript error: " + ((error == null) ? message : (sqlex == null ? error.message : ("SQL Error: " + sqlex.message)))
	-- set a new activity handler using javascript
	.handler activity "-- javascript activity: " + session.activityCount

See Also
--------

* `Scripting <../script.html>`__
