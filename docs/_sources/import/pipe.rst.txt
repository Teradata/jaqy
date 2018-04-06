pipe
^^^^

Pipe export / import is used to transfer data between databases without
using intermediate storage.

Pipe importer supports both positional and name based loading.  It also
supports schema discovery using the ResultSetMetaData obtained from pipe
exporter.

For importing data with LOB values, it may be necessary to change the batch
size to reduce the memory consumption.

See Also
********

* `pipe <../export/pipe.html>`__ export format.
