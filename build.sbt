name := "Image Manipulator"

organization := "com.rayrobdod"

organizationHomepage := Some(new URL("http://rayrobdod.name/"))

version := "1.0.5-SNAPSHOT"

scalaVersion := "2.9.3"

crossScalaVersions ++= Seq("2.11.0-M4", "2.10.2", "2.9.1")

libraryDependencies += ("com.rayrobdod" %% "utilities" % "1.0.0-SNAPSHOT")

exportJars := true

mainClass := Some("com.rayrobdod.imageManipulator.main.Main")

packageOptions in (Compile, packageBin) <+= (scalaVersion, sourceDirectory).map{(scalaVersion:String, srcDir:File) =>
    val manifest = new java.util.jar.Manifest(new java.io.FileInputStream(srcDir + "/main/MANIFEST.MF"))
    //
    manifest.getAttributes("scala/").putValue("Implementation-Version", scalaVersion)
    //
    Package.JarManifest( manifest )
}

dependencyClasspath in Compile += new Attributed( new File("C:/Program Files/Java/jdk1.7.0_21/jre/lib/javaws.jar"))(AttributeMap.empty)


excludeFilter in unmanagedSources in Compile := new FileFilter{
	def accept(n:File) = {
		val abPath = n.getAbsolutePath().replace('\\', '/')
		(
			(abPath endsWith "com/rayrobdod/imageManipulator/main/DisambigMain.java") ||
			(abPath endsWith "com/rayrobdod/imageManipulator/main/Win7Main.java")
		)
	}
}



//normalizedName.????
//addArtifact( Artifact("image-manipulator", "pack200+gz", "gz"), packageBinPack )



(managedResources in Compile) <+= (resourceManaged in Compile).map{new File(_, "com_rayrobdod_util_Win7Taskbar.dll")}

// proguard
proguardSettings

proguardType := "mini" 

ProguardKeys.inputs in Proguard ~= {(x:Seq[File]) => x.dropRight(1)}

ProguardKeys.options in Proguard <+= (baseDirectory in Compile, proguardType).map{"-include '"+_+"/"+_+".proguard'"}

ProguardKeys.inputFilter in Proguard := { file =>
  if (file.name.startsWith("image-manipulator"))
    None
  else
    Some("**.class")
}

// anon-fun-reduce
//autoCompilerPlugins := true

//addCompilerPlugin("com.rayrobdod" %% "anon-fun-reduce" % "1.0.0")

//unmanagedSources in Compile += new File("""C:\Users\Raymond\Documents\Programming\Java\ScalaParserPlugin\src\CommonAnonFuns.scala""")

