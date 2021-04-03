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

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.UtilFolder;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.background.BackgroundAbstract;
import com.b3dgs.lionengine.game.background.BackgroundComponent;
import com.b3dgs.lionengine.game.background.BackgroundElement;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionheart.MapTileWater;
import com.b3dgs.lionheart.constant.Folder;

/**
 * Water foreground implementation.
 */
public final class ForegroundWater extends BackgroundAbstract implements Foreground
{
    private static final int WATER_LINES = 4;
    private static final int WATER_SIDE_COUNT_MAX = 4;
    private static final double RAISE_SPEED = 0.4;

    /** Services reference. */
    private final MapTileWater mapWater;
    /** Water depth. */
    private final double depth;
    /** Water depth offset. */
    private final double depthOffset;
    /** Water speed. */
    private final double speed = 0.02;
    /** Water anim speed. */
    private final double animSpeed;
    /** Water effect flag. */
    private final boolean effect;
    /** Primary. */
    private final Primary primary;
    /** Secondary. */
    private final Secondary secondary;
    /** Water line delay */
    private final Tick tick = new Tick();
    /** Screen width. */
    private int screenWidth;
    /** Screen height. */
    private int screenHeight;
    /** Water height. */
    private double height;
    /** Water raise offset. */
    private double raise;
    /** Water raise max. */
    private double raiseMax;
    /** Water current line. */
    private int offsetLine;
    /** Water line offset. */
    private final int[] offset = new int[WATER_LINES];
    /** Water line offset side. */
    private int offsetSide = 1;
    /** Water line offset side counter. */
    private int offsetSideCount = 0;

    /**
     * Constructor.
     * 
     * @param services The services reference.
     * @param source The resolution source reference.
     * @param theme The theme name.
     * @param config The configuration.
     */
    ForegroundWater(Services services, SourceResolutionProvider source, String theme, ForegroundConfig config)
    {
        super(theme, 0, 0);

        depth = config.getWaterDepth().orElse(0);
        depthOffset = config.getWaterOffset().orElse(0);
        animSpeed = config.getWaterSpeed().orElse(0.25);
        effect = config.getWaterEffect();
        raiseMax = config.getWaterRaise();
        raise = 0;

        mapWater = services.get(MapTileWater.class);

        final String path = UtilFolder.getPathSeparator(Medias.getSeparator(), Folder.FOREGROUND, theme);
        primary = new Primary(path, this);
        secondary = new Secondary(path, this);

        setScreenSize(source.getWidth(), source.getHeight());
        add(primary);
        add(secondary);

        tick.start();
    }

    /**
     * Set maximum raise.
     * 
     * @param max The raise max.
     */
    public void setRaiseMax(double max)
    {
        raiseMax = max;
    }

    @Override
    public void renderBack(Graphic g)
    {
        if (raise < raiseMax)
        {
            raise += RAISE_SPEED;
        }
        else if (raiseMax < 0)
        {
            raise -= RAISE_SPEED * 5;
            if (raise < -32)
            {
                raise = -32;
            }
        }
        else
        {
            raise = raiseMax;
        }
        renderComponent(0, g);
    }

    @Override
    public void renderFront(Graphic g)
    {
        renderComponent(1, g);
    }

    @Override
    public void setScreenSize(int width, int height)
    {
        screenWidth = width;
        screenHeight = height;
    }

    /**
     * Set the height.
     * 
     * @param height The height to set.
     */
    public void setHeight(double height)
    {
        this.height = height;
    }

    /**
     * Get the height.
     * 
     * @return The height.
     */
    public double getHeight()
    {
        return height + depth + depthOffset + raise;
    }

    /**
     * Get the depth.
     * 
     * @return The depth.
     */
    public double getDepth()
    {
        return depth;
    }

    /**
     * Get the water speed.
     * 
     * @return The water speed.
     */
    public double getSpeed()
    {
        return speed;
    }

    /**
     * First front component, including water effect.
     */
    private final class Primary implements BackgroundComponent
    {
        /** Water element. */
        private final BackgroundElement data;
        /** Water reference. */
        private final ForegroundWater water;

        /**
         * Constructor.
         * 
         * @param path The primary surface path.
         * @param water The water reference.
         */
        Primary(String path, ForegroundWater water)
        {
            super();

            this.water = water;

            final Sprite sprite = Drawable.loadSprite(Medias.create(path, "calc.png"));
            sprite.load();
            sprite.prepare();
            data = new BackgroundElement(0, 0, sprite);
        }

