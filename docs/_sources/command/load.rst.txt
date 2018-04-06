.load
-----

``.load`` loads a Jaqy plugin.  See `Plugin Loading <../plugin.html>`__ for
more information.

Syntax
~~~~~~

.. code-block:: bash

	usage: .load [classpath]

Example
~~~~~~~

.. code-block:: sql

	-- Load AVRO import / export plugin
	.load /vagrant/jaqy-avro/target/jaqy-avro-1.0.jar
