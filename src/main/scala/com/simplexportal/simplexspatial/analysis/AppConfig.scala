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

import com.simplexportal.simplexspatial.analysis.AppConfig.{Command, NoneCmd}

case class AppConfig(
    cmd: Command = NoneCmd,
    input: String = "",
    output: String = "",
    modPartitions: Int = 0,
    latPartitions: Int = 0,
    lonPartitions: Int = 0
)

object AppConfig {
  trait Command {
    def id: String
  }

  object NoneCmd extends Command {
    override def id: String = ""
  }
  object MOD extends Command {
    override def id: String = "mod"
  }
  object TILE extends Command {
    override def id: String = "tile"
  }
  object EXTRACT extends Command {
    override def id: String = "extract"
  }

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
        opt[Int]("partitions")
          .abbr("p")
          .required()
          .action((v, args) => args.copy(modPartitions = v))
      )

    cmd(TILE.id)
      .action((_, cfg) => cfg.copy(cmd = TILE))
      .text("Calculate distribution partitioning data by Tile")
      .children(
        opt[Int]("latPartitions")
          .abbr("latP")
          .required()
          .action((v, cfg) => cfg.copy(latPartitions = v)),
        opt[Int]("lonPartitions")
          .abbr("lonP")
          .required()
          .action((v, cfg) => cfg.copy(lonPartitions = v))
      )

    checkConfig {
      case cfg: AppConfig if cfg.cmd == "" => failure("partitioner not present.")
      case _                               => success
    }
  }
}
