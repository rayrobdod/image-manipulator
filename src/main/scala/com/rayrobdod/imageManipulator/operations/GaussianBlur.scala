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
import java.awt.image.{BufferedImage, Kernel, ConvolveOp}
import javax.swing.JSlider
import java.awt.event.{ActionEvent, ActionListener}
import javax.swing.event.{ChangeEvent, ChangeListener}

/**
 * A gaussian blur
 * @author Raymond Dodge
 * @version 2013 Aug 06
 */
final class GaussianBlur extends Operation
{
	override val name = "Gaussian Blur"
	
	val divider = {
		val returnVal = new JSlider(5,50,15)
		returnVal.setMinorTickSpacing(5);
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
	
	override def apply(src:BufferedImage):BufferedImage =
	{
		val σ = divider.getValue * .1; // stdDev
		val σ2 = σ * σ; // I tried using ²; it didn't work
		
		val center = math.max(divider.getValue * 4 / 10, 3);
		val width = center * 2 - 1;
		val kernelBytes = new Array[Float](width * width);
		
		(0 until width).foreach{(x:Int) =>
		(0 until width).foreach{(y:Int) =>
			val Δx = x - center + 1
			val Δy = y - center + 1
			
			val newValue = (math.exp(-(Δx * Δx) / (2 * σ2)) *
				math.exp(-(Δy * Δy) / (2 * σ2))) / (2 * math.Pi * σ2)
			val index = x * width + y
			
			kernelBytes(index) = newValue.floatValue;
		}}
		
		val kernel = new Kernel(width, width, kernelBytes)
		val op = new ConvolveOp(kernel,
				ConvolveOp.EDGE_NO_OP, // really should do something decent here
				new java.awt.RenderingHints(new java.util.HashMap())
		)
		
		op.filter(src, null)
	}
}

