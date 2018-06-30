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

import java.awt.event.{ActionListener, ActionEvent}
import javax.swing.{JFileChooser, JOptionPane}
import javax.imageio.ImageIO
import javax.swing.filechooser.FileFilter
import java.awt.image.BufferedImage
import java.io.IOException
import javax.imageio.spi.{ImageReaderSpi, IIORegistry}
import com.rayrobdod.util.Taskbar;
import scala.collection.JavaConversions.asScalaIterator

/**
 * An actionlistener that pops up a [[JFileChooser]], reads the image and then runs it as the input to the provided funtion
 * @param setImage a function that the listener will put the obtained image into
 */
final class SwingLoadListener(val setImage:Function1[BufferedImage, Any]) extends ActionListener
{
	def actionPerformed(e:ActionEvent)
	{
		val chooser = SwingSaveAndLoadListener.fileChooser.get
		chooser.setAcceptAllFileFilterUsed(true)
		
		val readers:Seq[ImageReaderSpi] = IIORegistry.getDefaultInstance.getServiceProviders(classOf[ImageReaderSpi], false).toSeq.distinct
		
		val fileFilters:Seq[FileFilter] = AllImageFormatsFilter.item +:
				(readers.map{ImageIoSpiToExtensionFilter}.sortBy{_.getDescription})
		chooser.resetChoosableFileFilters()
		fileFilters.foreach{chooser.addChoosableFileFilter(_)}
		
		chooser.setAccessory(null);
		
		val returnVal = chooser.showOpenDialog(null)
		
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			try
			{
				val image:BufferedImage = ImageIO.read(chooser.getSelectedFile())
				
				if (image != null) {
					setImage(image)
					if (false) { // if (Taskbar.tryLoadWindowsLibrary()) {
						Taskbar.addToRecentDocs(chooser.getSelectedFile().getPath())
					}
				} else {
					JOptionPane.showMessageDialog(null,
							"This application does not support the specified file's image format",
							"Unknown File Type",
							JOptionPane.ERROR_MESSAGE
					)
				}
			}
			catch
			{
				case e1:IOException => e1.printStackTrace();
			}
		}
	}
	
}

/**
 * A helper object for the [[SwingLoadListener]] and [[SwingSaveListener]]
 * classes that caches a JFileChooser - mostly so that there's some level
 * of persistence of a base directory between loads or saves. 
 */
object SwingSaveAndLoadListener
{
	/**
	 * used by the [[SwingLoadListener]] and [[SwingSaveListener]] so that
	 * there is some level of perminence when changing files
	 */
	val fileChooser:Option[JFileChooser] = {
		try {
			Some(new JFileChooser).filter{Function.const(! java.awt.GraphicsEnvironment.isHeadless())}
		} catch {
			case x:java.security.AccessControlException => None
		}
	}
	
	/**
	 * returns true iff this expects SwingLoadListener and SwingSaveListener
	 * to be able to execute without problems
	 */
	def canBeUsed:Boolean = {this.fileChooser.isDefined}
}
