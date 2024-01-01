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
package com.b3dgs.lionheart;

import java.util.ArrayList;
import java.util.List;

import com.b3dgs.lionengine.Resource;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Renderable;

/**
 * Stories base.
 */
public class StoriesBase implements Resource, Renderable
{
    /** Stories data. */
    private final List<StoryRenderer> stories = new ArrayList<>();

    /** Story index. */
    private int story;
    private int start;

    /**
     * Create stories.
     */
    public StoriesBase()
    {
        super();
    }

    /**
     * Add a story.
     * 
     * @param story The story reference.
     */
    protected final void add(StoryRenderer story)
    {
        stories.add(story);
    }

    /**
     * Set current story.
     * 
     * @param story The story number.
     */
    public void setStory(int story)
    {
        this.story = story;
    }

    /**
     * Set start index.
     * 
     * @param start The start index.
     */
    public void setStart(int start)
    {
        this.start = start;
    }

    /**
     * Get stories count.
     * 
     * @return The stories count.
     */
    public int size()
    {
        return stories.size();
    }

    @Override
    public void load()
    {
        for (int i = 0; i < stories.size(); i++)
        {
            stories.get(i).load();
        }
    }

    @Override
    public void render(Graphic g)
    {
        for (int i = start; i < story; i++)
        {
            stories.get(i).setShowText(false);
            stories.get(i).render(g);
        }
        stories.get(story).setShowText(true);
        stories.get(story).render(g);
    }

    @Override
    public boolean isLoaded()
    {
        return stories.get(0).isLoaded();
    }

    @Override
    public void dispose()
    {
        for (int i = 0; i < stories.size(); i++)
        {
            stories.get(i).dispose();
        }
        stories.clear();
    }
}
