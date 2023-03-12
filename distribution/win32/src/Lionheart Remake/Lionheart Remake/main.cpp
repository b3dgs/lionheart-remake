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
		system("start \"\" /d data /b \"data\\jre64\\bin\\javaw.exe\" -Xverify:none -server -splash:splash.png -jar lionheart-pc-1.3.0.jar");
	}
	else
	{
		system("start \"\" /d data /b \"data\\jre32\\bin\\javaw.exe\" -Xverify:none -server -splash:splash.png -jar lionheart-pc-1.3.0.jar");
	}
	return 0;
}