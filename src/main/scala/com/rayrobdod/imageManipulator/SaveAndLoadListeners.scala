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

import java.awt.image.BufferedImage
import java.awt.event.ActionListener

/**
 * An object molding methods to select between Load or Save listeners.
 * 
 * @author Raymond Dodge
 * @version 2012 Sept 10
 */
object SaveAndLoadListeners
{
	/**
	 * returns an ActionListener that, when activated, loads an image using a
	 * method avaliable in the current system and uses it as a parameter to the
	 * choosen function
	 */
	@throws(classOf[NoAvailableFileOperatorsException])
	def loadListener(setImage:Function1[BufferedImage, Any]):ActionListener =
	{
		if (SwingSaveAndLoadListener.canBeUsed) {
			new SwingLoadListener(setImage)
		} else if (JavaWSSaveAndLoadListener.canBeUsed) {
			new JavaWSLoadListener(setImage)
		} else {
			throw new NoAvailableFileOperatorsException
		}
	}
	
	/**
	 * returns an ActionListener that, when activated, saves the image returned
	 * by the parameter function using a method avaliable in the current system.
	 */
	@throws(classOf[NoAvailableFileOperatorsException])
	def saveListener(getImage:Function0[BufferedImage]):ActionListener =
	{
		if (SwingSaveAndLoadListener.canBeUsed) {
			new SwingSaveListener(getImage)
		} else if (JavaWSSaveAndLoadListener.canBeUsed) {
			new JavaWSSaveListener(getImage)
		} else {
			throw new NoAvailableFileOperatorsException
		}
	}
	
	/**
	 * An exception thrown by [[SaveAndLoadListeners]]'s methods in the case where
	 * no Save or Load listeners think they will be able to work. 
	 */
	final class NoAvailableFileOperatorsException(msg:String) extends Exception(msg)
	{
		def this() = {this("No SaveAndLoadListeners work under the current environment ")}
	}
}
