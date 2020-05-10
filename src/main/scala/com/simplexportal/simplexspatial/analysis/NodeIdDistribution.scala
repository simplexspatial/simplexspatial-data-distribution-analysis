/*
 * Copyright 2020 Ángel Cervera Claudio
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.simplexportal.simplexspatial.analysis

import com.acervera.osm4scala.model.{NodeEntity, OSMEntity}
import org.apache.spark.SparkContext
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}
import org.slf4j.LoggerFactory

object NodeIdDistribution {
  val logger = LoggerFactory.getLogger(NodeIdDistribution.getClass.getName)

  def run(input: String, output: String, partitions: Long)(
      implicit ctx: SparkContext,
      spark: SparkSession
  ): Unit = {
    import spark.implicits._
    Common
      .fromBlobs(input, extractor(partitions))
      .toDS
      .distribution()
      .write
      .orc(output)
  }

  def extractor(partitions: Long): OSMEntity => Option[(Long, Long)] = {
    case node: NodeEntity => Some((node.id % partitions, node.id))
    case _                => None
  }

  implicit class Entities(entities: Dataset[(Long, Long)])(implicit sparkSession: SparkSession) {

    def distribution(): DataFrame = {
      entities
        .withColumnRenamed("_1", "partition")
        .withColumnRenamed("_2", "id")
        .createTempView("ids_per_partition")

      sparkSession.sql(
        "select partition, max(id) as maxId, min(id) as minId, count(*) as ids from ids_per_partition group by partition"
      )
    }
  }

}
