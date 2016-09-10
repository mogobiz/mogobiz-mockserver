organization in ThisBuild := "com.mogobiz.mockserver"

name := "mock-app"

version in ThisBuild := "0.1-SNAPSHOT"

scalaVersion in ThisBuild := "2.11.8"

crossScalaVersions in ThisBuild := Seq("2.11.8")

val mockServerV = "3.10.4"

val jacksonV = "2.7.3"

libraryDependencies in ThisBuild ++= Seq(
//  "com.fasterxml.jackson.module" %% "jackson-module-scala" % jacksonV,
  "com.fasterxml.jackson.core" % "jackson-annotations" % jacksonV,
  "com.fasterxml.jackson.core" % "jackson-core" % jacksonV,
  "com.fasterxml.jackson.core" % "jackson-databind" % jacksonV,
  "com.fasterxml.jackson.dataformat" % "jackson-dataformat-xml" % jacksonV,
  "org.codehaus.woodstox" % "woodstox-core-asl" % "4.4.1",
  "com.typesafe" % "config" % "1.2.1",
  "org.mock-server" % "mockserver-netty" % mockServerV,
//  "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2",
//  "org.specs2" %% "specs2" % "2.3.13" % Test,
  "junit" % "junit" % "4.12" % Test,
  "com.novocode" % "junit-interface" % "0.11" % Test,
  "com.sun.xml.messaging.saaj" % "saaj-impl" % "1.3.25" % Test,
  "xerces" % "xercesImpl" % "2.11.0" % Test
)

javacOptions in (ThisBuild, compile) ++= Seq("-source", "1.7", "-target", "1.7")

scalacOptions in (ThisBuild, compile) := Seq("-unchecked", "-deprecation", "-encoding", "utf8", "-target:jvm-1.7")

resolvers in ThisBuild ++= Seq(
  "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
  "ebiz repo" at "http://art.ebiznext.com/artifactory/libs-release-local",
  "ebiz snaphost" at "http://art.ebiznext.com/artifactory/libs-snapshot-local",
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"
)

publishTo in ThisBuild := {
  val repo = "http://boutique.masterbuild.net3-courrier.extra.laposte.fr/artifactory/"
  if (version.value.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at repo + "libs-snapshot-local")
  else
    Some("releases" at repo + "libs-release-local")
}

credentials in ThisBuild += Credentials(Path.userHome / ".ivy2" / ".credentials")

publishArtifact in (ThisBuild, Compile, packageSrc) := false

publishArtifact in (ThisBuild, Test, packageSrc) := false

parallelExecution in(ThisBuild, Test) := false

//publishMavenStyle in ThisBuild := true

lazy val common = project.in(file("mockserver-common"))

lazy val core = project.in(file("mockserver-core")).dependsOn(common)

lazy val mirakl = project.in(file("mockserver-mirakl")).dependsOn(common, core)

lazy val app = project.in(file(".")).dependsOn(common, core).aggregate(common, core, mirakl)

packSettings

publishPackArchiveTgz

packMain := Map("mockserver" -> "com.mogobiz.mockserver.cli.Main")

packExtraClasspath := Map("mockserver" -> Seq("${PROG_HOME}/conf", "${PROG_HOME}/ref"))

packResourceDir += (baseDirectory.value / "conf" -> "conf")

packResourceDir += (baseDirectory.value / "ref" -> "ref")

packGenerateWindowsBatFile := true

packExpandedClasspath := false

packJvmOpts := Map("mockserver" -> Seq("-Xmx2g", "-Dmockserver.logLevel=INFO"))

fork := true

