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

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UpdatableVoid;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.geom.Coord;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.RenderableVoid;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.Time;
import com.b3dgs.lionheart.constant.Folder;

/**
 * Intro part 2 implementation.
 */
public final class Part2 implements Updatable, Renderable
{
    private static final String PART2_FOLDER = "part2";

    private static final int BAND_HEIGHT = 144;
    private static final int PILLAR_COUNT = 6;

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

    private static final int RAGE_FLASH_DELAY = 40;
    private static final int RAGE_FLASH_COUNT = 4;

    private static final double SPEED_CAVE_FADE_IN = 3;
    private static final double SPEED_CAVE_FADE_OUT = 12;
    private static final double SPEED_EQUIP_FADE_IN = 12;
    private static final double SPEED_EQUIP_FADE_OUT = 12;
    private static final double SPEED_RAGE_FADE_IN = 8;
    private static final double SPEED_RAGE_FADE_OUT = 8;
    private static final double SPEED_RAGE_START = 8;
    private static final double SPEED_RAGE_END = 4;

    private static final int TIME_DOOR_OPEN_MS = 47500;
    private static final int TIME_DOOR_ENTER_MS = 48700;
    private static final int TIME_CAVE_FADE_IN_MS = 50500;
    private static final int TIME_VALDYN_MOVE_MS = 66900;
    private static final int TIME_CAVE_FADE_OUT_MS = 71500;
    private static final int TIME_EQUIP_FADE_IN_MS = 72100;
    private static final int TIME_SHOW_SWORD_MS = 74800;
    private static final int TIME_EQUIP_SWORD_MS = 75400;
    private static final int TIME_SHOW_FOOT_MS = 76500;
    private static final int TIME_EQUIP_FOOT_MS = 77100;
    private static final int TIME_SHOW_HAND_MS = 78300;
    private static final int TIME_EQUIP_HAND_MS = 78800;
    private static final int TIME_EQUIP_FADE_OUT_MS = 81000;
    private static final int TIME_RAGE_FADE_IN_MS = 81500;
    private static final int TIME_RAGE_START_MS = 83600;
    private static final int TIME_RAGE_FLASH_MS = 84700;
    private static final int TIME_RAGE_END_MS = 85500;
    private static final int TIME_RAGE_FADE_OUT_MS = 86500;

    /**
     * Get media from filename.
     * 
     * @param filename The filename.
     * @return The media.
     */
    private static Media get(String filename)
    {
        return Medias.create(Folder.INTRO, PART2_FOLDER, filename + ".png");
    }

    private final Sprite[] pillar = new Sprite[PILLAR_COUNT];
    private final SpriteAnimated door = Drawable.loadSpriteAnimated(get("door"), 3, 2);

    private final Sprite cave1 = Drawable.loadSprite(get("cave1"));
    private final Sprite cave2 = Drawable.loadSprite(get("cave2"));
    private final Sprite valdyn = Drawable.loadSprite(get("valdyn"));

    private final SpriteAnimated equipSword = Drawable.loadSpriteAnimated(get("sword"), 3, 1);
    private final SpriteAnimated equipFoot = Drawable.loadSpriteAnimated(get("foot"), 3, 1);
    private final SpriteAnimated equipHand = Drawable.loadSpriteAnimated(get("hand"), 3, 1);

    private final Sprite valdyn0 = Drawable.loadSprite(get("valdyn0"));
    private final Sprite valdyn1 = Drawable.loadSprite(get("valdyn1"));
    private final Sprite valdyn2 = Drawable.loadSprite(get("valdyn2"));

    private final Coord valdynCoord = new Coord(310, 240);
    private final double[] z = new double[2 + pillar.length];
    private final int lastPillarIndex = pillar.length + 1;
    private final Tick flashTime = new Tick();
    private final Time time;
    private final int width;
    private final int height;
    private final int rate;
    private final int bandHeight;

    private Updatable updaterCave = this::updateDoorInit;
    private Updatable updaterFade = this::updateFadeInitCave;
    private Updatable updaterRage = this::updateRageInit;
    private Updatable updaterValdyn = this::updateValdynInit;
    private Updatable updaterEquip = this::updateEquipInit;
    private Renderable rendererCave = this::renderDoor;
    private Renderable rendererValdyn = RenderableVoid.getInstance();
    private Renderable rendererEquip = RenderableVoid.getInstance();
    private Renderable rendererRage = RenderableVoid.getInstance();
    private Renderable rendererFade = RenderableVoid.getInstance();

