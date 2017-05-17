# README

## Test

The tests in `tests/unittests/` are designed to be run in both from command
line, or from JUnit.

### Test Environment Setup

You will need [VirtualBox](https://www.virtualbox.org/) and
[Vagrant](https://www.vagrantup.com/) to setup the testing environment.

In the project directory (jaqy/), run the following command to setup the main
test environment.

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
process can be tedious.  Be sure to map host port 1025 port to guest port
1025.

You will also need to download Teradata JDBC Drivers from this
[link](http://downloads.teradata.com/download/connectivity/jdbc-driver).  And
then you need to put the jar files in `tests/unittests/teradata/lib`.
