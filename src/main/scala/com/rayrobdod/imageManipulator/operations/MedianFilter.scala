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
 * 
 * @author Raymond Dodge
 * @version 2.0
 */
final class MedianFilter extends Operation
{
	override val name = "Median Filter"
	
	val divider = new JSlider(1,7,1)
	
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
		new MedianFilterImageOp(divider.getValue * 2 + 1)
	}
}

/**
 * Performs a MedianFilter on an image
 * 
 * @author Raymond Dodge
 * @version 2013 Feb 06
 * @param windowSize the size of the window to take the median of
 */
final class MedianFilterImageOp(val windowSize:Int) extends NoResizeBufferedImageOp with LocalReplacement
{
	def pixelReplaceFunction(src:BufferedImage, dst:BufferedImage, x:Int) = {
		{{(y:Int) =>
        	val startX = math.max(0, x - windowSize/2)
			val startY = math.max(0, y - windowSize/2)
			val widthX = math.min(src.getWidth  - x, windowSize)
			val widthY = math.min(src.getHeight - y, windowSize)
			
			val sortedRgbs:Seq[Int] = try {
				val rgbs = src.getRGB(startX, startY, widthX, widthY, null, 0, widthX);
				rgbs.sorted(AverageGreyscaleColorOrdering.on[Int]{(i:Int) => new Color(i)})
			} catch {
				case x:java.lang.ArrayIndexOutOfBoundsException => {
						System.out.println(src.getWidth, src.getHeight, startX, startY, widthX, widthY, src.getRGB(startX + widthX, startY + widthY))
						Seq(0xFF00FFFF)
				}
			}
			
			dst.setRGB(x,y, sortedRgbs(sortedRgbs.size/2));
		}}
	}
	
	def getRenderingHints() = null
}



/**
 * 
 * @author Raymond Dodge
 * @version 2013 Feb 06
 */
object AverageGreyscaleColorOrdering extends Ordering[Color] {
	override def compare(a:Color, b:Color) = {
		(a.getRed + a.getGreen + a.getBlue) compareTo
				(b.getRed + b.getGreen + b.getBlue)
	}
}

/*
 * Okay, then. Apparently Ordering isn't specialized, and there's no speed improvement
 *  in wrapping the int in an Int instead of a Color.  
 *
 * @author Raymond Dodge
 * @version 2013 Jun 07
object AverageGreyscaleColorAsIntOrdering extends Ordering[Int] {
	override def compare(a:Int, b:Int) = {
		java.lang.Integer.compare(
				((a & 0xFF) + (a >> 8 & 0xFF) + (a >> 16 & 0xFF)),
				((b & 0xFF) + (b >> 8 & 0xFF) + (b >> 16 & 0xFF))
		)
	}
}*/

/*
 * @author Raymond Dodge
 * @version 2013 Feb 06
object LuminocityGreyscaleColorOrdering extends Ordering[Color] {
	override def compare(a:Color, b:Color) = {
		(0.2126 * a.getRed + 0.7152 * a.getGreen + 0.0722 * a.getBlue) compareTo
				(0.2126 * b.getRed + 0.7152 * b.getGreen + 0.0722 * b.getBlue)
	}
}
*/
