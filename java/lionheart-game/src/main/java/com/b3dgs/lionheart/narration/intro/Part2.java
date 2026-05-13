/*
 * Copyright (C) 2013-2026 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionheart.narration.intro;

import java.io.Closeable;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.geom.Coord;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;
import com.b3dgs.lionheart.FadeSide;
import com.b3dgs.lionheart.Util;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.narration.NarrationFactory;

/**
 * Intro part 2 implementation.
 */
public final class Part2 implements Closeable
{
    private static final String PART2_FOLDER = "part2";

    private static final int BAND_HEIGHT = 144;
    private static final int PILLAR_COUNT = 6;
    private static final int LAST_PILLAR_INDEX = PILLAR_COUNT + 1;

    private static final int Z_DOOR_INIT = 10;
    private static final int Z_PILLAR_INIT = 25;
    private static final int Z_PILLAR_INDEX_MULT = 13;

    private static final double Z_DOOR_SPEED = 0.08;
    private static final double Z_PILLAR_SPEED = 0.2;

    private static final int PILLAR_AMPLITUDE = 10;
    private static final int PILLAR_AMPLITUDE_INDEX_MULT = 1;
    private static final int PILLAR_SCALE_BASE = 10;
    private static final int PILLAR_SCALE_MIN = -20;
    private static final int PILLAR_SCALE_MAX = 500;

    private static final double VALDYN_SPEED_X = -1.25;
    private static final double VALDYN_SPEED_Y = -2.3;
    private static final int VALDYN_MAX_X = 185;
    private static final int VALDYN_MAX_Y = 16;

    private static final int EQUIP_SWORD_X = 20;
    private static final int EQUIP_SWORD_Y = 4;
    private static final int EQUIP_FOOT_X = 70;
    private static final int EQUIP_FOOT_Y = 11;
    private static final int EQUIP_HAND_X = 120;
    private static final int EQUIP_HAND_Y = 19;

    private static final double FADE_IN_CAVE = 3;
    private static final double FADE_OUT_CAVE = 12;
    private static final double FADE_EQUIP = 12;
    private static final double FADE_RAGE = 8;

    private static final int RAGE_FLASH_DELAY = 40;
    private static final int RAGE_FLASH_COUNT = 4;

    private static final double FADE_RAGE_START = 8;
    private static final double FADE_RAGE_END = 4;

    private static final int T_DOOR_OPEN = 47_500;
    private static final int T_DOOR_ENTER = 49_000;
    private static final int T_CAVE_FADE_IN = 50_500;
    private static final int T_VALDYN_MOVE = 66_900;
    private static final int T_CAVE_FADE_OUT = 71_500;

    private static final int T_EQUIP_FADE_IN = 72_100;
    private static final int T_SWORD_SHOW = 74_800;
    private static final int T_SWORD_EQUIP = 75_400;
    private static final int T_FOOT_SHOW = 76_500;
    private static final int T_FOOT_EQUIP = 77_100;
    private static final int T_HAND_SHOW = 78_300;
    private static final int T_HAND_EQUIP = 78_800;
    private static final int T_EQUIP_FADE_OUT = 81_000;

    private static final int T_RAGE_FADE_IN = 81_500;
    private static final int T_RAGE_START = 83_300;
    private static final int T_RAGE_FLASH = 84_800;
    private static final int T_RAGE_END = 85_800;
    private static final int T_RAGE_FADE_OUT = 86_800;

    private static final int T_END = 89_000;

    private final Sprite[] pillar = new Sprite[PILLAR_COUNT];
    private final SpriteAnimated door = Util.get(3, 2, Folder.INTRO, PART2_FOLDER, "door.png");

    private final Sprite cave1 = Util.get(Folder.INTRO, PART2_FOLDER, "cave1.png");
    private final Sprite cave2 = Util.get(Folder.INTRO, PART2_FOLDER, "cave2.png");
    private final Sprite valdyn = Util.get(Folder.INTRO, PART2_FOLDER, "valdyn.png");