    private double alpha;
    private double alpha2;
    private int alpha2old;
    private int flash;

    /**
     * Constructor.
     * 
     * @param time The time reference.
     * @param width The screen width.
     * @param height The screen height.
     * @param rate The rate.
     */
    public Part2(Time time, int width, int height, int rate)
    {
        super();

        this.time = time;
        this.width = width;
        this.height = height;
        this.rate = rate;
        bandHeight = (int) (Math.floor(height - BAND_HEIGHT) / 2.0);
    }

    /**
     * Load part.
     */
    public void load()
    {
        door.load();

        for (int i = 0; i < pillar.length; i++)
        {
            pillar[i] = Drawable.loadSprite(get("pillar"));
            pillar[i].load();
        }
        valdyn.load();

        cave1.load();
        cave2.load();
        cave2.setOrigin(Origin.MIDDLE);

        equipFoot.load();
        equipSword.load();
        equipHand.load();

        valdyn0.load();
        valdyn0.setOrigin(Origin.MIDDLE);
        valdyn1.load();
        valdyn1.setOrigin(Origin.MIDDLE);
        valdyn1.setAlpha(0);
        valdyn2.load();
        valdyn2.setOrigin(Origin.MIDDLE);

        final Animation animDoor = new Animation(Animation.DEFAULT_NAME, 1, 6, 0.18, false, false);
        door.play(animDoor);

        final Animation animEquip = new Animation(Animation.DEFAULT_NAME, 1, 3, 0.18, false, false);
        equipFoot.play(animEquip);
        equipSword.play(animEquip);
        equipHand.play(animEquip);

        z[0] = Z_DOOR_INIT;
        for (int i = 1; i < z.length; i++)
        {
            z[i] = i * Z_PILLAR_INDEX_MULT + Z_PILLAR_INIT;
        }
    }

