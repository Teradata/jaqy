Path Formats
============

Whenever a jaqy script is run, all the relative path within the script are
related to the location of the script.  This rule applies to all the path
formats.

This behavior is intended to make writing setup scripts easier without having
to use absolute paths.

.. note::

	For loading plugins using `.load <command/load.html>`__ command, only file
	path is recognized.

Jaqy supports the following path formats

.. toctree::
	:maxdepth: 1
	:glob:

	path/*
