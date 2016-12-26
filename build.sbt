organization in ThisBuild := "com.thoughtworks.implicit-dependent-type"

libraryDependencies += "org.scala-lang" % "scala-compiler" % scalaVersion.value

libraryDependencies += "com.chuusai" %% "shapeless" % "2.3.2" % Test

scalacOptions in Test += "-Xplugin:" + (packageBin in Compile).value

libraryDependencies ++= (scalaBinaryVersion.value match {
  case "2.10" =>
    Seq(
      compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
      "org.scalamacros" %% "quasiquotes" % "2.1.0"
    )
  case _ =>
    Nil
})

crossScalaVersions := Seq("2.10.6", "2.11.8", "2.12.1")
