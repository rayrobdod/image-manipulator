/*
	Copyright (c) 2012, Raymond Dodge
	All rights reserved.
	
	Redistribution and use in source and binary forms, with or without
	modification, are permitted provided that the following conditions are met:
		* Redistributions of source code must retain the above copyright
		  notice, this list of conditions and the following disclaimer.
		* Redistributions in binary form must reproduce the above copyright
		  notice, this list of conditions and the following disclaimer in the
		  documentation and/or other materials provided with the distribution.
		* Neither the name "Image Manipulator" nor the names of its contributors
		  may be used to endorse or promote products derived from this software
		  without specific prior written permission.
	
	THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
	ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
	WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
	DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
	DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
	(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
	LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
	ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
	(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
	SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package com.rayrobdod.imageManipulator

import javax.imageio.ImageIO
import javax.imageio.ImageIO.{getReaderFileSuffixes => readerSuffixes,
			getReaderMIMETypes => readerMime,
			getReaderFormatNames => readerFormats
}
import javax.swing.filechooser.{FileNameExtensionFilter, FileFilter}

/**
 * A function that creates a File Filter for the specified file extension
 * 
 * @author Raymond Dodge
 * @version 2012 Aug 16
 * @version 2012 Sept 11 - taking the description method out of the apply method 
 * @since 1.0.1
 */
object ImageExtensionToExtensionFilter extends Function1[String, Option[FileFilter]]
{
	/**
	 * @param extension - the file name extension to create a filter for
	 * @return A Some(FileFilter), where the FileFilter filters for the extension
	 		and has an appropriate extension. None if there is a better extension
	 		for the filter
	 * @todo consider making the return an Either[FileFilter,String]?
			The String would be an other extension to use
	 * @trythis cache the FileFilters?
	 */
	def apply(extension:String):Option[FileFilter] = {
		val ext = extension.toLowerCase
		
		val descript = description(ext)
		
		ext match {
			case "jpg" => None
			case "jpeg" => Some(new FileNameExtensionFilter(descript, "jpeg", "jpg"))
			case _ => Some(new FileNameExtensionFilter(descript, ext))
		}
	}
	
	def description(extension:String):String = {
		val ext = extension.toLowerCase
		
		ext match {
			case "bmp" => "Bitmap (bmp)"
			case "wbmp" => "Wireless Bitmap (wbmp)"
			case "gif" => "Graphical Interchange Format (gif)"
			case "jpeg" | "jpg" => "Joint Photographic Experts Group format (jpeg)"
			case "png" => "Portable Network Graphic (png)"
			case _ => ext.toUpperCase + " file (" + ext + ")"
		}
	}
}
