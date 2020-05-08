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

import better.files.File
import org.scalatest.BeforeAndAfter
import org.scalatest.matchers.should.Matchers

class BlocksExtractionSpec
    extends org.scalatest.wordspec.AnyWordSpecLike
    with Matchers
    with BeforeAndAfter
    with SparkBaseRDDTesting {

  val outputTestFolder = "target/extractTest"

  before {
    File(outputTestFolder).delete(true)
  }

  after {
    File(outputTestFolder).delete(true)
  }

  "Extract blocks" in {
    BlocksExtraction
      .extractBlobs("src/test/resources/monaco-latest.osm.pbf", outputTestFolder)
      .shouldBe(9)

    File(outputTestFolder).list.size shouldBe 18
  }
}
