package com.rayrobdod.util;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.Guid.GUID;

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
//	private static native NativeLong SHGetPropertyStoreForWindow(HWND window, GUID.ByReference typeOfThing, PointerByReference returnValue);
	
//	public class PROPERTYKEY extends Structure
//	{
//		public GUID  fmtid;
//		public int   pid;
//
//	}
	
//	IPropertyStore {
//		GetAt(int index, PointerByReference[PROPERTYKEY] pkey)
//		SetValue(PointerByReference[PROPERTYKEY] key, PointerByReference[PROPVARIANT] value)
//	}
	
	/* public static void setRelaunchTask(Window frame, String command, String display)
	{
		HWND frameHwnd = new HWND(Native.getComponentPointer(frame));
		
		String Str_IPropertyStore = "{886D8EEB-8CF2-4446-8D02-CDBA1DBDCF99}"; 
		GUID IID_IPropertyStore = new GUID();
		// GUID[] REFIID_IPropertyStore = [IID_IPropertyStore];
		GUID.ByReference REFIID_IPropertyStore = new GUID.ByReference(IID_IPropertyStore)
		
		PointerByReference pref = new PointerByReference();
		
		
//		SHGetPropertyStoreForWindow(frameHwnd, REFIID_IPropertyStore, pref);
		
		Pointer p = pref.getValue().turnIntoIPropertyStore()
		
        
		
		????
		
//		System.AppUserModel.RelaunchCommandKey = PROPERTYKEY
//		{
//			fmtid = 9F4C2855-9F79-4B39-A8D0-E1D42DE1D5F3
//			pid = 2
//		}

		
//		System.AppUserModel.RelaunchCommand = command
//		System.AppUserModel.RelaunchDisplayNameResource = displayName

	} */
	
	static
	{
		Native.register("shell32");
	}
}
