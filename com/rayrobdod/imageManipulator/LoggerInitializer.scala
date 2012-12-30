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

import java.util.logging.{Logger, Level, ConsoleHandler,
		FileHandler, LogRecord, Handler}

/**
 * If a class needs a logging service, it is created here, and then
 * called where ever it's needed. 
 */
private[imageManipulator] object LoggerInitializer
{
//	val warningFileHandler = new FileHandler("%t/imageManipulator%g.log")
	val allSwingHandler = new SwingWindowHandler
	try {
		allSwingHandler.setLevel(Level.WARNING)
	} catch {
		case x:java.security.AccessControlException =>
			// ignore because Level.ALL isn't strictly neccessary.
	}
			
	
	val javaWSSaveLogger = Logger.getLogger(
			"com.rayrobdod.imageManipulator.JavaWSSaveListener")
	javaWSSaveLogger.addHandler(allSwingHandler)
	javaWSSaveLogger.setLevel(Level.WARNING)
}

/**
 * An attempt at creating a Handler that JavaWS will allow. And then I
 * discovered that logging is disallowed - not just the console or files.
 * 
 * @author Raymond Dodge
 * @version 2012 Sept 12
 * @todo move to util package if this can be used elsewhere
 */
class SwingWindowHandler extends Handler
{
	def close() {}
	def flush() {}
	
	def publish(throwable:Throwable)
	{
		this.publish(
			new LogRecord(Level.WARNING, throwable.getClass.getName) {
				setThrown(throwable)
			}
		)
	}
	
	def publish(record:LogRecord)
	{
		if (record.getThrown != null)
		{
			val x = record.getThrown
			
			import java.awt.Frame
			import javax.swing.{JDialog, JLabel, JTextArea}
			import java.io.{StringWriter, PrintWriter}
			
			new JDialog(null:Frame, x.getClass.getName) {
				private val stringWriter = new StringWriter
				x.printStackTrace(new PrintWriter(stringWriter))
				
				add(new JTextArea(stringWriter.toString))
				pack()
				setVisible(true)
			}
		}
		
		if (record.getThrown == null)
		{
			import java.awt.Frame
			import javax.swing.{JDialog, JLabel, JTextArea}
			import java.io.{StringWriter, PrintWriter}
			
			new JDialog(null:Frame, "") {
				add(new JTextArea(
					record.getSourceClassName() + '\n' +
					record.getSourceMethodName() + '\n' +
					record.getParameters() + '\n' +
					record.getMessage() 
				))
				pack()
				setVisible(true)
			}
		}
	}
}
