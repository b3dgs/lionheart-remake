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

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.geom.Coord;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.constant.Folder;

/**
 * Intro part 2 implementation.
 */
public final class Part2
{
    /** Door. */
    private final SpriteAnimated door = Drawable.loadSpriteAnimated(Medias.create(Folder.INTRO, "part2", "door.png"),
                                                                    3,
                                                                    2);
    /** Pillar. */
    private final Sprite[] pillar = new Sprite[6];
    /** Cave 1. */
    private final Sprite cave1 = Drawable.loadSprite(Medias.create(Folder.INTRO, "part2", "cave1.png"));
    /** Cave 2. */
    private final Sprite cave2 = Drawable.loadSprite(Medias.create(Folder.INTRO, "part2", "cave2.png"));
    /** Valdyn. */
    private final Sprite valdyn = Drawable.loadSprite(Medias.create(Folder.INTRO, "part2", "valdyn.png"));
    /** Equip sword. */
    private final SpriteAnimated equipSword = Drawable.loadSpriteAnimated(Medias.create(Folder.INTRO,
                                                                                        "part2",
                                                                                        "sword.png"),
                                                                          3,
                                                                          1);
    /** Equip foot. */
    private final SpriteAnimated equipFoot = Drawable.loadSpriteAnimated(Medias.create(Folder.INTRO,
                                                                                       "part2",
                                                                                       "foot.png"),
                                                                         3,
                                                                         1);
    /** Equip hand. */
    private final SpriteAnimated equipHand = Drawable.loadSpriteAnimated(Medias.create(Folder.INTRO,
                                                                                       "part2",
                                                                                       "hand.png"),
                                                                         3,
                                                                         1);
    /** Valdyn 0. */
    private final Sprite valdyn0 = Drawable.loadSprite(Medias.create(Folder.INTRO, "part2", "valdyn0.png"));
    /** Valdyn 1. */
    private final Sprite valdyn1 = Drawable.loadSprite(Medias.create(Folder.INTRO, "part2", "valdyn1.png"));
    /** Valdyn 2. */
    private final Sprite valdyn2 = Drawable.loadSprite(Medias.create(Folder.INTRO, "part2", "valdyn2.png"));
    /** Valdyn coordinate. */
    private final Coord valdynCoord = new Coord(320, 240);
    /** Z locations. */
    private final double[] z = new double[2 + pillar.length];

    /** Alpha. */
    private double alpha;
    /** Alpha 2. */
    private double alpha2;
    /** Flash. */
    private int flash;

    /**
     * Constructor.
     */
    public Part2()
    {
        super();

        for (int i = 0; i < pillar.length; i++)
        {
            pillar[i] = Drawable.loadSprite(Medias.create(Folder.INTRO, "part2", "pillar.png"));
            pillar[i].load();
        }
    }

    /**
     * Load part.
     */
    public void load()
    {
        door.load();
        cave1.load();
        valdyn.load();
        cave2.load();
        cave2.setOrigin(Origin.MIDDLE);
        equipFoot.load();
        equipSword.load();
        equipHand.load();
        valdyn0.load();
        valdyn0.setOrigin(Origin.MIDDLE);
        valdyn1.load();
        valdyn1.setOrigin(Origin.MIDDLE);
        valdyn2.load();
        valdyn2.setOrigin(Origin.MIDDLE);
        door.play(new Animation(Animation.DEFAULT_NAME, 1, 6, 0.15, false, false));
        final Animation equip = new Animation(Animation.DEFAULT_NAME, 1, 3, 0.15, false, false);
        equipFoot.play(equip);
        equipSword.play(equip);
        equipHand.play(equip);
        z[0] = 10;
    }

