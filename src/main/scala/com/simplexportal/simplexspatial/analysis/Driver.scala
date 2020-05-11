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

import com.simplexportal.simplexspatial.analysis.AppConfig._
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object Driver {

  def main(args: Array[String]): Unit = {
    parser.parse(args, AppConfig()) match {
      case Some(cfg) =>
        val conf = new SparkConf().setAppName("Data distribution")
        implicit val spark = SparkSession
          .builder()
          .appName("Data distribution")
          .config(conf)
          .getOrCreate()
        implicit val sparkContext = spark.sparkContext

        cfg.cmd match {
          case MOD =>
            NodeIdDistribution
              .run(cfg.input, cfg.output, cfg.modPartitions)
          case AppConfig.TILE =>
            TileDistribution
              .run(cfg.input, cfg.output, cfg.latPartitions, cfg.lonPartitions)
          case EXTRACT =>
            println(s"Extracted ${BlocksExtraction.extractBlobs(cfg.input, cfg.output)} blocks")
          case x =>
            println(s"Unknown command $x")
            System.exit(-2)
        }

      case None => System.exit(-1)
    }

  }
}
