package com.rayrobdod.imageManipulator.build

import sbt._
import Keys._
import scala.collection.immutable.Seq

object CopyDllPlugin extends AutoPlugin {
	object autoImport {
		val copyDll = TaskKey[File]("copy-dll", "")
		val copyDllIn = SettingKey[File]("copy-dll-in", "")
		val copyDllOut = SettingKey[File]("copy-dll-out", "")
	}
	import autoImport._
	
	override lazy val projectSettings = Seq(
		copyDllIn := unmanagedBase.value / "Taskbar.dll",
		copyDllOut := crossTarget.value / "proguard" / "Taskbar.dll",
		copyDll := {
			java.nio.file.Files.copy(
				copyDllIn.value.toPath,
				copyDllOut.value.toPath,
				java.nio.file.StandardCopyOption.REPLACE_EXISTING
			).toFile
		}
	)
	
	override def trigger = allRequirements
}
