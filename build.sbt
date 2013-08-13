name := "Image Manipulator"

organization := "com.rayrobdod"

organizationHomepage := Some(new URL("http://rayrobdod.name/"))

version := "1.0.5"

scalaVersion := "2.9.3"

crossScalaVersions ++= Seq("2.11.0-M4", "2.10.2", "2.9.1")

libraryDependencies += ("com.rayrobdod" %% "utilities" % "1.0.0")

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


includeFilter in unmanagedSources in Compile := new FileFilter{
	def accept(n:File) = {
		val abPath = n.getAbsolutePath().replace('\\', '/')
		!(
			(abPath endsWith "com/rayrobdod/imageManipulator/main/DisambigMain.java") ||
			(abPath endsWith "com/rayrobdod/imageManipulator/main/Win7Main.java")
		) && ((abPath endsWith ".java") || (abPath endsWith ".scala"))
	}
}



//normalizedName.????
//addArtifact( Artifact("image-manipulator", "pack200+gz", "gz"), packageBinPack )



TaskKey[File]("package-bin-full") <<=
(fullClasspath in Compile, packageBin in Compile).map{(inputs:Seq[Attributed[File]], packageBinFile:File) => 
	val outputFile = new File(packageBinFile.toString.dropRight(4) + "-full-full.jar")
	val outputStream = new java.util.zip.ZipOutputStream(new java.io.FileOutputStream(outputFile))
	val inputFiles = inputs.map{_.data}
	//
	inputFiles.foreach{(inFile:File) =>
		if (inFile.isDirectory) {
			def readDirectory(parent:File):Any = parent.listFiles.foreach{(child:File) =>
				if (child.isDirectory) {readDirectory(child)}
				else {
					val inStream = new java.io.FileInputStream(child)
					val entryName = inFile.toPath.relativize(child.toPath).toString.replace('\\','/')
					val nextEntry = new java.util.zip.ZipEntry(entryName)
					//
					outputStream.putNextEntry(nextEntry)
					while (inStream.available() != 0) {
						outputStream.write(inStream.read())
					}
					inStream.close()
				}
			}
			readDirectory(inFile)
		} else {
			val inStream = new java.util.zip.ZipInputStream(new java.io.FileInputStream(inFile))
			def entryWithSameName(x:java.util.zip.ZipEntry) = {
				if (x == null) null
				else new java.util.zip.ZipEntry(x.getName)
			}
			//
			var nextEntry = entryWithSameName(inStream.getNextEntry())
			while (null != nextEntry) {
				if (!(nextEntry.getName contains "META-INF")) {
					outputStream.putNextEntry(nextEntry)
					while (inStream.available() != 0) {
						outputStream.write(inStream.read())
					}
				}
				nextEntry = entryWithSameName(inStream.getNextEntry())
			}
			inStream.close()
		}
	}
	outputStream.close()
	outputFile
}

// Not very flexible, this one.
// TODO: add to resourceGenerators
TaskKey[File]("compile-native", "compile native code") <<= 
(resourceManaged in Compile, sourceDirectory in Compile).map{(x:File, srcDir:File) =>
	val outputDir = x//new File(x, "native")
	outputDir.mkdirs()
	//
	val proc = java.lang.Runtime.getRuntime().exec(
			Array("""C:\Program Files (x86)\Microsoft Visual Studio 10.0\VC\bin\amd64\cl""",
					"-I", """C:\Program Files (x86)\Java\jdk1.7.0_07\include""",
					"-I", """C:\Program Files (x86)\Java\jdk1.7.0_07\include\win32""",
					"-I", """C:\Program Files (x86)\Microsoft Visual Studio 10.0\VC\include""",
					"-I", """C:\Program Files\Microsoft SDKs\Windows\v7.1\Include""",
					srcDir + """\cpp\com_rayrobdod_util_Win7Taskbar.cpp""",
					"/O1",
					"/EHsc",
					"/LD",
					"/LD",
					"/link",
					"""C:\Program Files\Microsoft SDKs\Windows\v7.1\Lib\x64\*.lib""",
					"""C:\Program Files (x86)\Microsoft Visual Studio 10.0\VC\lib\amd64\libcpmt.lib""",
					"""C:\Program Files (x86)\Microsoft Visual Studio 10.0\VC\lib\amd64\libcmt.lib""",
					"""C:\Program Files (x86)\Microsoft Visual Studio 10.0\VC\lib\amd64\oldnames.lib"""
			),
			null,
			outputDir
	)
	// This causes problems, apparently. The process won't end if
	// this line is called.
/*	val retVal = proc.waitFor();
	//
//	if (retVal != 0) {
		while (proc.getInputStream.available != 0) {
			java.lang.System.out.write(proc.getInputStream.read())
		}
		while (proc.getErrorStream.available != 0) {
			java.lang.System.out.write(proc.getErrorStream.read())
		}
//	}
//*/
	new File(outputDir, "com_rayrobdod_util_Win7Taskbar.dll")
}

(managedResources in Compile) <+= (resourceManaged in Compile).map{new File(_, "com_rayrobdod_util_Win7Taskbar.dll")}

// proguard
proguardSettings

proguardType := "mini" 

ProguardKeys.inputs in Proguard ~= {(x:Seq[File]) => x.dropRight(1)}

ProguardKeys.options in Proguard <+= (baseDirectory in Compile, proguardType).map{"-include '"+_+"/"+_+".proguard'"}

ProguardKeys.inputFilter in Proguard := { file =>
  file.name match {
    case "scala-library.jar" => Some("!META-INF/**,!library.properties")
    case "anon-fun-reduce_2.9.3.jar" => Some("!**")
    case "anon-fun-reduce_2.9.1.jar" => Some("!**")
    case "anon-fun-reduce_2.10.jar" => Some("!**")
    case "scala-compiler.jar" => Some("!**")
    case _                   => None
  }
}

// anon-fun-reduce
//autoCompilerPlugins := true

//addCompilerPlugin("com.rayrobdod" %% "anon-fun-reduce" % "1.0.0")

//unmanagedSources in Compile += new File("""C:\Users\Raymond\Documents\Programming\Java\ScalaParserPlugin\src\CommonAnonFuns.scala""")
