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

package com.rayrobdod

import java.awt.image.BufferedImage

/**
 * Classes used for the [[http://rayrobdod.name/programming/programs/imageManipulator/ Image Manipulator program]]
 */
package object imageManipulator {

	/**
	 * Creates an scaled version of src such that it is smaller than
	 * a 400px by 400px square.
	 * @since 2.0
	 */
	def scaleImage(src:BufferedImage):BufferedImage = {
		if (src.getHeight > 400 && src.getHeight >= src.getWidth) {
			val width = src.getWidth * 400 / src.getHeight
			
			val a = src.getData.createCompatibleWritableRaster(width, 400)
			val b = new BufferedImage(src.getColorModel, a, true, new java.util.Hashtable())
			b.createGraphics().drawImage(src, 0, 0, width, 400, null)
			return b
		} else if (src.getWidth > 400 && src.getHeight <= src.getWidth) {
			val height = src.getHeight * 400 / src.getWidth
			
			val a = src.getData.createCompatibleWritableRaster(400, height)
			val b = new BufferedImage(src.getColorModel, a, true, new java.util.Hashtable())
			b.createGraphics().drawImage(src, 0, 0, 400, height, null)
			return b
		} else {
			src
		}
	}
	
}