    /**
     * Update door init time.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateDoorInit(double extrp)
    {
        if (time.isAfter(TIME_DOOR_OPEN_MS))
        {
            updaterCave = this::updateDoorOpen;
            rendererCave = this::renderDoor;
        }
    }

    /**
     * Update door open animation until enter phase.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateDoorOpen(double extrp)
    {
        door.update(extrp);

        if (time.isAfter(TIME_DOOR_ENTER_MS))
        {
            updaterCave = this::updateDoorEnter;
        }
    }

    /**
     * Update entering door phase until cave enter.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateDoorEnter(double extrp)
    {
        z[0] -= Z_DOOR_SPEED * extrp;

        final double doorZ = UtilMath.clamp(1000.0 / z[0], 100.0, 800.0);
        door.stretch(doorZ, doorZ);

        if (z[0] < 2.0)
        {
            updaterCave = this::updateCaveEnter;
            rendererFade = this::renderFade;
        }
    }

    /**
     * Update enter cave phase.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateCaveEnter(double extrp)
    {
        for (int i = 1; i < z.length; i++)
        {
            z[i] -= Z_PILLAR_SPEED * extrp;
        }
    }

    /**
     * Update valdyn approach init time.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateValdynInit(double extrp)
    {
        if (time.isAfter(TIME_VALDYN_MOVE_MS))
        {
            updaterValdyn = this::updateValdynMove;
            rendererValdyn = this::renderValdyn;
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
            updaterValdyn = UpdatableVoid.getInstance();
        }
        if (valdynCoord.getY() < VALDYN_MAX_Y)
        {
            valdynCoord.setY(VALDYN_MAX_Y);
            updaterValdyn = UpdatableVoid.getInstance();
        }
    }

    /**
     * Update fade in cave time.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFadeInitCave(double extrp)
    {
        if (time.isAfter(TIME_CAVE_FADE_IN_MS))
        {
            alpha = 255.0;
            updaterFade = this::updateFadeInCave;
            rendererFade = this::renderFade;
            rendererCave = this::renderCave;
        }
    }

    /**
     * Update fade in cave routine.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFadeInCave(double extrp)
    {
        alpha -= SPEED_CAVE_FADE_IN * extrp;

        if (getAlpha() < 0)
        {
            alpha = 0.0;
            updaterFade = this::updateFadeOutInitCave;
            rendererFade = this::renderFade;
        }
    }

    /**
     * Update fade out cave time.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFadeOutInitCave(double extrp)
    {
        if (time.isAfter(TIME_CAVE_FADE_OUT_MS))
        {
            updaterFade = this::updateFadeOutCave;
            rendererFade = this::renderFade;
        }
    }

    /**
     * Update fade out cave routine.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFadeOutCave(double extrp)
    {
        alpha += SPEED_CAVE_FADE_OUT * extrp;

        if (getAlpha() > 255)
        {
            alpha = 255.0;
            updaterFade = this::updateFadeInEquipInit;
            rendererValdyn = RenderableVoid.getInstance();
        }
    }

    /**
     * Update fade in equipment time.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFadeInEquipInit(double extrp)
    {
        if (time.isAfter(TIME_EQUIP_FADE_IN_MS))
        {
            alpha = 255.0;
            updaterFade = this::updateFadeInEquip;
            rendererValdyn = RenderableVoid.getInstance();
            rendererEquip = this::renderEquip;
        }
    }

    /**
     * Update fade in equipment routine.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFadeInEquip(double extrp)
    {
        alpha -= SPEED_EQUIP_FADE_IN * extrp;

        if (getAlpha() < 0)
        {
            alpha = 0.0;
            updaterFade = this::updateFadeOutEquipInit;
            rendererCave = RenderableVoid.getInstance();
            rendererValdyn = RenderableVoid.getInstance();
        }
    }

    /**
     * Update fade out equipment time.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFadeOutEquipInit(double extrp)
    {
        if (time.isAfter(TIME_EQUIP_FADE_OUT_MS))
        {
            updaterFade = this::updateFadeOutEquip;
            rendererFade = this::renderFade;
        }
    }

    /**
     * Update fade out equipment routine.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFadeOutEquip(double extrp)
    {
        alpha += SPEED_EQUIP_FADE_OUT * extrp;

        if (getAlpha() > 255)
        {
            alpha = 255.0;
            updaterFade = this::updateFadeInRageInit;
            rendererEquip = RenderableVoid.getInstance();
            rendererFade = this::renderFade;
        }
    }

    /**
     * Update fade in rage time.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFadeInRageInit(double extrp)
    {
        if (time.isAfter(TIME_RAGE_FADE_IN_MS))
        {
            alpha = 255.0;
            updaterFade = this::updateFadeInRage;
            rendererCave = RenderableVoid.getInstance();
            rendererEquip = RenderableVoid.getInstance();
            rendererRage = this::renderRage;
            rendererFade = this::renderFade;
        }
    }

    /**
     * Update fade in rage routine.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFadeInRage(double extrp)
    {
        alpha -= SPEED_RAGE_FADE_IN * extrp;

        if (getAlpha() < 0)
        {
            alpha = 0.0;
            updaterFade = this::updateFadeOutRageInit;
        }
    }

    /**
     * Update fade out rage time.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFadeOutRageInit(double extrp)
    {
        if (time.isAfter(TIME_RAGE_FADE_OUT_MS))
        {
            updaterFade = this::updateFadeOutRage;
        }
    }

    /**
     * Update fade out rage routine.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFadeOutRage(double extrp)
    {
        alpha += SPEED_RAGE_FADE_OUT * extrp;

        if (getAlpha() > 255)
        {
            alpha = 255.0;
            updaterFade = UpdatableVoid.getInstance();
            rendererRage = RenderableVoid.getInstance();
        }
    }

    /**
     * Update rage time.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateRageInit(double extrp)
    {
        if (time.isAfter(TIME_RAGE_START_MS))
        {
            updaterRage = this::updateRageStart;
            rendererRage = this::renderRageFlash;
        }
    }

    /**
     * Update rage start fade before flash.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateRageStart(double extrp)
    {
        alpha2old = getAlpha2();
        alpha2 += SPEED_RAGE_START * extrp;

        if (getAlpha2() > 255)
        {
            alpha2 = 255.0;
            updaterRage = this::updateRageFlashInit;
        }
    }

    /**
     * Update rage flash time.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateRageFlashInit(double extrp)
    {
        if (time.isAfter(TIME_RAGE_FLASH_MS))
        {
            flashTime.start();
            updaterRage = this::updateRageFlashStart;
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
        if (flashTime.elapsedTime(rate, RAGE_FLASH_DELAY))
        {
            flash++;
            flashTime.restart();
        }

        if (flash > RAGE_FLASH_COUNT * 2)
        {
            flash = 0;
            flashTime.stop();
            updaterRage = this::updateRageEndInit;
        }
    }

    /**
     * Update rage end time.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateRageEndInit(double extrp)
    {
        if (time.isAfter(TIME_RAGE_END_MS))
        {
            updaterRage = this::updateRageEnd;
        }
    }

    /**
     * Update rage end fade after flash.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateRageEnd(double extrp)
    {
        alpha2old = getAlpha2();
        alpha2 -= SPEED_RAGE_END * extrp;

        if (getAlpha2() < 0)
        {
            alpha2 = 0.0;
            updaterRage = UpdatableVoid.getInstance();
        }
    }

    /**
     * Update equipment time.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateEquipInit(double extrp)
    {
        if (time.isAfter(TIME_SHOW_SWORD_MS))
        {
            rendererEquip = this::renderEquipSword;
        }
        if (time.isAfter(TIME_EQUIP_SWORD_MS))
        {
            updaterEquip = this::updateEquipSword;
        }
    }

    /**
     * Update equipment sword routine.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateEquipSword(double extrp)
    {
        equipSword.update(extrp);

        if (time.isAfter(TIME_SHOW_FOOT_MS))
        {
            rendererEquip = this::renderEquipFoot;
        }
        if (time.isAfter(TIME_EQUIP_FOOT_MS))
        {
            updaterEquip = this::updateEquipFoot;
        }
    }

    /**
     * Update equipment foot routine.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateEquipFoot(double extrp)
    {
        equipFoot.update(extrp);

        if (time.isAfter(TIME_SHOW_HAND_MS))
        {
            rendererEquip = this::renderEquipHand;
        }
        if (time.isAfter(TIME_EQUIP_HAND_MS))
        {
            updaterEquip = this::updateEquipHand;
        }
    }

    /**
     * Update equipment hand routine.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateEquipHand(double extrp)
    {
        equipHand.update(extrp);

        if (equipHand.getAnimState() == AnimState.FINISHED)
        {
            updaterEquip = UpdatableVoid.getInstance();
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
    }

    /**
     * Render cave.
     * 
     * @param g The graphic output.
     */
    private void renderCave(Graphic g)
    {
        if (z[lastPillarIndex] > 0)
        {
            final double caveZ = UtilMath.clamp(1000.0 / z[lastPillarIndex], 5.0, 100.0);
            if (caveZ < 100.0)
            {
                cave1.stretch(caveZ, caveZ);
            }
        }
        cave1.setLocation(width / 2.0 - cave1.getWidth() / 2.0, height / 2.0 - cave1.getHeight() / 2.0);
        cave1.render(g);

        // Render pillars
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
    }

