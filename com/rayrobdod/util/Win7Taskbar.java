package com.rayrobdod.util;

import java.awt.Frame;

public class Win7Taskbar
{
	public static native int setCurrentProcessAppID(final String appID);
	
	public static native int setRelaunchCommand(Frame window, String appID, String command, String displayName);
	
	static {
		// Apparently, com_rayrobdod_util_Win7Taskbar.dll can't load it's dependencies, but IPROP can…
		System.load("C:/Windows/SysWOW64/IPROP.dll");
		System.loadLibrary("KERNEL32");
		System.loadLibrary("SHELL32");
		System.loadLibrary("SHLWAPI");
		System.load("C:/Users/Raymond/Documents/Programming/Java/Image Manipulator/com_rayrobdod_util_Win7Taskbar.dll");
	}
}
