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
import javax.swing.filechooser.{FileNameExtensionFilter, FileFilter}
import java.awt.image.{RenderedImage, BufferedImage}
import java.io.IOException
import javax.jnlp.{ServiceManager, FileOpenService, FileSaveService}
import javax.jnlp.UnavailableServiceException;
import javax.imageio.{ImageIO, ImageWriter, ImageWriteParam}
import javax.imageio.ImageIO.{getReaderFileSuffixes => readerSuffixes,
			getWriterFileSuffixes => writerSuffixes
}
import javax.imageio.spi.{ImageWriterSpi, IIORegistry}
import LoggerInitializer.{javaWSSaveLogger => saveLogger}
import java.util.logging.{Level, LogRecord}

/**
 * An actionlistener that asks the JavaWS services to open a file dialog and read an Image file
 * 
 * @param setImage a function that the listener will put the obtained image into
 * @author Raymond Dodge
 * @version 2012 Sept 10
 * @since 1.0.2
 */
class JavaWSLoadListener(val setImage:Function1[BufferedImage, Any]) extends ActionListener
{
	def actionPerformed(e:ActionEvent)
	{
		ImageIO.scanForPlugins()
		
		val fileOpenService = JavaWSSaveAndLoadListener.fileOpenService.get
		val fileContents = fileOpenService.openFileDialog("", readerSuffixes);
		if (fileContents != null) {
			val image = ImageIO.read( fileContents.getInputStream() );
			setImage(image)
		}
	}
}

/**
 * An actionlistener that asks the JavaWS services to open a file dialog and read an Image file
 * 
 * @param setImage a function that the listener will put the obtained image into
 * @author Raymond Dodge
 * @version 2012 Sept 10
 * @version 2012 Sept 11 - failed attempt at inserting logging to figure out why this thing refuses to work.
 * @version 2012 Sept 11 - fixing so that this works - more threads!
 */
class JavaWSSaveListener(val getImage:Function0[RenderedImage]) extends ActionListener
{
	// Apparently, JavaWS doesn't give permission to use logging at all.
	def actionPerformed(e:ActionEvent)
	{
		try {
//			saveLogger.entering("JavaWSSaveListener", "actionPerformed")
//			new SwingWindowHandler().publish(new LogRecord(Level.FINE, "entering JavaWSSaveLsitener"))
			
			import java.io.{PipedInputStream, PipedOutputStream}
			
			// this cannot run on the AWT thread
			// due to a sleep or wait call
			new Thread(new Runnable() {
				def run() = {
					import JavaWSSaveAndLoadListener.fileSaveService.{get => fileSaveService}
					val in = new PipedInputStream()
					val out = new PipedOutputStream(in)
					
//					saveLogger.entering("JavaWSSaveListener$AnonRunnable", "run")
					val format = JavaWSSaveAndLoadListener.userAskedFileFormat()
//					saveLogger.log(Level.FINER, "format is :" + format)
					
					format.foreach{(format:(ImageWriterSpi, Option[ImageWriteParam])) =>
//						saveLogger.entering("JavaWSSaveListener$AnonRunnable$anonForEach", "apply", format);
						
						val (writerSPI, param) = format
						new Thread(new Runnable() {
							def run() = {     
								val writer = writerSPI.createWriterInstance
								
								val output = ImageIO.createImageOutputStream(out)
								writer.setOutput(output)
								
								val image = new javax.imageio.IIOImage( getImage(), null, null )
								
								try {
									writer.write( null, image, param.orNull )
								} finally {
									// TODO: display errors 
									output.close()
									out.flush()
									out.close()
								}
							}
						}, "WriteImageToPipedStream").start
						
						fileSaveService.saveFileDialog("", writerSPI.getFileSuffixes, in, "");
					}
				}
			}, "Save Format Poll").start()
		
		} catch {
			case x:Throwable => new SwingWindowHandler().publish(x)
		}
	}
}

/**
 * Caches FileOpenService and FileSaveService instances.
 * 
 * @author Raymond Dodge
 * @version 2012 Sept 10
 */
object JavaWSSaveAndLoadListener
{
	/**
	 * The FileOpenService used by JavaWSLoadListener - for reusability and
	 * so that the cast only happens once.
	 */
	val fileOpenService = {
		try {
			Some(ServiceManager.lookup("javax.jnlp.FileOpenService")
					.asInstanceOf[FileOpenService])
		} catch {
			// TODO: verify that this is the correct exception to handle
			case x:UnavailableServiceException => None
			case x:java.lang.ClassNotFoundException => None
		}
	}
	
