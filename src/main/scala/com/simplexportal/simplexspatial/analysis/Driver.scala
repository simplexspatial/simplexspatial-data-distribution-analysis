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

object Driver {

  trait Command {
    def id: String
  }
  trait Partitioner

  object NoneCmd extends Command {
    override def id: String = ""
  }
  object MOD extends Command with Partitioner {
    override def id: String = "mod"
  }
  object TILE extends Command with Partitioner {
    override def id: String = "tile"
  }
  object EXTRACT extends Command {
    override def id: String = "extract"
  }

  case class AppConfig(
      cmd: Command = NoneCmd,
      input: String = "",
      output: String = "",
      modPartitions: Long = 0,
      latPartitions: Long = 0,
      lonPartitions: Long = 0
  )

  val parser = new scopt.OptionParser[AppConfig]("osm-data-distribution") {
    head("osm-data-distribution")

    opt[String]("osm")
      .abbr("i")
      .required()
      .text("OSM file path")
      .action((v, cfg) => cfg.copy(input = v))

    opt[String]("outputFolder")
      .abbr("o")
      .required()
      .text("Output folder path")
      .action((v, cfg) => cfg.copy(output = v))

    cmd(EXTRACT.id)
      .action((_, cfg) => cfg.copy(cmd = EXTRACT))
      .text("Extract blobs from osm pbf file")
    cmd(MOD.id)
      .action((_, cfg) => cfg.copy(cmd = MOD))
      .text("Calculate distribution using a module of the node id as partitioner.")
      .children(
        opt[Long]("partitions")
          .abbr("p")
          .required()
          .action((v, args) => args.copy(modPartitions = v))
      )
    cmd(TILE.id)
      .action((_, cfg) => cfg.copy(cmd = TILE))
      .text("Calculate distribution partitioning data by Tile")
      .children(
        opt[Long]("latPartitions")
          .abbr("latP")
          .required()
          .action((v, cfg) => cfg.copy(latPartitions = v)),
        opt[Long]("lonPartitions")
          .abbr("lonP")
          .required()
          .action((v, cfg) => cfg.copy(lonPartitions = v))
      )
    checkConfig {
      case cfg: AppConfig if cfg.cmd == "" => failure("partitioner not present.")
      case _                               => success
    }
  }

  def main(args: Array[String]): Unit = {
    parser.parse(args, AppConfig()) match {
      case Some(cfg) =>
        val conf = new SparkConf().setAppName("Data distribution")
        implicit val sparkContext = new SparkContext(conf)

        cfg.cmd match {
          case p: Partitioner => ???
          case EXTRACT =>
            println(s"Extracted ${BlocksExtraction.extractBlobs(cfg.input, cfg.output)} blocks")
          case x =>
            println(s"Unknown command $x")
            System.exit(-2)
        }

      //        val partitioner = cfg.cmd match {
      //          case Commands.MOD =>
      //            Distribution.modPartitioner(cfg.modPartitions)
      //          case Commands.TILE =>
      //            Distribution.tilePartitioner(cfg.latPartitions, cfg.lonPartitions)
      //        }
      case None => System.exit(-1)
    }

  }
}
