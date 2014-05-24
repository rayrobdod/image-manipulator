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
package com.rayrobdod.imageManipulator.main;

import com.rayrobdod.imageManipulator.ImageManipulateFrame;
import com.rayrobdod.imageManipulator.operations.Identity;
import com.rayrobdod.imageManipulator.FrameModel;
import javax.swing.JOptionPane;
import javax.swing.JFrame;

/**
 * A main method for the image manipulator program
 * 
 * @author Raymond Dodge
 * @since 1.0.0
 * @version 1.0.6
 */
public final class Main
{
	private Main() {}
	
	public static final void main(String[] args)
	{
		final ImageManipulateFrame frame = new ImageManipulateFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setVisible(true);
		
		setInitialImage(frame, args);
	}
	
	public static final void setInitialImage(ImageManipulateFrame frame, String[] args) {
		try {
			if (args.length != 0) {
				final java.awt.image.BufferedImage image = 
						javax.imageio.ImageIO.read(new java.io.File(args[0]));
				
				if (image != null) {
					frame.model().update(
						new FrameModel( image, new Identity())
					);
				} else {
					JOptionPane.showMessageDialog(null,
						"This application does not support the specified file's image format",
						"Unknown File Type",
						JOptionPane.ERROR_MESSAGE
					);
				}
			}
		} catch (java.io.IOException e) {
			JOptionPane.showMessageDialog(null,
				e.getMessage(),
				"Could not read image",
				JOptionPane.ERROR_MESSAGE
			);
		}
	}
}
