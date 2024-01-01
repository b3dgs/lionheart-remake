/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
#ifndef __APPLE__
__asm__(".symver realpath,realpath@GLIBC_2.2.5");
#endif
 
#include <stdlib.h>
#include <string.h>

int main(int argc, char** argv)
{
   const char cmd0[] = "cd ";
   const char cmd1[] = "data; jre_linux-x86_64/bin/java -server -splash:splash.png -jar lionheart-pc-%%INPUT_APPV%%.jar";
   int start = strchr(argv[0], '/') - argv[0];
   const int end = strrchr(argv[0], '/') - argv[0];

   // Double click run
   if (argv[0][0] == '.')
   {
      start++;
   }

   // Run from outside
   if (end > start)
   {
      char path[end - start];
      strncpy(path, argv[0] + start, end - start);

      char cmd2[sizeof(cmd0) + sizeof(char) + sizeof(path) + sizeof(cmd1)];
      strcpy(cmd2, "");
      strcat(cmd2, cmd0);
      strcat(cmd2, path);
      strcat(cmd2, "/");
      strcat(cmd2, cmd1);

      if (cmd2[0] == '.' && cmd2[1] == '/')
      {
            system(cmd2);
      }
      else
      {
            char cmd3[sizeof(cmd2) + 2];
            strncpy(cmd3, "./", 2);
            strncpy(cmd3, cmd2, sizeof(cmd2));

            system(cmd3);
      }
   }
   // Run from local
   else
   {
      char cmd2[sizeof(cmd0) + sizeof(cmd1)] = "";
      strcat(cmd2, cmd0);
      strcat(cmd2, cmd1);

      if (cmd2[0] == '.' && cmd2[1] == '/')
      {
            system(cmd2);
      }
      else
      {
            char cmd3[sizeof(cmd2) + 2];
            strncpy(cmd3, "./", 2);
            strncpy(cmd3, cmd2, sizeof(cmd2));

            system(cmd3);
      }
   }

   return 0;
}
