import sbt._
import Keys._
import java.util.zip.{ZipInputStream, ZipOutputStream, ZipEntry}

object ImageManipulatorBuild extends Build {
	
	val proguardType = SettingKey[String]("proguard-type", "The strength of proguard compression")
	val proguardTypeSetting = proguardType := "mini"
//	val proguardTypeSetting = proguardType := "micro"
	
	
	
	
	lazy val root = Project(
			id = "imageManipulator",
			base = file("."),
			settings = Defaults.defaultSettings
	)
}