	/** 
	 * The FileSaveService used by JavaWSSaveListener - for reusability and
	 * so that the cast only happens once.
	 */
	val fileSaveService = {
		try {
			Some(ServiceManager.lookup("javax.jnlp.FileSaveService")
					.asInstanceOf[FileSaveService])
		} catch {
			// TODO: verify that this is the correct exception to handle
			case x:UnavailableServiceException => None
			case x:java.lang.ClassNotFoundException => None
		}
	}
	
	/**
	 * true if this expects JavaWSLoadListener and JavaWSSaveListener
	 *  to be able to execute without problems
	 */
	def canBeUsed = {
		fileOpenService.isDefined && fileSaveService.isDefined
	}
	
	def userAskedFileFormat():Option[(ImageWriterSpi, Option[ImageWriteParam])] = {
		import javax.swing.{JDialog, JLabel, JButton, JList, JPanel,
				DefaultListCellRenderer, ListCellRenderer}
		import java.awt.BorderLayout.{NORTH, SOUTH, EAST}
		import java.awt.event.{WindowAdapter, WindowEvent, ActionListener, ActionEvent}
		import scala.collection.JavaConversions.asScalaIterator
		import com.rayrobdod.swing.ScalaSeqListModel
		import javax.swing.event.{ListSelectionListener, ListSelectionEvent}
		
		var buttonUsedToCloseDialog = -1
		val OK_BUTTON = 1
		val CANCEL_BUTTON = 0
		
		val availiableSpi:Seq[ImageWriterSpi] = 
				IIORegistry.getDefaultInstance.getServiceProviders(
						classOf[ImageWriterSpi], false).toSeq.distinct
		
		val writerList = new JList[ImageWriterSpi](new ScalaSeqListModel(availiableSpi))
		writerList.setCellRenderer(new ListCellRenderer[ImageWriterSpi]() {
			val base = new DefaultListCellRenderer();
			
			override def getListCellRendererComponent(list:JList[_ <: ImageWriterSpi], value:ImageWriterSpi,
						index:Int, isSelected:Boolean, cellHasFocus:Boolean) =
			{
				base.getListCellRendererComponent(list:JList[_],
						value.getFileSuffixes.tail.foldLeft("[" + value.getFileSuffixes.head){
							(soFar:String, next:String) => soFar + "," + next;
						} + "]",
						index, isSelected, cellHasFocus) 
			}
		})
		val writerParam = new ImageWriteParamAccessory(null)
		writerList.addListSelectionListener(new ListSelectionListener {
			override def valueChanged(e:ListSelectionEvent) {
				writerParam.p = Option(writerList.getSelectedValue).map{_.createWriterInstance.getDefaultWriteParam}
			}
		})
		
		val dialogLock = new Object()
		class DialogAnswerListener(closeButton:Int) extends WindowAdapter with ActionListener
		{
			override def windowClosing(e:WindowEvent) {
				dialogLock.synchronized
				{
					buttonUsedToCloseDialog = closeButton
					dialog.setVisible(false)
					dialogLock.notifyAll()
				}
			}
			
			override def actionPerformed(e:ActionEvent) {
				dialogLock.synchronized
				{
					buttonUsedToCloseDialog = closeButton
					dialog.setVisible(false)
					dialogLock.notifyAll()
				}
			}
		}
		
		// using object rather than val so that DialogAnswerListener can see it.
		object dialog extends JDialog(null:JDialog, "Choose format to save as")
		{
			add(new JLabel("Chose which format to save as"), NORTH)
			add(writerList)
			add(writerParam, EAST)
			add({
				val p = new JPanel()
				p.add({
					val b = new JButton("OK")
					b.addActionListener(new DialogAnswerListener(OK_BUTTON))
					b
				})
				p.add({
					val b = new JButton("Cancel")
					b.addActionListener(new DialogAnswerListener(CANCEL_BUTTON))
					b
				})
				p
			}, SOUTH)
			addWindowListener(new DialogAnswerListener(CANCEL_BUTTON))
			
			pack()
			setVisible(true)
		}
		
		while (dialog.isVisible)
		{
			dialogLock.synchronized
			{
				// wait until user answers
				dialogLock.wait()
			}
		}
		
		return Option((writerList.getSelectedValue, writerParam.p)).filter{(x:Any) => buttonUsedToCloseDialog == OK_BUTTON}
	}
}
