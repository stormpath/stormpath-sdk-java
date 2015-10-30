#! /bin/sh

# TODO: Move top-level docs folder into extensions/servlet-plugin
# servlet-plugin docs
cd docs
make html
cd ..

# spring boot docs
cd extensions/spring/boot/docs
make html
cd ../../../../

# javadocs
mvn javadoc:aggregate -P docs
