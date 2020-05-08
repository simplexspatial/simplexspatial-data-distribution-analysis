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

import org.apache.spark.{SparkConf, SparkContext}
import org.scalatest.Suite

/**
  * Utility class that will create a new session in the begging and reuse it in all tests.
  *
  */
trait SparkBaseTesting {

  /**
    * Descriptive name used to create the session.
    * The default one is the name of the class, but better if overwrite with more descriptive one.
    */
  val testName: String = this.getClass.getSimpleName

  val localCores: Option[Int] = None
  val errorLevel: String = "ERROR" // Valid log levels include: ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN

  def buildSparkConf(): SparkConf =
    new SparkConf()
      .setMaster(s"local[${localCores.getOrElse("*")}]")
      .setAppName(testName)
      .set("spark.ui.enabled", "false")
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .set("spark.sql.warehouse.dir", "./target/test-spark-warehouse")

}