        @Override
        public void update(double extrp, int x, int y, double speed)
        {
            data.setOffsetY(y);
        }

        @Override
        public void render(Graphic g)
        {
            final Sprite sprite = (Sprite) data.getRenderable();
            final int w = (int) Math.ceil(screenWidth / (double) sprite.getWidth());
            final int y = (int) (screenHeight + data.getOffsetY() - water.getHeight() + 4);

            if (y >= -sprite.getHeight() && y < screenHeight)
            {
                for (int j = 0; j < w; j++)
                {
                    for (int k = 0; k < water.getHeight() / sprite.getHeight(); k++)
                    {
                        sprite.setLocation(sprite.getWidth() * j, y + k * sprite.getHeight());
                        sprite.render(g);
                    }
                }
            }
        }
    }

    /**
     * Second front component, including water effect.
     */
    private final class Secondary implements BackgroundComponent
    {
        /** Animation data. */
        private final Animation animation = new Animation(Animation.DEFAULT_NAME, 1, 7, animSpeed, false, true);
        /** Sprite. */
        private final SpriteAnimated anim;
        /** Water reference. */
        private final ForegroundWater water;
        /** Main X. */
        private int mainX;
        /** Offset X. */
        private double offsetX;
        /** Offset Y. */
        private double offsetY;
        /** Height value. */
        private double height;
        /** Position y. */
        private int py;
        /** Anim flicker. */
        private int animFlick;

        /**
         * Constructor.
         * 
         * @param path The secondary surface path.
         * @param water The water reference.
         */
        Secondary(String path, ForegroundWater water)
        {
            super();

            this.water = water;

            anim = Drawable.loadSpriteAnimated(Medias.create(path, "anim.png"), 1, animation.getLast());
            anim.load();
            anim.play(animation);
        }

        /**
         * Update water effect.
         * 
         * @param g The graphics output.
         */
        private void waterEffect(Graphic g)
        {
            final int y = screenHeight + py - (int) water.getHeight() + 6;
            final int max = Math.max(0, (int) Math.floor(water.getHeight() / (WATER_LINES * 3)));

            for (int l = 0; l < WATER_LINES + max; l++)
            {
                g.copyArea(0,
                           y - (l - max) * WATER_LINES * 3 + 32,
                           screenWidth,
                           WATER_LINES * 3,
                           offset[(WATER_LINES + max - 1 - l) % WATER_LINES],
                           0);
            }

            if (offsetSideCount < WATER_SIDE_COUNT_MAX)
            {
                if (tick.elapsed(3))
                {
                    offset[WATER_LINES - 1 - offsetLine] += offsetSide;
                    offsetLine++;

                    if (Math.abs(offset[0]) % (WATER_LINES / 2) == 0)
                    {
                        tick.restart();
                    }
                }

                if (offsetLine >= WATER_LINES)
                {
                    offsetLine = 0;
                    offsetSideCount++;
                }
            }
            else
            {
                offsetSideCount = 0;
                offsetSide = -offsetSide;
            }
        }

        @Override
        public void update(double extrp, int x, int y, double speed)
        {
            tick.update(extrp);
            anim.update(extrp);

            offsetX = UtilMath.wrapDouble(offsetX + speed, 0.0, anim.getTileWidth());
            offsetY = y;

            height = UtilMath.wrapDouble(height + water.getSpeed() * extrp, 0.0, 360.0);
            water.setHeight(Math.cos(height) * water.getDepth());
            mapWater.setWaterHeight((int) water.getHeight());

            py = y;
        }

        @Override
        public void render(Graphic g)
        {
            // w number of renders used to fill screen
            int w = (int) Math.ceil(screenWidth / (double) anim.getWidth());
            int y = (int) (screenHeight + offsetY - water.getHeight());

            // animation rendering
            if (animFlick > 0)
            {
                w = (int) Math.ceil(screenWidth / (double) anim.getTileWidth()) + 1;
                final int x = (int) (-offsetX + mainX);

                y -= anim.getTileHeight() - 8;
                if (y >= 0 && y <= screenHeight)
                {
                    for (int j = 0; j < w; j++)
                    {
                        anim.setLocation(x + anim.getTileWidth() * j, y);
                        anim.render(g);
                    }
                }
            }
            animFlick++;
            if (animFlick > 3)
            {
                animFlick = 0;
            }

            if (effect)
            {
                waterEffect(g);
            }
        }
    }
}
