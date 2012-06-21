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

package com.rayrobdod.imageManipulator

import java.awt.image.{BufferedImage, BufferedImageOp}
import java.awt.geom.Point2D

package object operations
{
	/**
	 * A trait that indicates a [[java.awt.image.BufferedImageOp]] that
	 * will not change the size of the BufferedImage or translate any pixels
	 * 
	 * @author Raymond Dodge
	 * @version 2012 Jun 19
	 */
	trait NoResizeBufferedImageOp extends BufferedImageOp
	{
		override def createCompatibleDestImage(src:BufferedImage, cm:java.awt.image.ColorModel) = {
			new BufferedImage(src.getWidth, src.getHeight, BufferedImage.TYPE_INT_ARGB)
		}
		
		override def getBounds2D(src:BufferedImage) = new java.awt.Rectangle(src.getWidth, src.getHeight)

		override def getPoint2D(in:Point2D, out:Point2D) = {
			val returnValue = Option(out).getOrElse(new Point2D.Double)
			returnValue.setLocation(in)
			returnValue
		}

	}
}
