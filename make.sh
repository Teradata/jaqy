#!/bin/bash

BUILD=$1

if [ -z "$BUILD" ]; then
	BUILD=pkg
fi

#
# For test, coverage, clitests, need to spin up docker
#
# docker-compose up -d --build
#
# after the test, it can be shutdown with
# docker-compose rm -s
#

case "$BUILD" in
	doc)
		cd docs_src
		make docs
		;;
	pkg)
		export JVM_ARGS="-Dnashorn.args=--no-deprecation-warning"
		mvn clean package -Dmaven.test.skip=true
		;;
	test)
		# Java / Maven based tests
		export JVM_ARGS="-Dnashorn.args=--no-deprecation-warning"
		mvn -X clean package
		;;
	coverage)
		export JVM_ARGS="-Dnashorn.args=--no-deprecation-warning"
		mvn clean site -Dcobertura.report.format=xml org.eluder.coveralls:coveralls-maven-plugin:report
		;;
	clitest)
		# Jaqy based tests
		#
		# Need to run after ./make.sh pkg build all the jars
		tests/bin/testall.sh
		;;
esac
