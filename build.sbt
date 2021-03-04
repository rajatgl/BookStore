name := "BookStore"

version := "0.1"

scalaVersion := "2.12.2"

resolvers ++= Seq("snapshots", "releases").map(Resolver.sonatypeRepo)
resolvers += Resolver.sonatypeRepo("snapshots")

coverageEnabled := true

libraryDependencies ++= Seq(

  //akka essentials
  "com.typesafe.akka" %% "akka-actor" % "2.5.32",
  "com.typesafe.akka" %% "akka-stream" % "2.5.32",
  "com.typesafe.akka" %% "akka-http" % "10.2.2",

  //scala-test dependency
  "org.scalatest" %% "scalatest" % "3.2.2" % Test,
  "org.scalatestplus" %% "mockito-3-4" % "3.2.3.0" % "test",
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.32",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.2.2",

  //smtp dependency for mailing
  "com.github.daddykotex" %% "courier" % "3.0.0-M2",

  //scala log feature
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2" ,
  "ch.qos.logback" % "logback-classic" % "1.2.3" % Runtime ,
  "ch.qos.logback" % "logback-core" % "1.2.3"

)