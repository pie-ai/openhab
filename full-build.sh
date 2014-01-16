#!/bin/bash
dir=$(dirname $(which $0));
export MAVEN_OPTS="-Xms512m -Xmx1024m -XX:PermSize=256m -XX:MaxPermSize=512m"
mvn-3 -Dmaven.repo.local=${dir}/repo clean install
