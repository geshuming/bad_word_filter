ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.11.11"

lazy val root = (project in file("."))
  .settings(
    name := "test_ng_word_filter",

    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core" % "2.3.2",
      "org.apache.spark" %% "spark-sql" % "2.3.2",
      "com.holdenkarau" %% "spark-testing-base" % "2.3.2_0.14.0" % "test",
    ),

    // Benchmark Test
    scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature", "-Xlint"),
    publishArtifact := false,
    libraryDependencies ++= Seq(
      "com.storm-enroute" %% "scalameter" % "0.18" % "test" // ScalaMeter version is set in version.sbt
    ),
    resolvers ++= Seq(
      "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
      "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases"
    ),
    testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework"),
    parallelExecution in Test := false,
    logBuffered := false
  )
