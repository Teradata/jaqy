.set
----

``.set`` command is used to set or display interpreter level or session level
settings.

Syntax
~~~~~~

.. code-block:: text

	usage: .set [setting] [value]

Example
~~~~~~~

.. code-block:: text

	-- display all the settings
	-- session level settings are only displayed when the session is connected.
	.set

	-- only display the setting of expansion
	.set expansion

	-- turn off expansion
	.set expansion off

	-- set batchsize
	.set batchsize 5000

See Also
~~~~~~~~

* `Settings <../setting.html>`__
