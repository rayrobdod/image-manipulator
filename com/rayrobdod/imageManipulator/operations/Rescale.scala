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
import com.rayrobdod.swing.{ScalaSeqListModel, AbstractComboBoxModel}
import javax.swing.JComboBox

/**
 * 
 * @author Raymond Dodge
 * @version 2012 Jun 19
 */
final class Rescale extends Operation
{
	override val name = "Rescale"
	
	override def setup(
				panel:javax.swing.JPanel,
				listener:java.awt.event.ActionListener):Any = {}
				
	override def apply(src:BufferedImage):BufferedImage =
	{
		// TODO: input/output
		new RescaleImageOp(0,255).filter(src, null)
	}
	
}

/**
 * 
 * @author Raymond Dodge
 * @version 2012 Jun 19
 */
final class RescaleImageOp(val min:Int, val max:Int) extends NoResizeBufferedImageOp
{
	if (0 > min || min > max || max > 255) throw new IllegalArgumentException("The following must hold: 0 < min < max < 255")
	
	def filter(src:BufferedImage, x:BufferedImage) = {
		val dst = Option(x).getOrElse(createCompatibleDestImage(src, null))
		if (!(dst.getWidth == src.getWidth && src.getHeight == dst.getHeight)) throw new IllegalArgumentException
		
		val (srcMin, srcMax) = (
			(0 until src.getWidth).foldLeft((255,0)){(minmax:(Int, Int), x:Int) => 
			(0 until src.getHeight).foldLeft(minmax){(minmax2:(Int, Int), y:Int) =>
				val color = new Color(src.getRGB(x,y))
				
				((
					Seq(minmax2._1, color.getRed, color.getGreen, color.getBlue).min,
					Seq(minmax2._2, color.getRed, color.getGreen, color.getBlue).max
				))
			}}
		)
		
		(0 until src.getWidth).foreach{(x:Int) => 
		(0 until src.getHeight).foreach{(y:Int) =>
			val srcColor = new Color(src.getRGB(x,y))
			val newRed   = (((srcColor.getRed.toFloat   - srcMin) / (srcMax - srcMin) * (max - min)) + min).intValue
			val newGreen = (((srcColor.getGreen.toFloat - srcMin) / (srcMax - srcMin) * (max - min)) + min).intValue
			val newBlue  = (((srcColor.getBlue.toFloat  - srcMin) / (srcMax - srcMin) * (max - min)) + min).intValue
			
			val dstColor = new Color(newRed, newGreen, newBlue)
			
			dst.setRGB(x, y, dstColor.getRGB())
		}}
		
		dst
	}
	
	def getRenderingHints() = null
}
