/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionheart.extro;

import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionheart.Settings;
import com.b3dgs.lionheart.StoriesBase;
import com.b3dgs.lionheart.StoryRenderer;
import com.b3dgs.lionheart.Util;
import com.b3dgs.lionheart.constant.Folder;

/**
 * Extro stories.
 */
public class Stories extends StoriesBase
{
    /**
     * Create stories.
     * 
     * @param width The screen width.
     * @param height The screen height.
     */
    public Stories(int width, int height)
    {
        super();

        final String[] texts = new String[7];
        for (int i = 0; i < texts.length; i++)
        {
            texts[i] = Util.toFontText(Medias.create(Folder.TEXT,
                                                     Settings.getInstance().getLang(),
                                                     Folder.EXTRO,
                                                     "story" + (i + 1) + ".txt"));
        }

        final int textX = width / 2 - 124;

        add(new StoryRenderer(Medias.create(Folder.EXTRO, "part3", "pic0.png"), 0, 0, texts[0], 1, 128));
        add(new StoryRenderer(Medias.create(Folder.EXTRO, "part3", "pic1.png"), 160, 14, texts[1], 1, 128));
        add(new StoryRenderer(Medias.create(Folder.EXTRO, "part3", "pic2.png"), 80, 29, texts[2], 1, 128));
        add(new StoryRenderer(Medias.create(Folder.EXTRO,
                                            "part4",
                                            "credits.png"),
                              width / 2,
                              height / 2,
                              Origin.MIDDLE,
                              null,
                              0,
                              0));
        add(new StoryRenderer(Medias.create(Folder.EXTRO,
                                            "part4",
                                            "credits.png"),
                              width / 2,
                              height / 2,
                              Origin.MIDDLE,
                              texts[3],
                              textX,
                              22));
        add(new StoryRenderer(Medias.create(Folder.EXTRO,
                                            "part4",
                                            "credits.png"),
                              width / 2,
                              height / 2,
                              Origin.MIDDLE,
                              texts[4],
                              textX,
                              22));
        add(new StoryRenderer(Medias.create(Folder.EXTRO,
                                            "part4",
                                            "pic0.png"),
                              width / 2,
                              height / 2,
                              Origin.MIDDLE,
                              null,
                              0,
                              0));
        add(new StoryRenderer(Medias.create(Folder.EXTRO, "part4", "pic1.png"),
                              width / 2 - 114,
                              12,
                              texts[5],
                              textX,
                              172));
        add(new StoryRenderer(Medias.create(Folder.EXTRO, "part4", "pic2.png"),
                              width / 2 - 20,
                              62,
                              texts[6],
                              textX,
                              172));
    }
}
