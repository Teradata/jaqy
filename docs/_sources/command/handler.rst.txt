.handler
--------

``.handler`` displays or replaces various handlers in Jaqy.

Syntax
~~~~~~

.. code-block:: text

	usage:
	    .handler [type] [javascript]

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

javascript

* If the script is not specified, it displays the current handler.
* If the value here is simply ``default``, it restores the default handler.
* Otherwise, it is evaluated as a javascript.

Please see `Handler Configurations <../config/handler.html>`__ for more
information.

Examples
~~~~~~~~

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
~~~~~~~~

* `Handler Configurations <../config/handler.html>`__
