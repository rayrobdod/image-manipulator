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

package com.rayrobdod.imageManipulator.operations

import com.rayrobdod.imageManipulator.Operation
import java.awt.image.{BufferedImage, BufferedImageOp}

/**
 * An identity image manipulation
 * 
 * This is the both the Operation and the ImageOp.
 * @author Raymond Dodge
 * @version 2012 Jun 18
 * @version 2012 Jun 19 - moved from com.rayrobdod.imageManipulator.manipulations to com.rayrobdod.imageManipulator.operations
 * @version 2013 Feb 05 - now using trait LocalReplacement
 */
final class Identity extends Operation with NoResizeBufferedImageOp with LocalReplacement 
{
	override val name = "Identity"
	
	/** Does nothing */
	override def setup(panel:javax.swing.JPanel,
				actionListener:java.awt.event.ActionListener):Any = {}
	
	/** calls this.filter(inputImage, null) */
	override def apply(inputImage:BufferedImage):BufferedImage = filter(inputImage, null)
	
	/** Makes the (x,y) pixel in dst the same as the (x,y) pixel in src */
	def pixelReplaceFunction(src:BufferedImage, dst:BufferedImage, x:Int) = {
		{{(y:Int) => dst.setRGB(x, y, src.getRGB(x, y))}}
	}
	
	/** returns null */
	def getRenderingHints() = null
}
