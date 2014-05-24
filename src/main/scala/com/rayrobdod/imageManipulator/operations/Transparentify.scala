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
import java.awt.image.{BufferedImage, RenderedImage, BufferedImageOp}
import java.awt.Color

/**
 * A image operation that makes one color and uses it as a mask; all pixels of
 * that color are turned into transparent pixels
 * @author Raymond Dodge
 * @version 2.0
 */
final class Transparentify extends Operation
{
	override val name = "Transparentify"
	
	override def setup(
				panel:javax.swing.JPanel,
				actionListener:java.awt.event.ActionListener):Any = {
		val chooser = new javax.swing.JColorChooser
	}
	
	/** @since 2.0 */
	override def getImageOp:BufferedImageOp =
	{
		new TransparentifyImageOp((src) => new Color(src.getRGB(0,0)))
	}
}

/**
 * A image operation that makes one color and uses it as a mask; all pixels of
 * that color are turned into transparent pixels
 * @author Raymond Dodge
 * @version 2012 Jun 19
 * @version 2013 Feb 05 - now using trait LocalReplacement
 *
 * @constructor Makes a TransparentifyImageOp from a color
 * @since 2.0
 * @param 
 */
final class TransparentifyImageOp(val maskColor:Function1[BufferedImage,Color]) extends NoResizeBufferedImageOp with LocalReplacement
{
	/**
	 * Makes a TransparentifyImageOp from a RGB color code
	 * @param maskColor the color in the image to change to transparent
	 */
	 def this(maskColor:Color) = this{(a) => maskColor}
	/**
	 * Makes a TransparentifyImageOp from a RGB color code
	 * @param maskRGB an RGB color code to make transparent
	 */
	def this(maskRGB:Int) = this(new Color(maskRGB))
	
	override def createCompatibleDestImage(src:BufferedImage, cm:java.awt.image.ColorModel) = {
		new BufferedImage(src.getWidth, src.getHeight, BufferedImage.TYPE_INT_ARGB)
	}
	
	def pixelReplaceFunction(src:BufferedImage, dst:BufferedImage, x:Int) = {
		{{(y:Int) =>
			dst.setRGB(x, y, src.getRGB(x, y))
			
			if (src.getRGB(x,y) == maskColor(src).getRGB)
			{
				dst.getAlphaRaster.setPixel(x,y,Array(0,0,0,0,0,0))
			}
		}}
	}
	
	def getRenderingHints() = null
}
