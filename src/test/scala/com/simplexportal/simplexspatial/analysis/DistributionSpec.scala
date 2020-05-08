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

import com.acervera.osm4scala.model.{NodeEntity, OSMEntity, WayEntity}
import org.scalatest.matchers.should.Matchers

import scala.util.Random

class DistributionSpec
    extends org.scalatest.wordspec.AnyWordSpecLike
    with Matchers
    with SparkBaseRDDTesting {
  "Calculate distribution" should {
    "correctly" in {

      val randomData: Seq[OSMEntity] = (1 to 200).map(id =>
        NodeEntity(id, Random.nextDouble() % 90, Random.nextDouble() % 180, Map.empty)
      ) ++ (1000 to 1010)
        .map(id => WayEntity(id, (1L to 10L).map(_ % 200), Map.empty))

      import Distribution._

      val result = sparkContext
        .makeRDD(
          randomData,
          2
        )
        .distribution(
          Distribution.modPartitioner(10)
        )

      result.collect().toSet shouldBe (
        Set(
          (0, Metrics(190, 100, 20)),
          (1, Metrics(181, 101, 20)),
          (2, Metrics(182, 102, 20)),
          (3, Metrics(183, 103, 20)),
          (4, Metrics(184, 104, 20)),
          (5, Metrics(185, 105, 20)),
          (6, Metrics(186, 96, 20)),
          (7, Metrics(187, 97, 20)),
          (8, Metrics(188, 98, 20)),
          (9, Metrics(189, 99, 20))
        )
      )
    }
  }
}
