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
package com.b3dgs.lionheart.intro;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionheart.Constant;
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
    private static final String PART4 = "part4";

    private static String[] loadTexts(int count, String lang)
    {
        final String[] texts = new String[count];
        for (int i = 0; i < texts.length; i++)
        {
            texts[i] = Util.toFontText(Medias.create(Folder.TEXT, lang, Folder.INTRO, "story" + (i + 1) + ".txt"));
        }
        return texts;
    }

    private static Media get(String part, String pic)
    {
        return Medias.create(Folder.INTRO, part, pic);
    }

    /**
     * Create stories.
     * 
     * @param width The screen width.
     * @param height The screen height.
     */
    public Stories(int width, int height)
    {
        super();

        final String[] texts = loadTexts(4, Settings.getInstance().getLang());
        final int offsetX = width / 2 - Constant.RESOLUTION.getWidth() / 2;

        // CHECKSTYLE OFF: MagicNumber
        add(new StoryRenderer(get(PART4, "pic0.png"), offsetX + 0, 0, texts[0], offsetX + 1, 98));
        add(new StoryRenderer(get(PART4, "pic1.png"), offsetX + 45, 20, texts[1], offsetX + 1, 118));
        add(new StoryRenderer(get(PART4, "pic2.png"), offsetX + 90, 40, texts[2], offsetX + 1, 138));
        add(new StoryRenderer(get(PART4, "pic3.png"), offsetX + 135, 60, texts[3], offsetX + 1, 158));
        // CHECKSTYLE ON: MagicNumber
    }
}
