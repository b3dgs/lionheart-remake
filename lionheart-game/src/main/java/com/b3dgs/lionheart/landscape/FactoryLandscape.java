/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionheart.landscape;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.background.Background;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;

/**
 * Landscape factory.
 */
public final class FactoryLandscape
{
    /** Unknown landscape error message. */
    private static final String UNKNOWN_LANDSCAPE_ERROR = "Unknown landscape: ";

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
     * @param flicker The flicker flag.
     */
    public FactoryLandscape(SourceResolutionProvider source, boolean flicker)
    {
        super();

        this.source = source;
        this.flicker = flicker;
        scaleH = 1.0;
        scaleV = 1.0;
    }

    /**
     * Create a landscape from its type.
     * 
     * @param landscape The landscape type.
     * @return The landscape instance.
     */
    public Landscape createLandscape(LandscapeType landscape)
    {
        switch (landscape.getWorld())
        {
            case SWAMP:
            {
                final Background background = new Swamp(source, scaleH, scaleV, landscape.getTheme(), flicker);
                final Foreground foreground = new Foreground(source, landscape.getForeground().getTheme());
                return new Landscape(landscape, background, foreground);
            }
            case ANCIENT_TOWN:
                final Background background = new AncientTown(source, scaleH, scaleV, landscape.getTheme(), flicker);
                final Foreground foreground = new Foreground(source, landscape.getForeground().getTheme());
                return new Landscape(landscape, background, foreground);
            default:
                throw new LionEngineException(FactoryLandscape.UNKNOWN_LANDSCAPE_ERROR + landscape);
        }
    }
}
