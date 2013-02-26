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

package com.rayrobdod.imageManipulator.operations;

import com.rayrobdod.imageManipulator.Operation;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.BufferedImageOp;
import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;

/**
 * Performs a Contrast Stretch.
 * 
 * directly translated from a VHDL file - aside from the allowing to
 * support colors and not just greyscale. Probably the main point of
 * iffiness, and the impelmented-in-java-not-scala-ness.
 * 
 * @author Raymond Dodge
 * @version 2013 Feb 04
 */
public final class ContrastStretchingImageOp implements BufferedImageOp
{
	private final double percentageLow;
	private final double percentageHigh;
	
	public ContrastStretchingImageOp(double percentageLow, double percentageHigh) {
		this.percentageLow = percentageLow;
		this.percentageHigh = percentageHigh;
	}
	
	public BufferedImage filter(BufferedImage src, BufferedImage dst)
	{
		if (dst == null) {dst = this.createCompatibleDestImage(src, null);}
		if (!(dst.getWidth() == src.getWidth() && src.getHeight() == dst.getHeight())) throw new IllegalArgumentException("Destination doesn't have same size as source");
		
		int i;
		int[] histogram = new int[256];
		
		// make histogram
		for (int row = 0; row < src.getWidth(); row++)
		for (int col = 0; col < src.getHeight(); col++) {
			histogram[ (src.getRGB(row,col) & 0xFF) ]++;
			histogram[ (src.getRGB(row,col) & 0xFF00) >> 8]++;
			histogram[ (src.getRGB(row,col) & 0xFF0000) >> 16]++;
		}
		
		// determine percentiles
		final int percentileCountLow  = (int) (percentageLow  * 3 * src.getWidth() * src.getHeight());
		final int percentileCountHigh = (int) (percentageHigh * 3 * src.getWidth() * src.getHeight());
		
		// determine cutoff values
		int lowVal = 0;
		i = 0;
		while (i < percentileCountLow) {
			i = i + histogram[lowVal];
			lowVal++;
		}
    
		int highVal = 255;
		i = 0;
		while (i < percentileCountLow) {
			i = i + histogram[highVal];
			highVal--;
		}
		// System.out.println(lowVal + " - " + highVal);
		
		// writeback
		for (int row = 0; row < src.getWidth(); row++)
		for (int col = 0; col < src.getHeight(); col++) {
			int oldRGB = src.getRGB(row,col);
			int oldRed = (oldRGB & 0xFF0000) >> 16;
			int oldGreen = (oldRGB & 0xFF00) >> 8;
			int oldBlue = (oldRGB & 0xFF);
			
			int newRGB = 0xFF000000 |
						scale(oldRed, lowVal, highVal) << 16 |
						scale(oldGreen, lowVal, highVal) << 8 |
						scale(oldBlue, lowVal, highVal);
			dst.setRGB(row, col, newRGB);
		}
		
		if (dst.getAlphaRaster() != null && src.getAlphaRaster() != null)
			dst.getAlphaRaster().setRect(src.getAlphaRaster());
		
		return dst;
	}
	
	private int scale(int in, int lowVal, int highVal) {
		if ( in < lowVal )
			return 0;
		else if ( in > highVal )
			return 255;
		else
			return (255 * (in - lowVal)) / (highVal - lowVal);
	}
	
	public RenderingHints getRenderingHints() {return null;}
	
	
	
	/**
	 * Coped from NoResizeBufferedImageOp
	 */
	public BufferedImage createCompatibleDestImage(BufferedImage src, java.awt.image.ColorModel cm) {
		return new BufferedImage(src.getWidth(), src.getHeight(), src.getType());
	}
	
	public java.awt.Rectangle getBounds2D(BufferedImage src) {
		return new java.awt.Rectangle(src.getWidth(), src.getHeight());
	}

	public Point2D getPoint2D(Point2D in, Point2D out) {
		if (out == null) {out = new Point2D.Double();}
		out.setLocation(in);
		return out;
	}
}
