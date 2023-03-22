#include <stdlib.h>
#include <stdio.h>
#include <Windows.h>

static BOOL IsOS64Bit()
{
	BOOL b64Bit = FALSE;

	typedef BOOL(WINAPI* PFNISWOW64PROCESS)(HANDLE, PBOOL);

	HMODULE hKernel32 = LoadLibrary(TEXT("kernel32.dll"));
	if (hKernel32 != NULL)
	{
		PFNISWOW64PROCESS pfnIsWow64Process = (PFNISWOW64PROCESS)GetProcAddress(hKernel32, "IsWow64Process");
		if (pfnIsWow64Process == NULL)
		{
			FreeLibrary(hKernel32);
			return FALSE;
		}

		pfnIsWow64Process(GetCurrentProcess(), &b64Bit);
		FreeLibrary(hKernel32);
	}

	return b64Bit;
}

int WinMain(HINSTANCE hInstance, HINSTANCE hPrevInstance, char*, int nShowCmd)
{
	if (IsOS64Bit())
	{
		system("start \"\" /d data /b \"data\\jre_win32-x86_64\\bin\\javaw.exe\" -server -splash:splash.png -cp lionheart-pc-1.3.0.jar com.b3dgs.lionheart.Launcher");
	}
	else
	{
		system("start \"\" /d data /b \"data\\jre_win32-x86\\bin\\javaw.exe\" -server -splash:splash.png -cp lionheart-pc-1.3.0.jar com.b3dgs.lionheart.Launcher");
	}
	return 0;
}