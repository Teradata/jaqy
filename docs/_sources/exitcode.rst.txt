Exit Code
=========

If `.exit <command/exit.html>`__ command is run without specifying an exit
code, one of the following exit code is generated.

+------+---------------------------------------------------------------+
| Code | Meaning                                                       |
+======+===============================================================+
| 0    | Indicates no errors.                                          |
+------+---------------------------------------------------------------+
| 1    | Indicates command line option error.                          |
+------+---------------------------------------------------------------+
| 2    | Indicates having Jaqy command errors.                         |
+------+---------------------------------------------------------------+
| 4    | Indicates having SQL exceptions.                              |
+------+---------------------------------------------------------------+
| 6    | Indicates having both Jaqy command errors and SQL exceptions. |
+------+---------------------------------------------------------------+
