#include <stdlib.h>
#include <stdio.h>
#include <Windows.h>

BOOL Is64BitWindows()
{
#if defined(_WIN64)
	return TRUE;  // 64-bit programs run only on Win64
#elif defined(_WIN32)
	// 32-bit programs run on both 32-bit and 64-bit Windows
	BOOL f64 = FALSE;
	return IsWow64Process(GetCurrentProcess(), &f64) && f64;
#else
	return FALSE; // Win64 does not support Win16
#endif
}

int WinMain(HINSTANCE hInstance, HINSTANCE hPrevInstance, char*, int nShowCmd)
{
	if (Is64BitWindows())
	{
		system("start \"\" /d data /b \"data\\jre_win32-x86_64\\bin\\javaw.exe\" -server -splash:splash.png -cp lionheart-pc-%%APPV%%.jar com.b3dgs.lionheart.Launcher");
	}
	else
	{
		system("start \"\" /d data /b \"data\\jre_win32-x86\\bin\\javaw.exe\" -server -splash:splash.png -cp lionheart-pc-%%APPV%%.jar com.b3dgs.lionheart.Launcher");
	}
	return 0;
}