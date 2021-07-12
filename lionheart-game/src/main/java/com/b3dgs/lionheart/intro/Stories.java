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
package com.b3dgs.lionheart.intro;

import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionheart.Settings;
import com.b3dgs.lionheart.StoriesBase;
import com.b3dgs.lionheart.StoryRenderer;
import com.b3dgs.lionheart.Util;
import com.b3dgs.lionheart.constant.Folder;

/**
 * Intro stories.
 */
public class Stories extends StoriesBase
{
    /**
     * Create stories.
     */
    public Stories()
    {
        super();

        final String[] texts = new String[4];
        for (int i = 0; i < texts.length; i++)
        {
            texts[i] = Util.toFontText(Medias.create(Folder.TEXT,
                                                     Settings.getInstance().getLang(),
                                                     Folder.INTRO,
                                                     "story" + (i + 1) + ".txt"));
        }

        add(new StoryRenderer(Medias.create(Folder.INTRO, "part4", "pic0.png"), 0, 0, texts[0], 1, 98));
        add(new StoryRenderer(Medias.create(Folder.INTRO, "part4", "pic1.png"), 45, 20, texts[1], 1, 118));
        add(new StoryRenderer(Medias.create(Folder.INTRO, "part4", "pic2.png"), 90, 40, texts[2], 1, 138));
        add(new StoryRenderer(Medias.create(Folder.INTRO, "part4", "pic3.png"), 135, 60, texts[3], 1, 158));
    }
}
