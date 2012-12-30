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
import javax.swing.filechooser.{FileNameExtensionFilter, FileFilter}
import java.awt.image.{RenderedImage, BufferedImage}
import java.io.IOException
import javax.imageio.spi.{ImageReaderSpi, IIORegistry}
import scala.collection.JavaConversions.asScalaIterator

/**
 * An actionlistener that pops up a LoadDialog, reads teh image and then puts in in a particular function 
 * 
 * @param setImage a function that the listener will put the obtained image into
 * @author Raymond Dodge
 * @version 2012 Jun 18
 * @version 2012 Jun 19 - making filechooser persistent
 * @version 2012 Aug 16 - Changed fileFilters to dynamically get listeners from
			ImageIO#getReaderFileSuffixes and to use a lot of maps 
 * @version 2012 Sept 10 - renamed from LoadListener to SwingLoadListener
 * @version 2012 Sept 10 - changes due to change in signature of SwingSaveAndLoadListener.fileChooser
 * @version 2012 Nov 19 - making use ImageReaderSpis directly instead of indirect extension usage
 * @version 2012 Dec 27 - clears the chooser's accessory before showing it
 */
class SwingLoadListener(val setImage:Function1[BufferedImage, Any]) extends ActionListener
{
	def actionPerformed(e:ActionEvent)
	{
		val chooser = SwingSaveAndLoadListener.fileChooser.get
		chooser.setAcceptAllFileFilterUsed(true)
		
		val readers:Seq[ImageReaderSpi] = IIORegistry.getDefaultInstance.getServiceProviders(classOf[ImageReaderSpi], false).toSeq.distinct
		
		val fileFilters:Seq[FileFilter] = AllImageFormatsFilter.item +:
					readers.map{ImageIoSpiToExtensionFilter}
		chooser.resetChoosableFileFilters()
		fileFilters.foreach{chooser.addChoosableFileFilter(_)}
		
		chooser.setAccessory(null);
		
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
 * @version 2012 Sept 10 - renamed from SaveAndLoadListener to SwingSaveAndLoadListener
 * @version 2012 Sept 10 - fileChooser now returns a Option[JFileChooser], rather than a JFileChooser 
 * @version 2012 Sept 10 - adding the canBeUsed method
 */
object SwingSaveAndLoadListener
{
	/**
	 * used by the SwingLoadListener and SwingSaveListener so that
	 * there is some level of perminence when changing files
	 * 
	 */
	val fileChooser:Option[JFileChooser] = {
		try {
			Some(new JFileChooser)
		} catch {
			case x:java.security.AccessControlException => None
		}
	}
	
	/**
	 * true if this expects SwingLoadListener and SwingSaveListener
	 *  to be able to execute without problems
	 */
	def canBeUsed:Boolean = {this.fileChooser.isDefined}
}
