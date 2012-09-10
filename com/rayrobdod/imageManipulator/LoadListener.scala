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

import java.awt.event.{ActionListener, ActionEvent}
import javax.swing.JFileChooser
import javax.imageio.ImageIO
import javax.imageio.ImageIO.{getReaderFileSuffixes => readerSuffixes,
			getReaderMIMETypes => readerMime,
			getReaderFormatNames => readerFormats
}
import javax.swing.filechooser.{FileNameExtensionFilter, FileFilter}
import java.awt.image.{RenderedImage, BufferedImage}
import java.io.IOException

/**
 * An actionlistener that pops up a LoadDialog, reads teh image and then puts in in a particular function 
 * 
 * @param setImage a function that the listener will put the obtained image into
 * @author Raymond Dodge
 * @version 2012 Jun 18
 * @version 2012 Jun 19 - making filechooser persistent
 * @version 2012 Aug 16 - Changed fileFilters to dynamically get listeners from
			ImageIO#getReaderFileSuffixes and to use a lot of maps 
 */
class LoadListener(val setImage:Function1[BufferedImage, Any]) extends ActionListener
{
	def actionPerformed(e:ActionEvent)
	{
		val chooser = SaveAndLoadListener.fileChooser
		chooser.setAcceptAllFileFilterUsed(true)
		
		val fileFilters:Seq[FileFilter] =
			AllImageFormatsFilter.item +: ImageIO.getReaderFileSuffixes
					.map{_.toLowerCase}.distinct
					.map{ImageExtensionToExtensionFilter}
					.diff(Seq(None)).map{_.get}.toList
		chooser.resetChoosableFileFilters()
		fileFilters.foreach{chooser.addChoosableFileFilter(_)}
		
		val returnVal = chooser.showOpenDialog(null)
		
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			try
			{
				val image:BufferedImage = ImageIO.read(chooser.getSelectedFile())
				
				setImage(image)
			}
			catch
			{
				case e1:IOException => e1.printStackTrace();
			}
		}
	}
}

/**
 * A helper that allows Save listeners and Load listeners to have some level
 * of persistence.
 * 
 * @author Raymond Dodge
 * @version 2012 Jun 19
 */
object SaveAndLoadListener
{
	val fileChooser = new JFileChooser
}