    private final SpriteAnimated equipSword = Util.get(3, 1, Folder.INTRO, PART2_FOLDER, "sword.png");
    private final SpriteAnimated equipFoot = Util.get(3, 1, Folder.INTRO, PART2_FOLDER, "foot.png");
    private final SpriteAnimated equipHand = Util.get(3, 1, Folder.INTRO, PART2_FOLDER, "hand.png");

    private final Sprite valdyn0 = Util.get(Folder.INTRO, PART2_FOLDER, "valdyn0.png");
    private final Sprite valdyn1 = Util.get(Folder.INTRO, PART2_FOLDER, "valdyn1.png");
    private final Sprite valdyn2 = Util.get(Folder.INTRO, PART2_FOLDER, "valdyn2.png");

    private final Coord valdynCoord = new Coord(310, 240);
    private final double[] z = new double[2 + PILLAR_COUNT];
    private final Tick flashTime = new Tick();

    private final int width;
    private final int height;
    private final int rate;
    private final int bandHeight;

    private double alphaRage;
    private int alphaRageOld;
    private int flash = -1;

    /**
     * Constructor.
     * 
     * @param width The screen width.
     * @param height The screen height.
     * @param rate The rate.
     */
    public Part2(int width, int height, int rate)
    {
        super();

        this.width = width;
        this.height = height;
        this.rate = rate;
        bandHeight = (int) Math.floor((height - BAND_HEIGHT) / 2.0);
    }

    /**
     * Load part.
     * 
     * @param narration The narration factory.
     */
    public void load(NarrationFactory narration)
    {
        for (int i = 0; i < pillar.length; i++)
        {
            pillar[i] = Util.get(Folder.INTRO, PART2_FOLDER, "pillar.png");
        }
        cave2.setOrigin(Origin.MIDDLE);
        valdyn0.setOrigin(Origin.MIDDLE);
        valdyn1.setOrigin(Origin.MIDDLE);
        valdyn2.setOrigin(Origin.MIDDLE);

        valdyn1.setAlpha(0);

        final Animation animDoor = new Animation(Animation.DEFAULT_NAME, 1, 6, 0.18, false, false);
        door.play(animDoor);

        final Animation animEquip = new Animation(Animation.DEFAULT_NAME, 1, 3, 0.18, false, false);
        equipFoot.play(animEquip);
        equipSword.play(animEquip);
        equipHand.play(animEquip);

        z[0] = Z_DOOR_INIT;
        for (int i = 1; i < z.length; i++)
        {
            z[i] = i * (double) Z_PILLAR_INDEX_MULT + Z_PILLAR_INIT;
        }

        init(narration);
    }

    private void init(NarrationFactory n)
    {
        n.add(T_DOOR_OPEN, T_DOOR_ENTER, door::update, this::renderDoor);
        n.add(T_DOOR_ENTER, T_CAVE_FADE_IN, this::updateDoorEnter, this::renderDoor);

        n.add(T_CAVE_FADE_IN, FadeSide.IN, FADE_IN_CAVE);
        n.add(T_CAVE_FADE_IN, T_VALDYN_MOVE, this::updateCaveEnter, this::renderCave);
        n.add(T_VALDYN_MOVE, T_EQUIP_FADE_IN, this::updateValdynMove, this::renderValdyn);
        n.add(T_CAVE_FADE_OUT, FadeSide.OUT, FADE_OUT_CAVE);

        n.add(T_EQUIP_FADE_IN, FadeSide.IN, FADE_EQUIP);
        n.add(T_EQUIP_FADE_IN, T_SWORD_SHOW, this::renderEquip);
        n.add(T_SWORD_SHOW, T_SWORD_EQUIP, this::renderEquipSword);
        n.add(T_SWORD_EQUIP, T_FOOT_SHOW, equipSword::update, this::renderEquipSword);
        n.add(T_FOOT_SHOW, T_FOOT_EQUIP, this::renderEquipFoot);
        n.add(T_FOOT_EQUIP, T_HAND_SHOW, equipFoot::update, this::renderEquipFoot);
        n.add(T_HAND_SHOW, T_HAND_EQUIP, this::renderEquipHand);
        n.add(T_HAND_EQUIP, T_RAGE_FADE_IN, equipHand::update, this::renderEquipHand);
        n.add(T_EQUIP_FADE_OUT, FadeSide.OUT, FADE_EQUIP);

        n.add(T_RAGE_FADE_IN, FadeSide.IN, FADE_RAGE);
        n.add(T_RAGE_FADE_IN, T_END, this::renderRage);
        n.add(T_RAGE_START, T_RAGE_FLASH, this::updateRageStart);
        n.add(T_RAGE_FLASH, T_RAGE_END, this::updateRageFlashStart);
        n.add(T_RAGE_END, T_END, this::updateRageEnd);
        n.add(T_RAGE_FADE_OUT, FadeSide.OUT, FADE_RAGE);
    }

