# Teradata Jaqy [![Build Status](https://travis-ci.org/Teradata/jaqy.svg?branch=master)](https://travis-ci.org/Teradata/jaqy)[![codecov](https://codecov.io/gh/Teradata/jaqy/branch/master/graph/badge.svg)](https://codecov.io/gh/Teradata/jaqy)[![Apache License, Version 2.0](https://img.shields.io/badge/license-Apache--2.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

Teradata Jaqy is a universal JDBC client for connecting any databases with
JDBC drivers.  It is designed with the following in mind.

* Zero installation.  Nor does not require administrative privileges.
  In contrast, ODBC drivers require administrative privileges to install
  and setup.
* Late JDBC binding.  JDBC drivers are loaded dynamically only when needed.
  As the result, Jaqy starts up quickly without needing to load unnecessary
  drivers.
* Interactive and scripting support.
* Support data transfer to and from database in various formats (CSV, JSON, AVRO, etc).

Teradata Jaqy is mainly tested against SQLite, Derby, MySQL and PostgreSQL,
since they can be easily made available as part of CI process.  Brief testing
on Teradata, HIVE, Presto, Teradata Aster, etc were done, and it worked
pretty well.

## Build

To make the build, you will need to install some required JDBC drivers for
compilation purposes.  Then run the following command.

```bash
mvn clean package -Dmaven.test.skip=true
```

## Test

While Teradata Jaqy can be built on either Windows or Linux platforms, the
tests are designed for Linux platforms only, due to the fact that the EOL
for Windows and Linux are different.

Once the test environment is properly setup, run the following command in ``jaqy/``
to run the unit tests.

```bash
mvn clean test
```

To obtain the code coverage report, run the following command instead.

```bash
mvn clean site
```

### Test Environment Setup

[VirtualBox](https://www.virtualbox.org/) and [Vagrant](https://www.vagrantup.com/)
are required to setup the testing environment.

In the project directory (jaqy/), run the following command.

```bash
vagrant up
```

#### CDH VM Setup

In tests/unittests/cdh, run the following command to provision a CDH VM.

```bash
vagrant up
# and run the following command if the box is old
vagrant reload
```

#### Teradata Express VM Setup

You can download Teradata Express VM for VMWare from this
[link](http://downloads.teradata.com/download/database/teradata-express-for-vmware-player).
This VM can be converted to VirtualBox without VMWare installed, but the
process is a bit tedious so I will not elaborate here.  Be sure to map host
port 1025 port to guest port 1025.

You will also need to download Teradata JDBC Drivers from this
[link](http://downloads.teradata.com/download/connectivity/jdbc-driver), and
put the jar files under `tests/unittests/teradata/lib`.

### Manual Tests

There are a lot of tests in tests/unittests/ that are tested via scripts
instead of via Maven.  And some tests run from maven can also be run via
scripts.

To run these tests

```bash
vagrant ssh
```

Then in the appropriate ``tests/unittests/testdir``, just run

```
runtest.sh
```

The unit tests in the testdir will be executed.  If there are any mismatches,
it should report the testing temporary directory.  Go to the ``output/`` directory
and the testing output, and difference with the expected (i.e. control) output are
shown.

To update the control file, run the following command, with ``unittest.txt``
corresponding to the output file that needs to be promoted as the control.

```
newcontrol.sh unittest.txt
```
