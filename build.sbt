name := "simplexspatial-data-distribution-analysis"
version := "0.1"
scalaVersion := "2.11.12"

lazy val scalatestVersion = "3.1.1"
lazy val osm4scalaVersion = "1.0.3"
lazy val scoptVersion = "3.7.1"
lazy val logbackVersion = "1.2.3"
lazy val sparkVersion = "2.4.5"
lazy val arm4sVersion = "1.1.0"
lazy val betterFilesVersion = "3.8.0"

libraryDependencies ++= Seq(
  "com.acervera.osm4scala" %% "osm4scala-core" % osm4scalaVersion exclude ("ch.qos.logback", "logback-classic"),
  "com.github.scopt" %% "scopt" % scoptVersion,
  "io.tmos" %% "arm4s" % arm4sVersion, // until Scala 2.13 :(
  "com.github.pathikrit" %% "better-files" % betterFilesVersion,
  "org.apache.spark" %% "spark-core" % sparkVersion % Provided,
  "org.apache.spark" %% "spark-sql" % sparkVersion % Provided,
  "org.apache.spark" %% "spark-streaming" % sparkVersion % Provided,
  "org.scalatest" %% "scalatest" % scalatestVersion % Test,
  "org.scalactic" %% "scalactic" % scalatestVersion % Test
)