    /**
     * Render valdyn in cave.
     * 
     * @param g The graphic output.
     */
    private void renderValdyn(Graphic g)
    {
        valdyn.setLocation(valdynCoord.getX(), valdynCoord.getY() + bandHeight);
        valdyn.render(g);
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
    }

    /**
     * Render rage flash and flicker effect.
     * 
     * @param g The graphic output.
     */
    private void renderRageFlash(Graphic g)
    {
        renderRage(g);

        if (alpha2old != alpha2)
        {
            valdyn1.setAlpha(getAlpha2());
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
     * Get alpha value.
     * 
     * @return The alpha value.
     */
    private int getAlpha()
    {
        return (int) Math.floor(alpha);
    }

    /**
     * Get alpha2 value.
     * 
     * @return The alpha2 value.
     */
    private int getAlpha2()
    {
        return (int) Math.floor(alpha2);
    }

    /**
     * Render fade effect.
     * 
     * @param g The graphic output.
     */
    private void renderFade(Graphic g)
    {
        final int a = getAlpha();
        if (a > 0)
        {
            g.setColor(Constant.ALPHAS_BLACK[a]);
            g.drawRect(0, 0, width, height, true);
            g.setColor(ColorRgba.BLACK);
        }
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

    @Override
    public void update(double extrp)
    {
        updaterCave.update(extrp);
        updaterFade.update(extrp);
        updaterValdyn.update(extrp);
        updaterEquip.update(extrp);
        updaterRage.update(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        g.clear(0, 0, width, height);

        rendererCave.render(g);
        rendererValdyn.render(g);
        rendererEquip.render(g);
        rendererRage.render(g);
        rendererFade.render(g);

        renderBand(g);
    }
}
