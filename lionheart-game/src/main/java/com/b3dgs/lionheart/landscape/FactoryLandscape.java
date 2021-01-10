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
package com.b3dgs.lionheart.landscape;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.background.Background;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;

/**
 * Landscape factory.
 */
public final class FactoryLandscape
{
    /** The services reference. */
    private final Services services;
    /** The resolution source reference. */
    private final SourceResolutionProvider source;
    /** The horizontal factor. */
    private final double scaleH;
    /** The vertical factor. */
    private final double scaleV;
    /** Background flickering flag. */
    private final boolean flicker;

    /**
     * Constructor.
     * 
     * @param source The resolution source reference.
     * @param services The services reference.
     * @param flicker The flicker flag.
     */
    public FactoryLandscape(Services services, SourceResolutionProvider source, boolean flicker)
    {
        super();

        this.services = services;
        this.source = source;
        this.flicker = flicker;
        scaleH = 1.0;
        scaleV = 1.0;
    }

    /**
     * Create a landscape.
     * 
     * @param backgroundType The background type.
     * @param foregroundType The foreground type.
     * @return The created landscape.
     */
    public Landscape createLandscape(BackgroundType backgroundType, ForegroundType foregroundType)
    {
        final Background background = createBackground(backgroundType);
        final Foreground foreground;
        if (ForegroundType.NONE == foregroundType)
        {
            foreground = new ForegroundVoid();
        }
        else
        {
            foreground = new ForegroundWater(services, source, foregroundType.getTheme());
        }
        return new Landscape(background, foreground);
    }

    /**
     * Create a background.
     * 
     * @param backgroundType The background type.
     * @return The created background.
     */
    private Background createBackground(BackgroundType backgroundType)
    {
        final Background background;
        switch (backgroundType.getWorld())
        {
            case SWAMP:
                background = new Swamp(source, scaleH, scaleV, backgroundType.getTheme(), flicker);
                break;
            case SPIDERCAVE1:
            case SPIDERCAVE2:
                background = new BackgroundNone(source);
                break;
            case ANCIENT_TOWN:
                background = new AncientTown(source, scaleH, scaleV, backgroundType.getTheme(), flicker);
                break;
            default:
                throw new LionEngineException(backgroundType);
        }
        return background;
    }
}
