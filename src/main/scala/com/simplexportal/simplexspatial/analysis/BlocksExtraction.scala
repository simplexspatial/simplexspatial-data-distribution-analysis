/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Ángel Cervera Claudio
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.simplexportal.simplexspatial.analysis

import com.acervera.osm4scala.BlobTupleIterator
import io.tmos.arm.ArmMethods.manage
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.SparkContext
import org.openstreetmap.osmosis.osmbinary.fileformat.Blob
import org.slf4j.LoggerFactory

object BlocksExtraction {

  val logger = LoggerFactory.getLogger(BlocksExtraction.getClass.getName)

  def extractBlobs(input: String, output: String)(
      implicit ctx: SparkContext
  ): Long =
    ctx
      .binaryFiles(input)
      .flatMap {
        case (inputPath, stream) =>
          logger.info(s"Extracting Blobs from $inputPath")

          val fs = FileSystem.get(stream.getConfiguration)
          for (pbfIS <- manage(stream.open())) yield {
            BlobTupleIterator
              .fromPbf(pbfIS)
              .foldLeft(0L, Seq.empty[String]) {
                case ((counter, files), (_, blob)) =>
                  val outputFilePath = new Path(
                    output,
                    s"${new Path(inputPath).getName}_$counter"
                  )
                  writeBlob(blob, outputFilePath, fs)

                  (counter + 1, files :+ outputFilePath.toString)
              }
          }._2
      }
      .count()

  private def writeBlob(
      blob: Blob,
      outputFilePath: Path,
      fs: FileSystem
  ): Unit = {
    for (output <- manage(fs.create(outputFilePath))) {
      logger.info(s"Writing blob to $outputFilePath")
      blob.writeTo(output)
    }
  }
}