    @Override
    public void close()
    {
        for (int i = 0; i < pillar.length; i++)
        {
            pillar[i].dispose();
        }
        door.dispose();
        cave1.dispose();
        cave2.dispose();
        valdyn.dispose();
        equipSword.dispose();
        equipFoot.dispose();
        equipHand.dispose();
        valdyn0.dispose();
        valdyn1.dispose();
        valdyn2.dispose();
    }

    /**
     * Update entering door phase until cave enter.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateDoorEnter(double extrp)
    {
        if (z[0] > 2.0)
        {
            z[0] -= Z_DOOR_SPEED * extrp;

            final double doorZ = UtilMath.clamp(1000.0 / z[0], 100.0, 800.0);
            door.stretch(doorZ, doorZ);
        }
    }

    /**
     * Update enter cave phase.
     * 
     * @param extrp The extrapolation value.
     */
    public void updateCaveEnter(double extrp)
    {
        for (int i = 1; i < z.length; i++)
        {
            z[i] -= Z_PILLAR_SPEED * extrp;
        }
    }

    /**
     * Update valdyn approach movement.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateValdynMove(double extrp)
    {
        valdynCoord.translate(VALDYN_SPEED_X * extrp, VALDYN_SPEED_Y * extrp);

        if (valdynCoord.getX() < VALDYN_MAX_X)
        {
            valdynCoord.setX(VALDYN_MAX_X);
        }
        if (valdynCoord.getY() < VALDYN_MAX_Y)
        {
            valdynCoord.setY(VALDYN_MAX_Y);
        }
    }

    /**
     * Update rage start fade before flash.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateRageStart(double extrp)
    {
        alphaRageOld = getAlphaRage();
        alphaRage += FADE_RAGE_START * extrp;

        if (getAlphaRage() > 255)
        {
            alphaRage = 255.0;
        }
    }

    /**
     * Update rage flash counter.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateRageFlashStart(double extrp)
    {
        flashTime.update(extrp);
        if (flash < 0 || flashTime.elapsedTime(rate, RAGE_FLASH_DELAY))
        {
            flashTime.restart();

            flash++;
            if (flash > RAGE_FLASH_COUNT * 2)
            {
                flashTime.stop();
                flash = 0;
            }
        }
    }

    /**
     * Update rage end fade after flash.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateRageEnd(double extrp)
    {
        alphaRageOld = getAlphaRage();
        alphaRage -= FADE_RAGE_END * extrp;

        if (getAlphaRage() < 0)
        {
            alphaRage = 0.0;
        }
    }

    /**
     * Render door and its opening.
     * 
     * @param g The graphic output.
     */
    private void renderDoor(Graphic g)
    {
        door.setLocation(Math.floor(width / 2.0) - door.getTileWidth() / 2.0,
                         height / 2.0 - door.getTileHeight() / 2.0);
        door.render(g);

        renderBand(g);
    }

