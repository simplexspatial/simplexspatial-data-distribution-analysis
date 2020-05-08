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

import org.apache.spark.SparkContext
import org.scalatest.Suite

/**
  * Utility class that will create a new context in the begging and reuse it in all tests.
  */
trait SparkBaseRDDTesting extends SparkBaseTesting { self: Suite =>

  lazy implicit val sparkContext: SparkContext = {
    val tmpSC = SparkContext.getOrCreate(buildSparkConf())
    tmpSC.setLogLevel(errorLevel)
    tmpSC
  }

}
