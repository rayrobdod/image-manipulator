name := "Image Manipulator"

organization := "com.rayrobdod"

organizationHomepage := Some(new URL("http://rayrobdod.name/"))

homepage := Some(new URL("http://rayrobdod.name/programming/java/programs/imageManipulator/"))

version := "1.6-SNAPSHOT"

scalaVersion := "2.11.8"

crossScalaVersions ++= Seq("2.11.8")

dependencyClasspath in Compile += new Attributed( new File("C:/Program Files/Java/jdk1.8.0_161/jre/lib/javaws.jar"))(AttributeMap.empty)

exportJars := true

mainClass := Some("com.rayrobdod.imageManipulator.main.Main")

javacOptions in Compile ++= Seq("-Xlint:deprecation", "-Xlint:unchecked", "-source", "1.7", "-target", "1.7")

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-target:jvm-1.7")

scalacOptions ++= Seq("-Ywarn-unused-import", "-Ywarn-unused", "-Xlint:_")


packageOptions in (Compile, packageBin) += {
	val manifest = new java.util.jar.Manifest()
	manifest.getEntries().put("scala/", {
		val attrs = new java.util.jar.Attributes()
		attrs.putValue("Implementation-Title", "Scala")
		attrs.putValue("Implementation-URL", "http://www.scala-lang.org/")
		attrs.putValue("Implementation-Version", scalaVersion.value)
		attrs
	})
	manifest.getEntries().put("com/rayrobdod/imageManipulator/frameIcon.png", {
		val attrs = new java.util.jar.Attributes()
		attrs.putValue("Content-Type", "image/png")
		attrs
	})
	manifest.getEntries().put("com/rayrobdod/imageManipulator/frameIcon.ico", {
		val attrs = new java.util.jar.Attributes()
		attrs.putValue("Content-Type", "image/x-icon")
		// attrs.putValue("Content-Type", "image/vnd.microsoft.icon")
		attrs
	})
	manifest.getEntries().put("CHANGES.txt", {
		val attrs = new java.util.jar.Attributes()
		attrs.putValue("Content-Type", "text/plain")
		attrs
	})
	manifest.getEntries().put("README.txt", {
		val attrs = new java.util.jar.Attributes()
		attrs.putValue("Content-Type", "text/plain")
		attrs
	})
	Package.JarManifest( manifest )
}

excludeFilter in unmanagedSources in Compile := new FileFilter{
	def accept(n:File) = {
		val abPath = n.getAbsolutePath().replace('\\', '/')
		(
			(abPath endsWith "DisambigMain.java") ||
			(abPath endsWith "Win7Main.java") ||
			false
		)
	}
}



licenses += (("3-point BSD", new URL("http://opensource.org/licenses/BSD-3-Clause") ))

mappings in (Compile, packageSrc) += ((baseDirectory.value / "LICENSE.txt"), "LICENSE.txt" )

mappings in (Compile, packageBin) += ((baseDirectory.value / "LICENSE.txt"), "LICENSE.txt" )


// proguard
enablePlugins(SbtProguard)

val proguardType = settingKey[String]("level of proguard compression")

proguardType := "mini" // "micro"

proguardVersion in Proguard := "6.0"

proguardInputs in Proguard ~= {(x:Seq[File]) => x.dropRight(1)}

proguardInputFilter in Proguard := { file =>
  if (file.name.startsWith("image-manipulator"))
    None
  else
    Some("**.class")
}

proguardOptions in Proguard += "-include " + ((baseDirectory in Compile).value / (proguardType.value + ".proguard"))

proguardOptions in Proguard := (proguardOptions in Proguard).value.map{line =>
	if (line contains "scala") {line.replaceAll("-libraryjars (.+)", "-injars $1(**.class)")} else {line}
}

artifactPath in Proguard := {
	(artifactPath in Proguard).value.getParentFile() / (s"imageManipulator-fatjar-${proguardType.value}.jar")
}

