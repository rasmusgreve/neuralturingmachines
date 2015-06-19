#!/bin/bash

rm nohup.out

for id in `seq 1000 1049`; 
do 
  nohup echo RUN ${id}
  nohup ant -f ./evolve.xml -Dproperties.file=${1} -Drun-id=${id}
done
