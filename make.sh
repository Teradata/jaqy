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
		mvn clean package -Dmaven.test.skip=true
		;;
	test)
		mvn clean package
		;;
	coverage)
		mvn jacoco:report-aggregate
		;;
	clitest)
		# Jaqy based tests
		#
		# Need to run after ./make.sh pkg build all the jars
		tests/bin/testall.sh
		;;
esac

