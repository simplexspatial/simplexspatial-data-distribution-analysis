[![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-v2.0%20adopted-ff69b4.svg)](code_of_conduct.md)

# Data Distribution.

This repository contains different data analysis of data distribution in the OSM dataset.


## Extract blocks
To be able to parallelize, lets extract all blocks. Full universe will take 4 minutes:
```shell script
spark-submit \
  --class com.simplexportal.simplexspatial.analysis.Driver \
  --master "local[*]" \
  target/scala-2.11/simplexspatial-data-distribution-analysis-assembly-0.1.jar \
  extract \
  -i file:///home/angelcc/Downloads/osm/planet/planet-200309.osm.pbf \
  -o file:///home/angelcc/Downloads/osm/planet/blobs
```

## Node IDs distribution
Following, example of how to report for 100 "partitions", locally, using 5 cores and 4Gb per core.
It will take around 30 minutes.

```shell script
/home/angelcc/apps/spark-2.4.5-bin-hadoop2.7/bin/spark-submit \
  --class com.simplexportal.simplexspatial.analysis.Driver \
  --master "spark://angelcc-B450-AORUS-ELITE:7077" \
  --deploy-mode cluster \
  --executor-memory 4G \
  --total-executor-cores 5 \
  --num-executors 1 \
  target/scala-2.11/simplexspatial-data-distribution-analysis-assembly-0.1.jar \
  mod \
  -p 100 \
  -i file:///home/angelcc/Downloads/osm/planet/blobs \
  -o file:///home/angelcc/Downloads/osm/planet/distribution/nodeId/100
```


## Zeppelin
To start the notebook, from {root_project}/zeppelin:
```shell script
docker run -p 8080:8080 --rm \
   -v $PWD/logs:/logs \
   -v $PWD/notebook:/notebook \
   -v /home/angelcc/Downloads/osm/planet/distribution/nodeId/100:/zeppelin/data/nodeId \
   -e ZEPPELIN_LOG_DIR='/logs' \
   -e ZEPPELIN_NOTEBOOK_DIR='/notebook' \
   --name zeppelin \
   apache/zeppelin:0.9.0
```
