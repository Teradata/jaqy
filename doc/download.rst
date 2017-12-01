.. _download:

Download
========

Jaqy is still under development for adding a few remaining features,
thus there is no formal releases.  However, it is already very much usable
with a lot of features.   You can download and make a build yourself using
the following instructions.

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

There are two jars built.

.. code-block::	bash

	dist/jaqy-1.0.jar
	jaqy-avro/target/jaqy-avro-1.0.jar

``jaqy-1.0.jar`` is the standalone package.  ``jaqy-avro-1.0.jar`` is a separate
addon for AVRO import / export format.
