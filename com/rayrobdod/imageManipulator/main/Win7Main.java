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
import javax.swing.JFrame;
import com.rayrobdod.util.Win7Taskbar;
import java.util.logging.Logger;

/**
 * A main method for the image manipulator program
 * 
 * @author Raymond Dodge
 * @version 2012 Jun 18
 * @version 2012 Sept 08 - transcribed directly from scala with no changes
 * @version 2012 Sept 09 - remarking as public. 
 * @version 2013 Jan 10-11 - Win7Taskbar seems to work; not trying to get this to work on top of that
 */
public class Win7Main
{
	private Win7Main() {}
	
	public static final String appID = "Rayrobdod.ImageManipulator.build_0004";
	public static final String name = "Image Manipulator build_0004";
	private static final Logger logger = com.rayrobdod.imageManipulator.LoggerInitializer.Win7MainLogger();
	
	public static final void main(String[] args)
	{
		Win7Taskbar.setCurrentProcessAppID(appID);
		
		JFrame frame = new ImageManipulateFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationByPlatform(true);
		
		String myPath = "";
		try {
			String tmp = Win7Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			myPath = java.net.URLDecoder.decode(tmp, "UTF-8");
			// serialize to work with system commands
			myPath = new java.io.File(myPath).toString();
		} catch (java.io.UnsupportedEncodingException e) {
			logger.warning("Apparently, this system doesn't support UTF-8");
			e.printStackTrace();
		}
		logger.info("Path: " + myPath);
		
		frame.setVisible(true);
		// This has to be called on a visible frame to work
		if (! myPath.equals("") ) {
			String command = "javaw -jar " + myPath;
			logger.info("Relaunch command: " + command);
			
			Win7Taskbar.setRelaunchCommand(frame, appID, command, name);
			
		} else {
			logger.info("Not setting relaunch command");
		}
	}
}
