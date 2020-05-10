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
import org.apache.spark.sql.Row
import org.scalatest.matchers.should.Matchers

import scala.util.Random

class NodeIdDistributionSpec
    extends org.scalatest.wordspec.AnyWordSpecLike
    with Matchers
    with SparkBaseSQLTesting {
  "Calculate distribution" should {
    "correctly" in {

      import NodeIdDistribution._
      import sparkSession.implicits._

      val data = Seq(
        NodeEntity(10, 10.1, -10.1, Map.empty),
        NodeEntity(20, 20.1, -10.1, Map.empty),
        NodeEntity(30, 30.1, -10.1, Map.empty),
        NodeEntity(11, 11.1, -10.1, Map.empty),
        NodeEntity(21, 21.1, -10.1, Map.empty),
        NodeEntity(32, 32.1, -10.1, Map.empty),
        WayEntity(100, Seq(10, 20, 30), Map.empty)
      )

      val result = sparkSession
        .createDataset(data.flatMap(NodeIdDistribution.extractor(10)(_)))
        .distribution

      result.sort($"partition".asc).collect().toSet shouldBe (
        Set(
          Row(0, 30, 10, 3),
          Row(1, 21, 11, 2),
          Row(2, 32, 32, 1)
        )
      )
    }
  }
}
