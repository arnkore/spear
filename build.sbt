lazy val modules: Seq[ProjectReference] = Seq(
  `spear-core`,
  `spear-docs`,
  `spear-examples`,
  `spear-local`,
  `spear-repl`,
  `spear-trees`,
  `spear-utils`
)

lazy val spear = {
  lazy val repl = taskKey[Unit]("Runs the Spear REPL.")

  Project(id = "spear", base = file("."))
    .aggregate(modules: _*)
    // Creates a SBT task alias "repl" that starts the REPL within an SBT session.
    .settings(repl := (Compile / run).toTask("").value)
}

def spearModule(name: String): Project =
  Project(id = name, base = file(name))
    .enablePlugins(commonPlugins: _*)
    .settings(commonSettings)

lazy val `spear-utils` = spearModule("spear-utils")
  .settings(libraryDependencies ++= Dependencies.logging)
  .settings(libraryDependencies ++= Dependencies.scala)
  .settings(libraryDependencies ++= Dependencies.testing)

lazy val `spear-trees` = spearModule("spear-trees")
  .dependsOn(`spear-utils` % "compile->compile;test->test")

lazy val `spear-core` = spearModule("spear-core")
  .dependsOn(`spear-trees` % "compile->compile;test->test")
  .settings(libraryDependencies ++= Dependencies.fastparse)
  .settings(libraryDependencies ++= Dependencies.typesafeConfig)

lazy val `spear-local` = spearModule("spear-local")
  .dependsOn(`spear-core` % "compile->compile;test->test")

lazy val `spear-repl` = spearModule("spear-repl")
  .dependsOn(`spear-core` % "compile->compile;test->test")
  .dependsOn(`spear-local` % "compile->compile;test->test;compile->test")
  .enablePlugins(JavaAppPackaging)
  .settings(runtimeConfSettings)
  .settings(javaPackagingSettings)
  .settings(libraryDependencies ++= Dependencies.ammonite)
  .settings(libraryDependencies ++= Dependencies.scopt)

lazy val `spear-examples` = spearModule("spear-examples")
  .dependsOn(`spear-core`, `spear-local`)
  .enablePlugins(JavaAppPackaging)
  .settings(runtimeConfSettings)
  .settings(javaPackagingSettings)

lazy val `spear-docs` = spearModule("spear-docs")
  .dependsOn(`spear-core`, `spear-local`)
  .enablePlugins(SphinxPlugin)

lazy val javaPackagingSettings = {
  import NativePackagerHelper.directory

  Seq(
    // Adds the "conf" directory into the package.
    Universal / mappings ++= directory(baseDirectory(_.getParentFile / "conf").value),
    // Adds the "conf" directory to runtime classpath (relative to "$app_home/../lib").
    scriptClasspath += "../conf"
  )
}

lazy val commonPlugins = Seq(
  // For Scala code formatting
  SbtScalariform,
  // For Scala test coverage reporting
  ScoverageSbtPlugin
)

lazy val commonSettings = {
  val buildSettings = Seq(
    organization := "spear",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := Dependencies.Versions.scala,
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature"),
    scalacOptions ++= Seq("-Ywarn-unused-import", "-Xlint"),
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-g", "-Xlint:-options")
  )

  val commonTestSettings = Seq(
    // Disables parallel test execution to ensure logging order.
    Test / parallelExecution := false,
    // Does not fork a new JVM process to run the tests.
    Test / fork := false,
    // Shows duration and full exception stack trace
    Test / testOptions += Tests.Argument("-oDF")
  )

  val commonDependencySettings = {
    Seq(
      // Avoids copying managed dependencies into `lib_managed`
      retrieveManaged := false,
      // Enables extra resolvers
      resolvers ++= Dependencies.extraResolvers,
      // Disables auto conflict resolution
      conflictManager := ConflictManager.strict,
      // Explicitly overrides all conflicting transitive dependencies
      dependencyOverrides ++= Dependencies.overrides.toSeq
    )
  }

  val scalariformPluginSettings = {
    import com.typesafe.sbt.SbtScalariform.ScalariformKeys.preferences
    import scalariform.formatter.preferences.PreferencesImporterExporter.loadPreferences

    Seq(
      preferences := loadPreferences("scalariform.properties")
    )
  }

  val taskSettings = Seq(
    // Note: scalastyle automatic integration has syntax issues in sbt 1.x
    // Scalastyle plugin is available and can be run manually with: sbt scalastyle
    // The plugin works correctly but automatic integration syntax differs from sbt 0.13
  )

  Seq(
    buildSettings,
    commonTestSettings,
    commonDependencySettings,
    scalariformPluginSettings,
    taskSettings
  ).flatten
}

lazy val runtimeConfSettings = Seq(
  Runtime / unmanagedClasspath += baseDirectory { _.getParentFile / "conf" }.value
)
