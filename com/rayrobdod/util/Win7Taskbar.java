package com.rayrobdod.util;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.WString;
//import com.sun.jna.platform.win32.WinDef.HWND;

/**
 * Uses native Windows code to set an explicit model id
 * @author Raymond Dodge
 * @version 18 May 2012
 * @see [[http://stackoverflow.com/questions/1907735/using-jna-to-get-set-application-identifier]]
 */
public class Win7Taskbar
{
	public static void setCurrentProcessAppID(final String appID)
	{
		if (SetCurrentProcessExplicitAppUserModelID(new WString(appID)).longValue() != 0)
			throw new RuntimeException("unable to set current process explicit AppUserModelID to: " + appID);
	}
	
	private static native NativeLong SetCurrentProcessExplicitAppUserModelID(WString appID);
//	private static native NativeLong SHGetPropertyStoreForWindow(HWND window, REFIID typeOfThing, Pointer returnValue);
	
/*	public static void setReopenTask(Window frame, String command, String display)
	{
		HWND frameHwnd = new HWND(Native.getComponentPointer(frame));
		
		
		SHGetPropertyStoreForWindow(frameHwnd, ???, ???)
		
        
		
		????
		
		System.AppUserModel.RelaunchCommand = command
        System.AppUserModel.RelaunchDisplayNameResource = displayName

	}
*/	
	static
	{
		Native.register("shell32");
	}
}
 
