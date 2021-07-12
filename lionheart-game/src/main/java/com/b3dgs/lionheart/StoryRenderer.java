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
package com.b3dgs.lionheart;

import java.util.Optional;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Resource;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionengine.graphic.drawable.SpriteFont;
import com.b3dgs.lionheart.constant.Folder;

/**
 * Handle story rendering.
 */
public class StoryRenderer implements Resource, Renderable
{
    /** Font. */
    private final SpriteFont font = Drawable.loadSpriteFont(Medias.create(Folder.SPRITE, "font.png"),
                                                            Medias.create(Folder.SPRITE, "fontdata.xml"),
                                                            12,
                                                            12);

    /** Pictures. */
    private final Sprite story;
    /** Show text. */
    private boolean showText = true;

    /**
     * Create story.
     * 
     * @param picture The picture media.
     * @param px The horizontal picture location.
     * @param py The vertical picture location.
     * @param text The text value.
     * @param tx The horizontal text location.
     * @param ty The vertical text location.
     */
    public StoryRenderer(Media picture, int px, int py, String text, int tx, int ty)
    {
        this(picture, px, py, Origin.TOP_LEFT, text, tx, ty);
    }

    /**
     * Create story.
     * 
     * @param picture The picture media.
     * @param px The horizontal picture location.
     * @param py The vertical picture location.
     * @param origin The picture origin.
     * @param text The text value.
     * @param tx The horizontal text location.
     * @param ty The vertical text location.
     */
    public StoryRenderer(Media picture, int px, int py, Origin origin, String text, int tx, int ty)
    {
        super();

        story = Drawable.loadSprite(picture);
        story.setLocation(px, py);
        story.setOrigin(origin);
        font.setLocation(tx, ty);
        font.setText(Optional.ofNullable(text).orElse(Constant.EMPTY_STRING));
    }

    /**
     * Set show text.
     * 
     * @param flag <code>true</code> to show text, <code>false</code> else.
     */
    public void setShowText(boolean flag)
    {
        showText = flag;
    }

    @Override
    public void load()
    {
        story.load();
        font.load();
    }

    @Override
    public boolean isLoaded()
    {
        return story.isLoaded();
    }

    @Override
    public void dispose()
    {
        story.dispose();
        font.dispose();
    }

    @Override
    public void render(Graphic g)
    {
        story.render(g);
        if (showText)
        {
            font.render(g);
        }
    }
}
