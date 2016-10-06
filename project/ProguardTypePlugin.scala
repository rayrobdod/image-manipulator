package com.rayrobdod.imageManipulator.build

import sbt._
import Keys._
import scala.collection.immutable.Seq
import com.typesafe.sbt.SbtProguard.{ProguardKeys, Proguard}

object ProguardTypePlugin extends AutoPlugin {
	object autoImport {
		val proguardType = settingKey[String]("The strength of proguard compression")
	}
	import autoImport._
	
	override lazy val projectSettings = Seq(
		proguardType := "mini", // "micro"
		ProguardKeys.options in Proguard += {
			val baseDir = (baseDirectory in Compile).value
			val proguardTyp = proguardType.value
			val settingsFile = baseDir / (proguardTyp + ".proguard")
			
			s"-include '${settingsFile}'"
		}
	)
	
	// override def requires = com.typesafe.sbt.SbtProguard 
	override def trigger = allRequirements
}
