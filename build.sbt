name := "Image Manipulator"

organization := "com.rayrobdod"

organizationHomepage := Some(new URL("http://rayrobdod.name/"))

version := "1.6-SNAPSHOT"

scalaVersion := "2.11.8"

crossScalaVersions ++= Seq("2.11.8")

resolvers += ("rayrobdod" at "http://ivy.rayrobdod.name/")

libraryDependencies += ("com.rayrobdod" %% "utilities" % "20160112")

dependencyClasspath in Compile += new Attributed( new File("C:/Program Files/Java/jdk1.8.0_91/jre/lib/javaws.jar"))(AttributeMap.empty)

exportJars := true

mainClass := Some("com.rayrobdod.imageManipulator.main.Main")

javacOptions in Compile ++= Seq("-Xlint:deprecation", "-Xlint:unchecked", "-source", "1.7", "-target", "1.7")

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-target:jvm-1.7")

scalacOptions ++= Seq("-Ywarn-unused-import", "-Ywarn-unused", "-Xlint:_", "-Xlint:-adapted-args")


packageOptions in (Compile, packageBin) += {
	val manifest = new java.util.jar.Manifest()
	manifest.getEntries().put("scala/", {
		val attrs = new java.util.jar.Attributes()
		attrs.putValue("Implementation-Title", "Scala")
		attrs.putValue("Implementation-URL", "http://www.scala-lang.org/")
		attrs.putValue("Implementation-Version", scalaVersion.value)
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
proguardSettings

proguardType := "mini" 

ProguardKeys.proguardVersion in Proguard := "5.2.1"

ProguardKeys.inputs in Proguard ~= {(x:Seq[File]) => x.dropRight(1)}

ProguardKeys.inputFilter in Proguard := { file =>
  if (file.name.startsWith("image-manipulator"))
    None
  else
    Some("**.class")
}

artifactPath in Proguard <<= (artifactPath in Proguard, proguardType, version).apply{(orig:File, level:String, version:String) =>
	orig.getParentFile() / ("imageManipulator-" + version + "-full-" + level + ".jar")
}

