

#include "com_rayrobdod_util_Win7Taskbar.h"
#include <windows.h>
#include <jawt.h>
#include <jawt_md.h>
#include <shellapi.h>
#include <shlobj.h>
#include <propsys.h>
#include <propkey.h>
#include <propvarutil.h>
#include <stdio.h>
extern "C"

//#define LOG(x) printf(x)
//#define LOG2(x,y) printf(x,y)
#define LOG(x)
#define LOG2(x,y)

HWND getWindowHandle(JNIEnv* env, jobject comp);
HRESULT IPropertyStore_SetValue(IPropertyStore *pps,
	REFPROPERTYKEY pkey, PCWSTR pszValue);


JNIEXPORT jint JNICALL Java_com_rayrobdod_util_Win7Taskbar_setCurrentProcessAppID (
			JNIEnv* env,
			jclass unused,
			jstring userModelID
) {
	jboolean* isCopy(0);
	
	const jchar* newNameChars = env->GetStringChars(userModelID, isCopy);
	const PCWSTR newNameStr((WCHAR*) newNameChars);
	
	return SetCurrentProcessExplicitAppUserModelID(newNameStr);
}

JNIEXPORT jint JNICALL Java_com_rayrobdod_util_Win7Taskbar_setRelaunchCommand(
		JNIEnv* env,
		jclass unused,
		jobject window,
		jstring userModelID,
		jstring command,
		jstring displayName
) {
	LOG("Entering setRelaunchCommand()\n");
	HWND hwnd = getWindowHandle(env, window);
	LOG2("HWND: %d\n", hwnd); 
	
	// getWindowHandle uses negative values for error values; HWND is an unsigned value
	if ((int) hwnd < 0xFFFFFFFFFFFFFFF0) {
		// the IID for an IPropertyStore is {886D8EEB-8CF2-4446-8D02-CDBA1DBDCF99}
				
		IPropertyStore *pps;
		HRESULT hr2 = SHGetPropertyStoreForWindow(hwnd, IID_PPV_ARGS(&pps));
		
		if ( SUCCEEDED(hr2) ) {
			
			HRESULT hr3(0);
		 	
			hr3 = IPropertyStore_SetValue(pps, PKEY_AppUserModel_ID,
					(PCWSTR)(WCHAR*) env->GetStringChars(userModelID, 0));
			if (! SUCCEEDED(hr3) ) { return hr3; }
		 	
			hr3 = IPropertyStore_SetValue(pps, PKEY_AppUserModel_RelaunchCommand,
					(PCWSTR)(WCHAR*) env->GetStringChars(command, 0));
			if (! SUCCEEDED(hr3) ) { return hr3; }
		 	
			IPropertyStore_SetValue(pps, PKEY_AppUserModel_RelaunchDisplayNameResource,
					(PCWSTR)(WCHAR*) env->GetStringChars(displayName, 0));
			if (! SUCCEEDED(hr3) ) { return hr3; }
		 	
			// optionally also set PKEY_AppUserModel_RelaunchIconResource
			pps->Release();
			
			return 0;
		} else {return -2;}
	} else {return -1;}
}









// @see http://blogs.msdn.com/b/oldnewthing/archive/2011/06/01/10170113.aspx
HRESULT IPropertyStore_SetValue(IPropertyStore *pps,
			REFPROPERTYKEY pkey, PCWSTR pszValue)
{
	PROPVARIANT var;
	HRESULT hr = InitPropVariantFromString(pszValue, &var);
	if (SUCCEEDED(hr))
	{
		hr = pps->SetValue(pkey, var);
		PropVariantClear(&var);
	}
	return hr;
}




HMODULE _hAWT = 0;

// @see http://stackoverflow.com/questions/386792/in-java-swing-how-do-you-get-a-win32-window-handle-hwnd-reference-to-a-window
HWND getWindowHandle(JNIEnv* env, jobject comp)
{
	HWND hWnd = 0;
	typedef jboolean (JNICALL *PJAWT_GETAWT)(JNIEnv*, JAWT*);
	JAWT awt;
	JAWT_DrawingSurface* ds;
	JAWT_DrawingSurfaceInfo* dsi;
	JAWT_Win32DrawingSurfaceInfo* dsi_win;
	jboolean result;
	jint lock;

	LOG("Entering getWindowHandle()\n");

	//Load AWT Library
	if(!_hAWT)
		//for Java 1.4
		_hAWT = LoadLibrary("jawt.dll");
	if(!_hAWT)
		//for Java 1.3
		_hAWT = LoadLibrary("awt.dll");
	if(_hAWT)
	{
		PJAWT_GETAWT JAWT_GetAWT = (PJAWT_GETAWT)GetProcAddress(_hAWT, "JAWT_GetAWT");
		if(JAWT_GetAWT)
		{
			awt.version = JAWT_VERSION_1_4; // Init here with JAWT_VERSION_1_3 or JAWT_VERSION_1_4
			//Get AWT API Interface
			result = JAWT_GetAWT(env, &awt);
			if(result != JNI_FALSE)
			{
				ds = awt.GetDrawingSurface(env, comp);
				if(ds != NULL)
				{
					lock = ds->Lock(ds);
					if((lock & JAWT_LOCK_ERROR) == 0)
					{
						dsi = ds->GetDrawingSurfaceInfo(ds);
						if(dsi)
						{
							dsi_win = (JAWT_Win32DrawingSurfaceInfo*)dsi->platformInfo;
							if(dsi_win)
							{
								hWnd = dsi_win->hwnd;
								LOG2("SUCCESS! %d\n", hWnd);
							} else {
								// cerr << "Drawing Surface Info doesn't have platform info" << endl;
								LOG("Drawing Surface Info doesn't have platform info\n");
								hWnd = (HWND) -1;
							}
							ds->FreeDrawingSurfaceInfo(dsi);
						} else {
							// cerr << "drawing surface info not gotten" << endl;
							LOG("drawing surface info not gotten\n");
							hWnd = (HWND) -2;
						}
						ds->Unlock(ds);
					} else {
						// cerr << "drawing surface not locked" << endl;
						LOG("drawing surface not locked\n");
						hWnd = (HWND) -3;
					}
					awt.FreeDrawingSurface(ds);
				} else {
					// cerr << "drawing surface not gotten" << endl;
					LOG("drawing surface not gotten\n");
					hWnd = (HWND) -4;
				}
			} else {
				// cerr << "AWT interface not gotten" << endl;
				LOG("AWT interface not gotten\n");
				hWnd = (HWND) -5;
			}
		} else {
			// cerr << "no process found" << endl;
			LOG("no process found\n");
			hWnd = (HWND) -6;
		}
	} else {
		// cerr << "no AWT library found" << endl;
		LOG("no AWT library found\n");
		hWnd = (HWND) -7;
	}
	return hWnd;

}

