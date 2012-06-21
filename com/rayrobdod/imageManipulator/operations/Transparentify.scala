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

package com.rayrobdod.imageManipulator.operations

import com.rayrobdod.imageManipulator.Operation
import java.awt.image.{BufferedImage, RenderedImage}
import java.awt.Color

/**
 * A image operation that makes one color and uses it as a mask; all pixels of
 * that color are turned into transparent pixels
 * @author Raymond Dodge
 * @version 2012 Jun 18 - 19
 * @version 2012 Jun 19 - moved from com.rayrobdod.imageManipulator.manipulations to com.rayrobdod.imageManipulator.operations
 */
final class Transparentify extends Operation
{
	override val name = "Transparentify"
	
	override def setup(
				panel:javax.swing.JPanel,
				actionListener:java.awt.event.ActionListener):Any = {
		val chooser = new javax.swing.JColorChooser
	}
	
	override def apply(src:BufferedImage):BufferedImage =
	{
		new TransparentifyImageOp(src.getRGB(0,0)).filter(src, null)
	}
}

/**
 * A image operation that makes one color and uses it as a mask; all pixels of
 * that color are turned into transparent pixels
 * @author Raymond Dodge
 * @version 2012 Jun 19
 */
final class TransparentifyImageOp(val maskColor:Color) extends NoResizeBufferedImageOp
{
	def this(maskRGB:Int) = this(new Color(maskRGB))
	
	def filter(src:BufferedImage, x:BufferedImage) = {
		val dst = Option(x).getOrElse(createCompatibleDestImage(src, null))
		if (!(dst.getWidth == src.getWidth && src.getHeight == dst.getHeight)) throw new IllegalArgumentException
		
		(0 until src.getWidth).foreach{(x:Int) => 
		(0 until src.getHeight).foreach{(y:Int) =>
			dst.setRGB(x, y, src.getRGB(x, y))
			
			if (src.getRGB(x,y) == maskColor.getRGB)
			{
				dst.getAlphaRaster.setPixel(x,y,Array(0,0,0,0,0,0))
			}
		}}
		
		dst
	}
	
	def getRenderingHints() = null
}
