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
package com.b3dgs.lionheart.landscape;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.background.Background;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionheart.WorldType;

/**
 * Landscape factory.
 */
// CHECKSTYLE IGNORE LINE: DataAbstractionCoupling
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
    private final boolean flickerBackground;
    /** Foreground flickering flag. */
    private final boolean flickerForeground;

    /**
     * Constructor.
     * 
     * @param source The resolution source reference.
     * @param services The services reference.
     * @param flickerBackground The flicker flag.
     * @param flickerForeground The flicker flag.
     */
    public FactoryLandscape(Services services,
                            SourceResolutionProvider source,
                            boolean flickerBackground,
                            boolean flickerForeground)
    {
        super();

        this.services = services;
        this.source = source;
        this.flickerBackground = flickerBackground;
        this.flickerForeground = flickerForeground;
        scaleH = 1.0;
        scaleV = 1.0;
    }

    /**
     * Create a landscape.
     * 
     * @param backgroundType The background type.
     * @param foregroundConfig The foreground configuration.
     * @return The created landscape.
     */
    public Landscape createLandscape(BackgroundType backgroundType, ForegroundConfig foregroundConfig)
    {
        final Background background = createBackground(backgroundType);
        final ForegroundType foregroundType = foregroundConfig.getType();
        final Foreground foreground;
        if (ForegroundType.NONE == foregroundType)
        {
            foreground = new ForegroundVoid();
        }
        else if (ForegroundType.AIRSHIP == foregroundType)
        {
            foreground = new ForegroundAirship(services, source, foregroundType.getTheme(), foregroundConfig);
        }
        else
        {
            foreground = services.add(new ForegroundWater(services,
                                                          source,
                                                          foregroundType.getTheme(),
                                                          foregroundConfig,
                                                          flickerForeground));
        }
        return new Landscape(background, foreground);
    }

    /**
     * Create a background.
     * 
     * @param backgroundType The background type.
     * @return The created background.
     */
    // CHECKSTYLE IGNORE LINE: CyclomaticComplexity
    private Background createBackground(BackgroundType backgroundType)
    {
        final WorldType world = backgroundType.getWorld();
        return switch (world)
        {
            case SWAMP -> new Swamp(source, scaleH, scaleV, backgroundType.getTheme(), flickerBackground);
            case SPIDERCAVE1, SPIDERCAVE2, SPIDERCAVE3, UNDERWORLD -> new Black(source);
            // CHECKSTYLE IGNORE LINE: MagicNumber
            case ANCIENTTOWN -> new Gradient(1800, 470, source, backgroundType, flickerBackground);
            case LAVA -> new Lava(source, scaleH, scaleV, backgroundType.getTheme(), flickerBackground);
            // CHECKSTYLE IGNORE LINE: MagicNumber
            case SECRET -> new Gradient(1600, 384, source, backgroundType, flickerBackground);
            case AIRSHIP -> new Airship(source, scaleH, scaleV, backgroundType.getTheme(), flickerBackground);
            case DRAGONFLY -> new Dragonfly(source, scaleH, scaleV, backgroundType.getTheme(), flickerBackground);
            // CHECKSTYLE IGNORE LINE: MagicNumber
            case TOWER -> new Gradient(2448, 676, source, backgroundType, flickerBackground);
            case NORKA -> new Norka(source, scaleH, scaleV, backgroundType.getTheme(), flickerBackground);
            default -> throw new LionEngineException(backgroundType);
        };
    }
}
