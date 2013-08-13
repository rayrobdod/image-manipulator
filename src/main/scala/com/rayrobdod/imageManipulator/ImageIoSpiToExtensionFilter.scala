/*
	Copyright (c) 2012-2013, Raymond Dodge
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
import javax.imageio.spi.ImageReaderWriterSpi;

/**
 * A function that creates a File Filter for the specified ImageReaderWriterSpi
 * 
 * @author Raymond Dodge
 * @version 2012 Nov 19
 * @version 2013 Jul 24 - now capable of dealing with null or empty FileSuffix lists
 * @since 1.0.3
 */
object ImageIoSpiToExtensionFilter extends Function1[ImageReaderWriterSpi, FileFilter]
{
	/**
	 */
	def apply(spi:ImageReaderWriterSpi):FileFilter = {
		val suffixes = spi.getFileSuffixes.filter{_ != ""}
		
		val formatName = suffixes match {
			// I really don't like any of the default names
			case Array("bmp")  => "Bitmap"
			case Array("wbmp") => "Wireless Bitmap"
			case Array("gif")  => "Graphical Interchange Format"
			case Array("jpg", "jpeg") => "Joint Photographic Experts Group format"
			case Array("png")  => "Portable Network Graphic"
			case _ => {
				val descripts = spi.getFormatNames.map{_.toLowerCase}.distinct
				descripts.tail.foldLeft(descripts.head){_+" OR "+_}
			}
		}
		
		if (suffixes == null || suffixes.isEmpty) {
			new AcceptAllWithNewDescriptionFileFilter(formatName)
		} else {
			val ext = suffixes.tail.foldLeft(suffixes.head){_ + "; " + _}.toLowerCase
			val descript = formatName + " (" + ext + ")"
			
			new FileNameExtensionFilter(descript, suffixes(0)) // TODO: array
		}
	}
	
	
	private final class AcceptAllWithNewDescriptionFileFilter(
		override val getDescription:String
	) extends FileFilter {
		def accept(f:java.io.File) = true
	}

}