    /**
     * Update part.
     * 
     * @param seek The current seek.
     * @param extrp The extrapolation value.
     */
    public void update(long seek, double extrp)
    {
        // Open the door
        if (seek < 48700)
        {
            door.update(extrp);
        }

        // Enter in the door
        if (seek > 49000 && z[0] > 2)
        {
            z[0] -= 0.08;
            final double doorZ = UtilMath.clamp(1000 / z[0], 100, 800);
            door.stretch(doorZ, doorZ);
            if (z[0] < 2)
            {
                for (int i = 1; i < z.length; i++)
                {
                    z[i] = i * 15 + 25;
                }
            }
        }

        // Alpha inside cave
        if (seek > 50500 && seek < 55000)
        {
            alpha += 1.5;
        }

        // Move along the cave when entered
        if (z[0] < 2)
        {
            for (int i = 1; i < z.length; i++)
            {
                z[i] -= 0.16;
            }
        }

        // Valdyn approaching
        if (seek > 66500 && seek < 71000)
        {
            valdynCoord.translate(-1.05 * extrp, -1.5 * 1.28 * extrp);
            if (valdynCoord.getX() < 195)
            {
                valdynCoord.setX(195);
            }
            if (valdynCoord.getY() < 16)
            {
                valdynCoord.setY(16);
            }
        }

        // Fade out from cave
        if (seek > 71260 && seek < 71900)
        {
            alpha -= 15.0;
        }

        // Fade in to equipment
        if (seek > 71900 && seek < 72400)
        {
            alpha += 15.0;
        }

        // Equipment
        if (seek > 75100 && seek < 76100)
        {
            equipSword.update(extrp);
        }
        if (seek > 76900 && seek < 77900)
        {
            equipFoot.update(extrp);
        }
        if (seek > 78670 && seek < 79670)
        {
            equipHand.update(extrp);
        }

        // Fade out from equipment
        if (seek > 80800 && seek < 81370)
        {
            alpha -= 15.0;
        }

        // Fade in to valdyn rage
        if (seek > 81370 && seek < 81900)
        {
            alpha += 15.0;
        }

        // Fade out from valdyn rage
        if (seek > 86430 && seek < 88000)
        {
            alpha -= 10.0;
        }

        // Fade in valdyn rage
        if (seek > 83300 && seek < 84560)
        {
            alpha2 += 10.0;
        }
        if (seek > 84340 && seek < 84900 && flash < 12)
        {
            flash++;
        }

        // Fade out valdyn rage
        if (seek > 84900 && seek < 85800)
        {
            alpha2 -= 10.0;
        }

        alpha = UtilMath.clamp(alpha, 0.0, 255.0);
        alpha2 = UtilMath.clamp(alpha2, 0.0, 255.0);
    }

    /**
     * Render part.
     * 
     * @param width The width.
     * @param height The height.
     * @param seek The current seek.
     * @param g The graphic output.
     */
    public void render(int width, int height, long seek, Graphic g)
    {
        g.clear(0, 0, width, height);

        final int bandHeight = (int) (Math.floor(height - 144) / 2.0);

        // Render door
        if (z[0] > 2)
        {
            door.setLocation(Math.floor(width / 2.0) - door.getTileWidth() / 2, height / 2 - door.getTileHeight() / 2);
            door.render(g);
        }

        // Render cave
        if (z[0] < 2 && seek < 71900)
        {
            if (z[7] > 0)
            {
                final double caveZ = UtilMath.clamp(1000 / z[7], 5, 100);
                if (caveZ < 100)
                {
                    cave1.stretch(caveZ, caveZ);
                }
            }
            cave1.setLocation(width / 2 - cave1.getWidth() / 2, height / 2 - cave1.getHeight() / 2);
            cave1.render(g);

            // Render pillars
            for (int i = pillar.length - 1; i >= 0; i--)
            {
                final double newPillarZ = z[1 + i];
                if (newPillarZ > 0)
                {
                    final double pillarZ = 1000.0 / newPillarZ;
                    final double offset;
                    if (i % 2 == 1)
                    {
                        offset = -24 + i * 4 - pillarZ;
                    }
                    else
                    {
                        offset = 24 - i * 4 + pillarZ;
                    }
                    final double scale = UtilMath.clamp(pillarZ, -19, 500);
                    pillar[i].stretch(10 + scale, 10 + scale);
                    pillar[i].setLocation(width / 2 - pillar[i].getWidth() / 2 + offset,
                                          height / 2 - pillar[i].getHeight() / 2);
                    pillar[i].render(g);
                }
            }
        }

        // Render valdyn
        if (seek > 66500 && seek < 72200)
        {
            valdyn.setLocation((int) valdynCoord.getX(), (int) valdynCoord.getY() + bandHeight);
            valdyn.render(g);
        }

        // Render cave 2
        if (seek > 71900 && seek < 81370)
        {
            cave2.setLocation(width / 2, height / 2);
            cave2.render(g);
        }

        // Render equipment
        if (seek > 74500 && seek < 81370)
        {
            equipSword.setLocation(20, bandHeight + 4);
            equipSword.render(g);
        }
        if (seek > 76300 && seek < 81370)
        {
            equipFoot.setLocation(70, bandHeight + 11);
            equipFoot.render(g);
        }
        if (seek > 78060 && seek < 81370)
        {
            equipHand.setLocation(120, bandHeight + 19);
            equipHand.render(g);
        }

        // Render valdyn rage
        if (seek > 81370 && seek < 88000)
        {
            valdyn0.setLocation(width / 2, height / 2);
            valdyn0.render(g);
        }
        if (seek > 83300 && seek < 88000)
        {
            valdyn1.setAlpha((int) alpha2);
            valdyn1.setLocation(width / 2, height / 2);
            valdyn1.render(g);
            if (flash % 3 == 1 || flash % 3 == 2)
            {
                valdyn2.setLocation(width / 2, height / 2);
                valdyn2.render(g);
            }
        }

        // Render fade
        if (seek > 50500 && alpha < 255)
        {
            g.setColor(Constant.ALPHAS_BLACK[255 - (int) alpha]);
            g.drawRect(0, 0, width, height, true);
        }

        g.clear(0, 0, width, bandHeight);
        g.clear(0, height - bandHeight, width, bandHeight);
    }
}
