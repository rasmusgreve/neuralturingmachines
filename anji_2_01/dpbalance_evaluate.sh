#!/bin/bash
#echo off
export MYCLASSPATH=./properties
for i in `ls ./lib/*.jar`
do 
	export MYCLASSPATH=${MYCLASSPATH}:${i}
done
echo ${MYCLASSPATH}

nohup java -classpath ${MYCLASSPATH} -Xms256m -Xmx384m com.anji.polebalance.DoublePoleBalanceEvaluator $1 $2 &
