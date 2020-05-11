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

object TileDistribution {
  val logger = LoggerFactory.getLogger(TileDistribution.getClass.getName)

  def run(input: String, output: String, latPartitions: Int, lonPartitions: Int)(
      implicit ctx: SparkContext,
      spark: SparkSession
  ): Unit = {
    import spark.implicits._
    Common
      .fromBlobs(input, extractor(latPartitions, lonPartitions))
      .toDS
      .distribution()
      .write
      .orc(output)
  }

  def extractor(
      latPartitions: Int,
      lonPartitions: Int,
      decimalPrecision: Byte = 6
  ): OSMEntity => Option[(Long, Long)] = {
    val PRECISION_ROUNDING: Int = Math.pow(10, decimalPrecision).toInt

    def latPartition(lat: Double): Int =
      ((lat + 90) * PRECISION_ROUNDING).toInt / ((180 * PRECISION_ROUNDING) / latPartitions)

    def lonPartition(lon: Double): Int =
      ((lon + 180) * PRECISION_ROUNDING).toInt / ((360 * PRECISION_ROUNDING) / lonPartitions)

    (entity: OSMEntity) =>
      entity match {
        case node: NodeEntity =>
          Some((latPartition(node.latitude) << 16 | lonPartition(node.longitude), node.id))
        case _ => None
      }
  }

  implicit class Entities(entities: Dataset[(Long, Long)])(implicit sparkSession: SparkSession) {

    def distribution(): DataFrame = {
      entities
        .withColumnRenamed("_1", "partition")
        .withColumnRenamed("_2", "id")
        .createTempView("tile_ids_per_partition")

      val mask = 0x0000ffff

      sparkSession.sql(
        "select" +
          " int(shiftrightunsigned(partition, 16)) as latPart," +
          " int(partition & 4095) as lonPart," +
          " max(id) as maxId," +
          " min(id) as minId," +
          " count(*) as ids" +
          " from tile_ids_per_partition group by partition"
      )
    }
  }

}
