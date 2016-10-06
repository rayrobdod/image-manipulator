/*
	Copyright (c) 2013, Raymond Dodge
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
import java.awt.image.BufferedImage
import java.awt.Color
import java.awt.event.ActionListener

/**
 * Tetronic Oscillocope images are encoded as JPEGs.
 * This is despite the fact that their images use 7 colors, before jpegification.
 * It is a huge waste of space. Not even counting the fact that it makes
 * correcting the mistake even harder.
 * 
 * @author Raymond Dodge
 * @version 2013 Mar 20
 */
final class TetronicDejpeger extends Operation
{
	override val name = "Tektronic deJPEGer"
	
	val colors = Seq(
			Color.white, new Color(237, 237, 237), new Color(203, 203, 203),
			Color.black, new Color(27, 171, 26),   new Color(255, 160, 0),
			new Color(0, 193, 193)
	);
	
	override def setup(
				panel:javax.swing.JPanel,
				listener:ActionListener):Any = {}
				
	override def apply(src:BufferedImage):BufferedImage =
	{
		new Dejpeger(colors).filter(src, null)
	}
}

/**
 * 
 * @author Raymond Dodge
 * @version 2013 Mar 20
 */
final class Dejpeger(val colors:Seq[Color]) extends NoResizeBufferedImageOp with LocalReplacement
{
	def pixelReplaceFunction(src:BufferedImage, dst:BufferedImage, x:Int) = {
		{{(y:Int) =>
			val srcColor = new Color(src.getRGB(x,y))
			
			import math.abs;
			
			val dstColor = colors.minBy{(tryColor:Color) =>
				2 * abs(tryColor.getRed()   - srcColor.getRed()  ) +
				2 * abs(tryColor.getGreen() - srcColor.getGreen()) +
				2 * abs(tryColor.getBlue()  - srcColor.getBlue() )
			}
			
			dst.setRGB(x, y, dstColor.getRGB)
		}}
	}
	
	def getRenderingHints() = null
}
