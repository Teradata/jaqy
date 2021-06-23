#!/bin/bash

BUILD=$1

if [ -z "$BUILD" ]; then
	BUILD=pkg
fi

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
esac

