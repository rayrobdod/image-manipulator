/*
	Copyright (c) 2012-2014, Raymond Dodge
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

import java.awt.image.{BufferedImage, BufferedImageOp}
import scala.collection.mutable.{Buffer => MSeq}


/**
 * 
 * @since 2.0
 */
final class FrameModel (
		val originalImage:BufferedImage,
		val operation:Operation
) {
 	lazy val originalImageScaled:BufferedImage =
 			scaleImage(originalImage)
	
	// Operations are mutable...
	def afterImage:BufferedImage =
			operation.apply(originalImage)
	
	// Operations are mutable...
	def afterImageScaled:BufferedImage =
			operation.apply(originalImageScaled)
	
}


/**
 * @TODO util(?)
 */
final class Observer[A](initialValue:A) {
	
	private var value = initialValue
	def apply() = this.value
	def update(newValue:A) = {
		value = newValue
		changeListeners.foreach{(l:Function[A,_]) => l.apply(value)}
	}
	
	// TODO: change type of listener to be better
	private val changeListeners = MSeq[Function1[A,_]]()
	def addChangeListener(l:Function1[A,_]) = {changeListeners += l}
	
}
