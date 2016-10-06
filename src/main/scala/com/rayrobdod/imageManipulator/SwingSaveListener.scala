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
import javax.swing.JFileChooser
import javax.imageio.ImageIO
import javax.swing.filechooser.FileFilter
import java.awt.image.RenderedImage
import java.io.IOException
import javax.imageio.spi.{ImageWriterSpi, IIORegistry}
import scala.collection.JavaConversions.asScalaIterator

/**
 * An actionlistener that pops up a [[JFileChooser]] and puts the image returned
 * from the function into the selected file
 * 
 * @author Raymond Dodge
 * @version 2012 Jun 18
 * @version 2012 Jun 19 - making filechooser persistent
 * @version 2012 Aug 16 - Changed fileFilters to dynamically get listeners from
			ImageIO#getWriterFileSuffixes and to use a lot of maps 
 * @version 2012 Sept 10 - renamed from SaveListener to SwingSaveListener
 * @version 2012 Sept 10 - changes due to change in signature of SwingSaveAndLoadListener.fileChooser
 * @version 2012 Nov 19 - making use ImageWriterSpis directly instead of indirect extension usage
 * @version 2012 Dec 27 - set the chooser's accessory to a component that will modify the ImageWriteParams
 * @version 2013 Feb 24 - make accessory reflect first filefilter shown upon first showing
 */
final class SwingSaveListener(val getImage:Function0[RenderedImage]) extends ActionListener
{
	def actionPerformed(arg0:ActionEvent)
	{
		val chooser = SwingSaveAndLoadListener.fileChooser.get
		chooser.setAcceptAllFileFilterUsed(false)
		
		val fileFiltersToSPI:Map[FileFilter, ImageWriterSpi] =
		{
			val writers:Seq[ImageWriterSpi] = IIORegistry.getDefaultInstance.getServiceProviders(classOf[ImageWriterSpi], false).toSeq.distinct
			val returnValue = writers.map{ImageIoSpiToExtensionFilter}.zip(writers).toMap
			
			returnValue
		}
		
		chooser.resetChoosableFileFilters()
		val fileFilters = fileFiltersToSPI.keys.toSeq.sortBy{_.getDescription}
		fileFilters.foreach{chooser.addChoosableFileFilter(_)}
		
		val acc = new ImageWriteParamAccessory(fileFiltersToSPI)
		chooser.setAccessory(new javax.swing.JScrollPane(acc,
				javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER))
		chooser.addPropertyChangeListener(acc)
		acc.p = Some(fileFiltersToSPI.head._2.createWriterInstance.getDefaultWriteParam)
		
		val returnVal = chooser.showSaveDialog(null);
		
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			try
			{
				val writerSPI = fileFiltersToSPI(chooser.getFileFilter())
				val writer = writerSPI.createWriterInstance()
				
				val output = ImageIO.createImageOutputStream(chooser.getSelectedFile())
				writer.setOutput(output)
				
				val image = new javax.imageio.IIOImage( getImage(), null, null )
				
				try {
					writer.write( null, image, acc.p.orNull )
				} finally {
					// TODO: display errors in a swing manner 
					output.close()
				}
				
				/* ImageIO.write(getImage(), 
						fileFiltersToSPI(chooser.getFileFilter()).getFormatNames()(0),
						chooser.getSelectedFile()
				) */
			}
			catch
			{
				case e1:IOException => e1.printStackTrace();
			}
		}
	}
}
