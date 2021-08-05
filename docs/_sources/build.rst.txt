Building Jaqy
=============

Build Requirement
-----------------

* `git <https://git-scm.com/>`__
* `maven <http://maven.apache.org/>`__
* `JDK 8 <http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html>`__
  or `OpenJDK 8 <http://openjdk.java.net/install/>`__

On Ubuntu 16.04 LTS, the build environment can be simply setup with the following
command.

.. code-block::	bash

	sudo apt-get install -y git maven openjdk-8-jdk

Making the Build
----------------

.. code-block::	bash

	git clone https://github.com/Teradata/jaqy.git
	cd jaqy
	mvn clean package -Dmaven.test.skip=true

There are severals jars built.

.. list-table::
	:header-rows: 1

	* - Jar Path
	  - Description
	* - dist/jaqy-1.2.0.jar
	  - Main program
	* - jaqy-avro/target/jaqy-avro-1.2.0.jar
	  - Addon to import / export AVRO format.
	* - jaqy-s3/target/jaqy-s3-1.2.0.jar
	  - Addon to access AWS S3
	* - jaqy-azure/target/jaqy-azure-1.2.0.jar
	  - Addon to access Azure

Testing Environment
-------------------

For the unit tests, use `docker-compose <https://docs.docker.com/compose/>`__
in the jaqy/ directory to start up a testing docker environment.

.. code-block::	bash

	docker-compose up -d

Once the testing environment is set up, just run the following maven command
to execute the unit tests.

.. code-block::	bash

	# run junit tests then build the package
	mvn clean package
	# run standalone CLI tests
	tests/bin/testall.sh
