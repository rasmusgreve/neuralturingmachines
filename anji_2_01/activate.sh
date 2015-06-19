#!/bin/bash
echo off
export MYCLASSPATH=./properties
for i in `ls ./lib/*.jar`
do 
	export MYCLASSPATH=${MYCLASSPATH}:${i}
done
java -classpath ${MYCLASSPATH} com.anji.neat.NeatActivator $1 $2
