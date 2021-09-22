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

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionheart.Constant;
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
    private static final String PART3 = "part3";
    private static final String PART4 = "part4";
    private static final String PIC0 = "pic0.png";
    private static final String PIC1 = "pic1.png";
    private static final String PIC2 = "pic2.png";
    private static final String CREDITS = "credits.png";

    private static String[] loadTexts(String lang, int count)
    {
        final String[] texts = new String[count];
        for (int i = 0; i < texts.length; i++)
        {
            texts[i] = Util.toFontText(Medias.create(Folder.TEXT, lang, Folder.EXTRO, "story" + (i + 1) + ".txt"));
        }
        return texts;
    }

    private static Media get(String part, String pic)
    {
        return Medias.create(Folder.EXTRO, part, pic);
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

        final String[] texts = loadTexts(Settings.getInstance().getLang(), 7);
        final int textX = width / 2 - 124;
        final int offsetX = width / 2 - Constant.RESOLUTION.getWidth() / 2;

        // CHECKSTYLE OFF: MagicNumber
        add(new StoryRenderer(get(PART3, PIC0), offsetX + 0, 0, texts[0], offsetX + 1, 128));
        add(new StoryRenderer(get(PART3, PIC1), offsetX + 160, 14, texts[1], offsetX + 1, 128));
        add(new StoryRenderer(get(PART3, PIC2), offsetX + 80, 29, texts[2], offsetX + 1, 128));
        add(new StoryRenderer(get(PART4, CREDITS), width / 2, height / 2, Origin.MIDDLE, null, 0, 0));
        add(new StoryRenderer(get(PART4, CREDITS), width / 2, height / 2, Origin.MIDDLE, texts[3], textX, 22));
        add(new StoryRenderer(get(PART4, CREDITS), width / 2, height / 2, Origin.MIDDLE, texts[4], textX, 22));
        add(new StoryRenderer(get(PART4, PIC0), width / 2, height / 2, Origin.MIDDLE, null, 0, 0));
        add(new StoryRenderer(get(PART4, PIC1), width / 2 - 114, 12, texts[5], textX, 172));
        add(new StoryRenderer(get(PART4, PIC2), width / 2 - 20, 62, texts[6], textX, 172));
        // CHECKSTYLE ON: MagicNumber
    }
}
