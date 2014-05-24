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
import javax.swing.JComboBox

/**
 * Swaps color channels and similar to make regions of a color look like
 * regions of another color
 * @author Raymond Dodge
 * @version 2.0
 */
final class ChangeColor extends Operation
{
	import com.rayrobdod.imageManipulator.operations.ChangeColor.Colors

	override val name = "Change Color"
	
	final class ColorsComboBoxModel extends ScalaSeqListModel[Colors.Value](Colors.values) with AbstractComboBoxModel[Colors.Value]
	
	val fromCombo = new JComboBox[Colors.Value](new ColorsComboBoxModel)
	val toCombo = new JComboBox[Colors.Value](new ColorsComboBoxModel)
	fromCombo.setSelectedIndex(0)
	toCombo.setSelectedIndex(0)
	
	override def setup(
				panel:javax.swing.JPanel,
				listener:java.awt.event.ActionListener):Any = {
		panel.add(fromCombo)
		panel.add(toCombo)
		
		fromCombo.addActionListener(listener)
		toCombo.addActionListener(listener)
	}
	
	/** @since 2.0 */
	override def getImageOp:BufferedImageOp =
	{
		new ColorChangeImageOp(
			fromCombo.getModel.getElementAt(fromCombo.getSelectedIndex),
			toCombo.getModel.getElementAt(toCombo.getSelectedIndex)
		)
	}
}

/**
 * 
 * @author Raymond Dodge
 * @version 2012 Jun 19
 * @version 2013 Feb 03 - Colors._ are now vals instead of objects
 */
object ChangeColor
{
	/**
	 * The colors that can be changed to and from
	 */
	object Colors
	{
		final class Value(val name:String) {
			override def toString = name;
		}
		
		/** <div style="color:#F00;">Red</div> */
		val Red = new Value("Red");
		
		/** <div style="color:#0F0;">Green</div> */
		val Green = new Value("Green")
		
		/** <div style="color:#00F;">Blue</div> */
		val Blue = new Value("Blue")
		
		/** <div style="color:#FF0;">Yellow</div> */
		val Yellow = new Value("Yellow")
		
		/** <div style="color:#0FF;">Cyan</div> */
		val Cyan = new Value("Cyan")
		
		/** <div style="color:#F0F;">Magenta</div> */
		val Magenta = new Value("Magenta")
		
		/** <div style="color:#000;">Black</div> */
		val Black = new Value("Black")
		
		
		val values = Seq(Red, Green, Blue, Yellow, Cyan, Magenta, Black)
	}
}

import com.rayrobdod.imageManipulator.operations.ChangeColor.Colors

/**
 * Swaps color channels and similar to make regions of a color look like
 * regions of another color
 * 
 * @author Raymond Dodge
 * @version 2012 Jun 19
 * @version 2013 Feb 05 - now using trait LocalReplacement
 */
final class ColorChangeImageOp(val from:Colors.Value, val to:Colors.Value) extends NoResizeBufferedImageOp with LocalReplacement 
{
	override def createCompatibleDestImage(src:BufferedImage, cm:java.awt.image.ColorModel) = {
		new BufferedImage(src.getWidth, src.getHeight, 
			if (src.getAlphaRaster() != null ) // if there is an alpha component
			{
				BufferedImage.TYPE_INT_ARGB
			}
			else if (Colors.Black == to)
			{
				BufferedImage.TYPE_BYTE_GRAY
			}
			else
			{
				BufferedImage.TYPE_INT_RGB
			}
		)
	}
	
	
	
	/**
	 * @param a RGB value
	 */
	def changePixel(src:Int):Color = changePixel(new Color(src))
	
	def changePixel(src:Color) =
	{
		import ChangeColor.Colors._
		
		val primaryFrom = from match
		{
			case Red   => src.getRed()
			case Green => src.getGreen()
			case Blue  => src.getBlue()
			case Yellow  => (src.getRed()  + src.getGreen()) / 2
			case Cyan    => (src.getBlue() + src.getGreen()) / 2
			case Magenta => (src.getRed()  + src.getBlue() ) / 2
			case Black => 255
		}
		
		val secondaryFrom = from match
		{
			case Red   => (src.getBlue() + src.getGreen()) / 2
			case Green => (src.getRed() + src.getBlue()) / 2
			case Blue  => (src.getRed() + src.getGreen()) / 2
			case Yellow  => src.getBlue()
			case Cyan    => src.getRed()
			case Magenta => src.getGreen()
			case Black => (src.getRed() + src.getGreen() + src.getBlue()) / 3
		}
		
		to match
		{
			case Red => 
			{
				val green = from match
				{
					case Green => src.getRed
					case Red => src.getGreen
					case Blue => src.getGreen
					case _ => secondaryFrom
				}
				val blue:Int = from match
				{
					case Blue => src.getRed
					case Red => src.getBlue
					case Green => src.getBlue
					case _ => secondaryFrom
				}
				
				new Color(primaryFrom, green, blue)
			}
			case Green => 
			{
				val red = from match
				{
					case Red => src.getGreen
					case Blue => src.getRed
					case Green => src.getRed
					case _ => secondaryFrom
				}
				val blue = from match
				{
					case Blue => src.getGreen
					case Red => src.getBlue
					case Green => src.getBlue
					case _ => secondaryFrom
				}
				
				new Color(red, primaryFrom, blue)
			}
			case Blue => 
			{
				val red = from match
				{
					case Red => src.getGreen
					case Blue => src.getRed
					case Green => src.getRed
					case _ => secondaryFrom
				}
				val green = from match
				{
					case Green => src.getBlue
					case Red => src.getGreen
					case Blue => src.getGreen
					case _ => secondaryFrom
				}
				
				new Color(red, green, primaryFrom)
			}
			case Yellow => new Color(primaryFrom, primaryFrom, secondaryFrom)
			case Cyan => new Color(secondaryFrom, primaryFrom, primaryFrom)
			case Magenta => new Color(primaryFrom, secondaryFrom, primaryFrom)
			case Black => new Color(secondaryFrom, secondaryFrom, secondaryFrom)
			case _ => src
		}
	}
	
	def pixelReplaceFunction(src:BufferedImage, dst:BufferedImage, x:Int) = {
		{{(y:Int) => dst.setRGB(x, y, changePixel(src.getRGB(x, y)).getRGB)}}
	}
	
	def getRenderingHints() = null
}
