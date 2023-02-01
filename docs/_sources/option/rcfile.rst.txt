--rcfile
--------

``--rcfile`` specifies the initialization file.

By default, jaqy reads the initialization script at ``~/.jqrc`` on start up.
One can change the location of the initiation script.

Syntax
~~~~~~

.. code-block:: text

    --rcfile [file]

Example
~~~~~~~

.. code-block:: bash

	# Read myinit.jq as the startup script
	java -jar jaqy.jar --rcfile myinit.jq
