import sbt._
import Keys._
import java.util.zip.{ZipInputStream, ZipOutputStream, ZipEntry}

object ImageManipulatorBuild extends Build {
	
	val proguardType = SettingKey[String]("proguard-type", "The strength of proguard compression")
	val proguardTypeSetting = proguardType := "mini"
//	val proguardTypeSetting = proguardType := "micro"
	
	val copyDll = TaskKey[File]("copy-dll", "")
	val copyDllIn = SettingKey[File]("copy-dll-in", "")
	val copyDllOut = SettingKey[File]("copy-dll-out", "")
	
	val copyDllTasks = Seq(
		copyDllIn <<= (unmanagedBase).apply{_ / "Taskbar.dll"},
		copyDllOut <<= (crossTarget).apply{_ / "proguard" / "Taskbar.dll"},
		copyDll <<= (copyDllIn, copyDllOut).map{(inFile:File, outFile:File) =>
			java.nio.file.Files.copy(
					inFile.toPath, outFile.toPath,
					java.nio.file.StandardCopyOption.REPLACE_EXISTING
			).toFile
		}
	)
	
	
	
	lazy val root = Project(
			id = "imageManipulator",
			base = file("."),
			settings = Defaults.coreDefaultSettings ++
					copyDllTasks
	)
}
