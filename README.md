[![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-v2.0%20adopted-ff69b4.svg)](code_of_conduct.md)

# Data Distribution.

This repository contains different data analysis of data distribution in the OSM dataset.


## Extract blocks
To be able to parallelize, lets extract all blocks. Full universe will take 4 minutes:
```shell script
spark-submit
  --class com.simplexportal.simplexspatial.analysis.Driver \
  --master "local[*]" \
  target/scala-2.11/simplexspatial-data-distribution-analysis-assembly-0.1.jar \
  extract \
  -i file:///home/angelcc/Downloads/osm/planet/planet-200309.osm.pbf \
  -o file:///home/angelcc/Downloads/osm/planet/blobs
```
