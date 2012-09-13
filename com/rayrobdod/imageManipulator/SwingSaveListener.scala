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
import javax.imageio.ImageIO.{getWriterFileSuffixes => writerSuffixes}
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
 * @version 2012 Aug 16 - Changed fileFilters to dynamically get listeners from
			ImageIO#getWriterFileSuffixes and to use a lot of maps 
 * @version 2012 Sept 10 - renamed from SaveListener to SwingSaveListener
 * @version 2012 Sept 10 - changes due to change in signature of SwingSaveAndLoadListener.fileChooser
 */
class SwingSaveListener(val getImage:Function0[RenderedImage]) extends ActionListener
{
	def actionPerformed(arg0:ActionEvent)
	{
		val chooser = SwingSaveAndLoadListener.fileChooser.get
		chooser.setAcceptAllFileFilterUsed(false)
		
		val fileFiltersToSuffix:Map[FileFilter, String] =
		{
			val uniqueExtensionList = ImageIO.getWriterFileSuffixes
					.map{_.toLowerCase}.distinct
			
			val returnValue = uniqueExtensionList
					.map{ImageExtensionToExtensionFilter}
					.zip(uniqueExtensionList)
					.filterNot({(x:Tuple2[Option[FileFilter], String]) => x._1} andThen {(x) => x == None})
					.map{(x) => ((x._1.get, x._2))}
					.toMap
			
			// debug
			//returnValue.foreach{(x) => println(x._1, x._2)}
			
			returnValue
		}
		
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