    /**
     * Render cave.
     * 
     * @param g The graphic output.
     */
    private void renderCave(Graphic g)
    {
        if (z[LAST_PILLAR_INDEX] > 0)
        {
            final double caveZ = UtilMath.clamp(1000.0 / z[LAST_PILLAR_INDEX], 5.0, 100.0);
            if (caveZ < 100.0)
            {
                cave1.stretch(caveZ, caveZ);
            }
        }
        cave1.setLocation(width / 2.0 - cave1.getWidth() / 2.0, height / 2.0 - cave1.getHeight() / 2.0);
        cave1.render(g);

        for (int i = pillar.length - 1; i >= 0; i--)
        {
            final double newPillarZ = z[1 + i];
            if (newPillarZ > 0.0)
            {
                final double pillarZ = 1000.0 / newPillarZ;
                final double offset;
                if (i % 2 == 1)
                {
                    offset = -PILLAR_AMPLITUDE + i * PILLAR_AMPLITUDE_INDEX_MULT - pillarZ;
                }
                else
                {
                    offset = PILLAR_AMPLITUDE - i * PILLAR_AMPLITUDE_INDEX_MULT + pillarZ;
                }
                final double scale = UtilMath.clamp(pillarZ, PILLAR_SCALE_MIN, PILLAR_SCALE_MAX);
                pillar[i].stretch(PILLAR_SCALE_BASE + scale, PILLAR_SCALE_BASE + scale);
                pillar[i].setLocation(width / 2.0 - pillar[i].getWidth() / 2.0 + offset,
                                      height / 2.0 - pillar[i].getHeight() / 2.0);
                pillar[i].render(g);
            }
        }
        renderBand(g);
    }

    /**
     * Render valdyn in cave.
     * 
     * @param g The graphic output.
     */
    private void renderValdyn(Graphic g)
    {
        cave1.render(g);

        valdyn.setLocation(valdynCoord.getX(), valdynCoord.getY() + bandHeight);
        valdyn.render(g);

        renderBand(g);
    }

    /**
     * Render equipment background.
     * 
     * @param g The graphic output.
     */
    private void renderEquip(Graphic g)
    {
        cave2.setLocation(width / 2.0, height / 2.0);
        cave2.render(g);
    }

    /**
     * Render equipment sword picture.
     * 
     * @param g The graphic output.
     */
    private void renderEquipSword(Graphic g)
    {
        renderEquip(g);

        equipSword.setLocation(EQUIP_SWORD_X, bandHeight + EQUIP_SWORD_Y);
        equipSword.render(g);
    }

    /**
     * Render equipment foot picture.
     * 
     * @param g The graphic output.
     */
    private void renderEquipFoot(Graphic g)
    {
        renderEquipSword(g);

        equipFoot.setLocation(EQUIP_FOOT_X, bandHeight + EQUIP_FOOT_Y);
        equipFoot.render(g);
    }

    /**
     * Render equipment hand picture.
     * 
     * @param g The graphic output.
     */
    private void renderEquipHand(Graphic g)
    {
        renderEquipFoot(g);

        equipHand.setLocation(EQUIP_HAND_X, bandHeight + EQUIP_HAND_Y);
        equipHand.render(g);
    }

    /**
     * Render rage valdyn.
     * 
     * @param g The graphic output.
     */
    private void renderRage(Graphic g)
    {
        valdyn0.setLocation(width / 2.0, height / 2.0);
        valdyn0.render(g);

        if (alphaRageOld != getAlphaRage())
        {
            valdyn1.setAlpha(getAlphaRage());
        }
        valdyn1.setLocation(width / 2.0, height / 2.0);
        valdyn1.render(g);

        if (flash % 2 == 1)
        {
            valdyn2.setLocation(width / 2.0, height / 2.0);
            valdyn2.render(g);
        }
    }

    /**
     * Get alpha2 value.
     * 
     * @return The alpha2 value.
     */
    private int getAlphaRage()
    {
        return (int) Math.floor(alphaRage);
    }

    /**
     * Render horizontal top and bottom band.
     * 
     * @param g The graphic output.
     */
    private void renderBand(Graphic g)
    {
        g.clear(0, 0, width, bandHeight);
        g.clear(0, height - bandHeight, width, bandHeight);
    }
}
