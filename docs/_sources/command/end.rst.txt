.end
----

``.end`` ends a multi-line command.

The following commands can be potentially multi-line.

* `.alias <alias.html>`__
* `.exec <exec.html>`__
* `.if <if.html>`__
* `.script <script.html>`__

Example
~~~~~~~

.. code-block:: sql

	SELECT * FROM MyTable ORDER BY a;
	-- Check the number of rows returned from a query
	.if activityCount == 0
	.rem Test
	SELECT 2;
	.rem Test
	.end if
