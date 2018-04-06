Plugin Loading
==============

`.load <command/load.html>`__ command can be used to dynamically load a
Jaqy plugin.

Here are the loading sequence.

* ``META-INF/services/com.teradata.jaqy.interfaces.JaqyPlugin`` is checked.
* ``META-INF/services/com.teradata.jaqy.interfaces.JaqyPrinter`` is checked.
* ``META-INF/services/com.teradata.jaqy.interfaces.JaqyExporter`` is checked.
* ``META-INF/services/com.teradata.jaqy.interfaces.JaqyImporter`` is checked.
