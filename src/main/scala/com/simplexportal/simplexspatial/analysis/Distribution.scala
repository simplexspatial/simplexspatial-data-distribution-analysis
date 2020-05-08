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
import org.apache.spark.rdd.RDD
import org.slf4j.LoggerFactory

object Distribution {

  val logger = LoggerFactory.getLogger(Distribution.getClass.getName)

  case class Metrics(minId: Long, maxId: Long, count: Long)

  def modPartitioner(partitions: Long): OSMEntity => Option[Long] = {
    case node: NodeEntity => Some(node.id % partitions)
    case _                => None
  }

  def tilePartitioner(latPartitions: Long, lonPartitions: Long): OSMEntity => Option[Long] = ???

  implicit class Entities(entities: RDD[OSMEntity]) {
    def distribution(
        partitioner: OSMEntity => Option[Long]
    ): RDD[(Long, Metrics)] =
      entities
        .flatMap(e => partitioner(e).map(part => (part, Metrics(e.id, e.id, 1))))
        .reduceByKey((a, b) =>
          a.copy(Math.max(a.maxId, b.maxId), Math.min(a.minId, a.minId), count = a.count + b.count)
        )
  }

//  def fromOSM(pbfFile: File)(implicit ctx: SparkContext): RDD[OSMEntity] = {
//    ctx.binaryFiles()
//    for (pbfIS <- manage(new FileInputStream(pbfFile))) {
//      EntityIterator.fromPbf(pbfIS)
//    }
//  }

}
