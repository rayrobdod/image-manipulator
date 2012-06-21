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
import javax.imageio.ImageIO.{getWriterFileSuffixes => writerSuffixes,
			getWriterMIMETypes => writerMime,
			getWriterFormatNames => writerFormats
}
import javax.swing.filechooser.{FileNameExtensionFilter, FileFilter}
import java.awt.image.RenderedImage
import java.io.IOException

/**
 * An actionlistener that pops up a SaveDialog and puts the image returned
 * from the function into the selected file
 * 
 * @author Raymond Dodge
 * @version 2012 Jun 18
 * @version 2012 Jun 19 - making filechooser persistent
 */
class SaveListener(val getImage:Function0[RenderedImage]) extends ActionListener
{
	def actionPerformed(arg0:ActionEvent)
	{
		val chooser = SaveAndLoadListener.fileChooser
		chooser.setAcceptAllFileFilterUsed(false)
		
		// TODO: allow other image writers based on which ones exist
		val fileFiltersToSuffix:Map[FileFilter, String] = Map(
			(new FileNameExtensionFilter("Bitmap (bmp)", "bmp"), "bmp"),
			(new FileNameExtensionFilter("Wireless Bitmap (wbmp)", "wbmp"), "wbmp"),
			(new FileNameExtensionFilter("Joint Photographic Experts Group format (jpeg)", "jpeg", "jpg"), "jpeg"),
			(new FileNameExtensionFilter("Portable Network Graphic (png)", "png"), "png")
		)
		
		chooser.resetChoosableFileFilters()
		val fileFilters = fileFiltersToSuffix.keys
		fileFilters.foreach{chooser.addChoosableFileFilter(_)}
		
		val returnVal = chooser.showSaveDialog(null);
		
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			try
			{
				ImageIO.write(getImage(), 
						fileFiltersToSuffix(chooser.getFileFilter()),
						chooser.getSelectedFile()
				)
			}
			catch
			{
				case e1:IOException => e1.printStackTrace();
			}
		}
	}
}
