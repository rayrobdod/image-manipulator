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

import scala.collection.immutable.Seq
import scala.collection.JavaConversions.iterableAsScalaIterable
import java.util.ServiceLoader
import java.awt.image.{BufferedImage, BufferedImageOp}

/**
 * A funtion that changes an image into an new image using some type of
 * manipulation
 * 
 * As this is a service, an implementing class should have a 0-arg constructor.
 * @author Raymond Dodge
 * @version 2012 Jun 18
 * @version 2012 Jun 19 - renamed from Manipulations to Operations
 * @version 2012 Jun 19 - added ActionListener parameter to setup
 */
trait Operation
{
	/** returns a display value for this Image Manipulation */
	def name:String
	
	/**
	 * A hook to let an operation add operations to a JPanel, so extra inputs
	 * can be provided to an ImageOp
	 * 
	 * @param panel the JPanel to add components to
	 * @param listener A listener that contains an action to update the preview image
	 */
	def setup(panel:javax.swing.JPanel,
				listener:java.awt.event.ActionListener):Any
	
	/**
	 * A hook to modify an image.
	 * Should return a new image without modifying the original image
	 * @deprecated
	 * @param input the input image
	 * @return a modified image
	 */
	final def apply(input:BufferedImage):BufferedImage = this.getImageOp.filter(input, null)
	
	/**
	 * Retuns an ImageOp, based on the state of the setup
	 * @since 2.0
	 */
	def getImageOp:BufferedImageOp
	
	// Temporary Stopgap
	override def toString = name
}

/**
 * A service provider for getting Manipulations
 * @author Raymond Dodge
 * @version 2012 Jun 18
 * @version 2012 Jun 19 - renamed from Manipulations to Operations
 */
object Operation
{
	/** A service loader that lists the known Maipulations */
	val serviceLoader = ServiceLoader.load[Operation](
			Class.forName("com.rayrobdod.imageManipulator.Operation").asInstanceOf[Class[Operation]])
	
	/** The values from the serviceLoader, turned into a Seq for convenience */
	def serviceSeq:Seq[Operation] = {
		Seq.empty ++ iterableAsScalaIterable(serviceLoader)
	}
	
}
