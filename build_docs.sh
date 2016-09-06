#! /bin/sh

# servlet-plugin + spring-boot docs
cd docs
make allhtml
cd ..

# javadocs
mvn javadoc:aggregate -P travis-docs
