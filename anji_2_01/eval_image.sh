#!/bin/bash
echo off
export MYCLASSPATH=./properties
for i in `ls ./lib/*.jar`
do 
	export MYCLASSPATH=${MYCLASSPATH}:${i}
done
java -classpath ${MYCLASSPATH} com.anji.imaging.EvaluateImageIdentifier $1 $2
