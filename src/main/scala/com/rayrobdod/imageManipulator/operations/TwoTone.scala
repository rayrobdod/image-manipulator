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
import com.rayrobdod.swing.{ScalaSeqListModel, AbstractComboBoxModel}
import javax.swing.JSlider
import java.awt.event.{ActionEvent, ActionListener}
import javax.swing.event.{ChangeEvent, ChangeListener}

/**
 * Turns an image into a two-tone image by taking grayscale values
 * and comparing it with a threshold: if the value is greater than
 * the threshold, the pixel becomes black; otherwise the pixel becomes
 * white
 * 
 * @author Raymond Dodge
 * @version 2.0
 */
final class TwoTone extends Operation
{
	override val name = "Two-Tone"
	
	val divider = {
		val returnVal = new JSlider(0,255,128)
		returnVal.setMinorTickSpacing(16);
		returnVal.setPaintTicks(true);
		returnVal
	}
	
	override def setup(
				panel:javax.swing.JPanel,
				listener:ActionListener):Any =
	{
		panel.add(divider)
		
		divider.addChangeListener(new ChangeListener(){
			def stateChanged(e:ChangeEvent) {
				listener.actionPerformed(new ActionEvent(e, 1337, "SliderChanged"))
			}
		})
	}
	
	/** @since 2.0 */
	override def getImageOp:BufferedImageOp =
	{
		new TwoToneImageOp(divider.getValue)
	}
}

/**
 * Turns an image into a two-tone image by taking grayscale values
 * and comparing it with a threshold: if the value is greater than
 * the threshold, the pixel becomes black; otherwise the pixel becomes
 * white
 * 
 * @author Raymond Dodge
 * @version 19 Jun 2012
 * @version 2013 Feb 05 - now using trait LocalReplacement
 */
final class TwoToneImageOp(val threshold:Int) extends NoResizeBufferedImageOp with LocalReplacement
{
	override def createCompatibleDestImage(src:BufferedImage, cm:java.awt.image.ColorModel) = {
		new BufferedImage(src.getWidth, src.getHeight, BufferedImage.TYPE_BYTE_BINARY)
	}
	
	def pixelReplaceFunction(src:BufferedImage, dst:BufferedImage, x:Int) = {
		{{(y:Int) =>
			val srcColor = new Color(src.getRGB(x,y))
			val tone = (srcColor.getRed() + srcColor.getGreen() + srcColor.getBlue()) / 3 
			
			if (tone > threshold)
				dst.setRGB(x, y, Color.white.getRGB)
			else
				dst.setRGB(x, y, Color.black.getRGB)
		}}
	}
	
	def getRenderingHints() = null
}
