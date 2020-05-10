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

import com.acervera.osm4scala.EntityIterator
import com.acervera.osm4scala.model.OSMEntity
import io.tmos.arm.ArmMethods.manage
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.openstreetmap.osmosis.osmbinary.fileformat.Blob
import org.slf4j.LoggerFactory

import scala.reflect.ClassTag

object Common {

  val logger = LoggerFactory.getLogger(Common.getClass.getName)

  /**
    * Apply a local transformation to every entity and generate an RDD.
    *
    * @param folder
    * @param localTransformation
    * @param ctx
    * @tparam T
    * @return
    */
  def fromBlobs[T: ClassTag](folder: String, localTransformation: OSMEntity => Option[T])(
      implicit ctx: SparkContext
  ): RDD[T] =
    ctx.binaryFiles(folder).flatMap {
      case (_, stream) =>
        for (blobIS <- manage(stream.open())) yield {
          EntityIterator
            .fromBlob(Blob.parseFrom(blobIS))
            .flatMap(e => localTransformation(e))
        }
    }

}
