/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UpdatableVoid;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.UtilRandom;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.CameraTracker;
import com.b3dgs.lionengine.game.feature.ComponentDisplayable;
import com.b3dgs.lionengine.game.feature.ComponentRefreshable;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Spawner;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.RenderableVoid;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;
import com.b3dgs.lionengine.graphic.engine.Sequence;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionDelegate;
import com.b3dgs.lionengine.helper.DeviceControllerConfig;
import com.b3dgs.lionengine.helper.MapTileHelper;
import com.b3dgs.lionengine.io.DeviceController;
import com.b3dgs.lionheart.AppInfo;
import com.b3dgs.lionheart.CheatsProvider;
import com.b3dgs.lionheart.CheckpointHandler;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.DeviceMapping;
import com.b3dgs.lionheart.GameConfig;
import com.b3dgs.lionheart.Time;
import com.b3dgs.lionheart.Util;
import com.b3dgs.lionheart.constant.Folder;

/**
 * Extro part 1 implementation.
 */
// CHECKSTYLE IGNORE LINE: FanOutComplexity|DataAbstractionCoupling
public class Part1 extends Sequence
{
    private static final int MIN_HEIGHT = 208;
    private static final int MAX_WIDTH = 400;
    private static final int MARGIN_WIDTH = 80;
    private static final int SPAWN_EXPLODE_DELAY_MS = 500;
    private static final int SPAWN_EXPLODE_MEDIUM_DELAY_MS = 80;
    private static final int SPAWN_EXPLODE_FAST_DELAY_MS = 15;
    private static final int FADE_SPEED = 5;

    private static final String PART1_FOLDER = "part1";
    private static final String EXPLODE_LITTLE = "ExplodeLittle.xml";
    private static final String EXPLODE_BIG = "ExplodeBig.xml";

    private static final int VALDYN_X = 100;
    private static final int VALDYN_Y = 65;
    private static final int VALDYN_Z = 100;
    private static final int VALDYN_X_MAX = 140;
    private static final double VALDYN_Z_SPEED = 0.00125;
    private static final double VALDYN_Z_ACC = -0.35;
    private static final double VALDYN_Z_ACC_MIN = -0.35;
    private static final double VALDYN_Z_ACC_MAX = -0.10;
    private static final int VALDYN_SCALE_MIN = 10;
    private static final int VALDYN_SCALE_MAX = 400;
    private static final double VALDYN_MOVE_X = -0.05;
    private static final double VALDYN_MOVE_Y = 0.01;
    private static final int VALDYN_MOVE_X_SCALE_DIVISOR = -500;

    private static final int CITADEL_X = 82;
    private static final int CITADEL_Y = 4;
    private static final double CITADEL_FALL_SPEED = 0.05;
    private static final double CITADEL_ACC_MAX = 3.5;

    private static final int EXPLODE_Y_OFFSET = 12;
    private static final double EXPLODE_Y_SCALE = 0.6;

    private static final int TIME_EXPLODE_LOT_MS = 15_300;
    private static final int TIME_CITADEL_DESTROYED_MS = 16_000;
    private static final int TIME_CITADEL_FALL_MS = 16_600;
    private static final int TIME_EXPLODE_END_MS = 18_000;
    private static final int TIME_FADE_OUT_MS = 19_300;

    /**
     * Get media from filename.
     * 
     * @param filename The filename.
     * @return The media.
     */
    private static Media get(String filename)
    {
        return Medias.create(Folder.EXTRO, PART1_FOLDER, filename + ".png");
    }

    /** Device controller reference. */
    final DeviceController device;
    /** Alpha speed. */
    int alphaSpeed = FADE_SPEED;

    private final Sprite backcolor = Drawable.loadSprite(get("backcolor"));
    private final Sprite clouds = Drawable.loadSprite(get("clouds"));
    private final SpriteAnimated citadel = Drawable.loadSpriteAnimated(get("citadel"), 2, 1);
    private final SpriteAnimated valdyn = Drawable.loadSpriteAnimated(get("valdyn"), 4, 3);

    private final Services services = new Services();
    private final Factory factory = services.create(Factory.class);
    private final Handler handler = services.create(Handler.class);
    private final Spawner spawner = services.add((Spawner) (media, x, y) ->
    {
        final Featurable featurable = factory.create(media);
        featurable.getFeature(Transformable.class).teleport(x, y);
        handler.add(featurable);
        return featurable;
    });
    private final Tick tickExplode = new Tick();
    private final Animation valdynAnim = new Animation(Animation.DEFAULT_NAME, 1, 12, 0.25, false, true);
    private final int bandHeight = (int) Math.floor((getHeight() - 208) / 2.0);
    private final AppInfo info;
    private final Time time;
    private final Audio audio;

    private Updatable updaterFade = this::updateFadeIn;
    private Updatable updaterCitadel = this::updateCitadelInit;
    private Updatable updaterValdyn = this::updateValdyn;
    private Updatable updaterExplode = this::updateExplodeFew;

    private Renderable rendererFade = this::renderFade;
    private Renderable rendererValdyn = valdyn;

    private double citadelY = CITADEL_Y;
    private double citadelYacc;
    private double valdynX = VALDYN_X;
    private double valdynY = VALDYN_Y;
    private double valdynZ = VALDYN_Z;
    private double valdynZacc = VALDYN_Z_ACC;
    private double alpha = 255.0;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     * @param config The game config reference.
     * @param time The time reference.
     * @param audio The audio reference.
     * @param alternative The alternative end.
     */
    public Part1(Context context, GameConfig config, Time time, Audio audio, Boolean alternative)
    {
        super(context,
              Util.getResolution(context, MIN_HEIGHT, MAX_WIDTH, MARGIN_WIDTH),
              Util.getLoop(context.getConfig().getOutput()));

        this.time = time;
        this.audio = audio;

        services.add(new SourceResolutionDelegate(this::getWidth, this::getHeight, this::getRate));
        services.add(new GameConfig());
        final Camera camera = services.create(Camera.class);
        camera.setView(0, 0, getWidth(), getHeight(), getHeight());

        services.add(context);
        services.add(new CameraTracker(services));
        services.add(new MapTileHelper(services));
        services.add((CheatsProvider) () -> false);
        services.add(new CheckpointHandler(services));
        device = services.add(DeviceControllerConfig.create(services, Medias.create(Constant.INPUT_FILE_DEFAULT)));
        info = new AppInfo(this::getFps, services);

        handler.addComponent(new ComponentRefreshable());
        handler.addComponent(new ComponentDisplayable());
        handler.addListener(factory);

        load(Part2.class, config, time, audio, alternative);

        setSystemCursorVisible(false);
        Util.setFilter(this, context, Util.getResolution(context, MIN_HEIGHT, MAX_WIDTH, MARGIN_WIDTH), 2);
    }

    /**
     * Spawn explode effect.
     * 
     * @param delay The next explode delay.
     */
    private void spawnExplode(int delay)
    {
        if (tickExplode.elapsedTime(getRate(), delay))
        {
            tickExplode.restart();

            final int width = citadel.getTileWidth();
            final int height = citadel.getTileHeight();
            final Media media = Medias.create(Folder.EXTRO,
                                              PART1_FOLDER,
                                              UtilRandom.getRandomBoolean() ? EXPLODE_LITTLE : EXPLODE_BIG);
            final double x = UtilRandom.getRandomInteger(width);
            final double y = UtilRandom.getRandomInteger((int) (height * EXPLODE_Y_SCALE)) + height + bandHeight;
            final int citadelOffsetY = citadel.getHeight() / 3;
            spawner.spawn(media, citadel.getX() + x, y - citadel.getY() + citadelOffsetY - EXPLODE_Y_OFFSET);
        }
    }

    @Override
    public void load()
    {
        backcolor.load();
        backcolor.prepare();
        backcolor.setLocation(0, bandHeight);

        clouds.load();
        clouds.prepare();
        clouds.setLocation(0, bandHeight);

        citadel.load();
        citadel.prepare();
        citadel.setFrame(1);
        citadel.setLocation(CITADEL_X, bandHeight + citadelY);

        valdyn.load();
        valdyn.prepare();
        valdyn.play(valdynAnim);

        tickExplode.start();
    }

    /**
     * Update fade in effect.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFadeIn(double extrp)
    {
        alpha -= alphaSpeed * extrp;

        if (getAlpha() < 0)
        {
            alpha = 0.0;
            updaterFade = this::updateFadeOutInit;
            rendererFade = RenderableVoid.getInstance();
        }
    }

    /**
     * Update fade out time.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFadeOutInit(double extrp)
    {
        if (time.isAfter(TIME_FADE_OUT_MS))
        {
            updaterFade = this::updateFadeOut;
            rendererFade = this::renderFade;
        }
    }

    /**
     * Update fade out effect.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFadeOut(double extrp)
    {
        alpha += alphaSpeed * extrp;

        if (getAlpha() > 255)
        {
            alpha = 255.0;
            end();
        }
    }

    /**
     * Update citadel fall time.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateCitadelInit(double extrp)
    {
        if (time.isAfter(TIME_CITADEL_DESTROYED_MS))
        {
            updaterCitadel = this::updateCitadelDestroyed;
        }
    }

    /**
     * Update citadel destroyed routine.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateCitadelDestroyed(double extrp)
    {
        citadel.setFrame(2);

        if (time.isAfter(TIME_CITADEL_FALL_MS))
        {
            updaterCitadel = this::updateCitadelFall;
        }
    }

    /**
     * Update citadel fall routine.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateCitadelFall(double extrp)
    {
        citadel.setFrame(2);
        citadelY = UtilMath.clamp(citadelY + citadelYacc * extrp, 0.0, getHeight() + citadel.getHeight());
        citadelYacc = UtilMath.clamp(citadelYacc + CITADEL_FALL_SPEED * extrp, 0.0, CITADEL_ACC_MAX);
        citadel.setLocation(CITADEL_X, bandHeight + citadelY);
    }

    /**
     * Update valdyn fly and scale.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateValdyn(double extrp)
    {
        valdyn.update(extrp);
        valdyn.setLocation(valdynX, valdynY);

        valdynZ += valdynZacc * extrp;
        valdynZacc = UtilMath.clamp(valdynZacc + VALDYN_Z_SPEED * extrp, VALDYN_Z_ACC_MIN, VALDYN_Z_ACC_MAX);

        final double scale = UtilMath.clamp(1000 / valdynZ, VALDYN_SCALE_MIN, VALDYN_SCALE_MAX);
        valdyn.stretch(scale, scale);

        valdynX += (VALDYN_MOVE_X - scale / VALDYN_MOVE_X_SCALE_DIVISOR) * extrp;
        valdynY += VALDYN_MOVE_Y * extrp;

        if (valdynX > VALDYN_X_MAX)
        {
            valdynX = VALDYN_X_MAX;
            updaterValdyn = UpdatableVoid.getInstance();
            rendererValdyn = RenderableVoid.getInstance();
        }
    }

    /**
     * Update explode few routine.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateExplodeFew(double extrp)
    {
        tickExplode.update(extrp);
        spawnExplode(SPAWN_EXPLODE_DELAY_MS);
        spawnExplode(SPAWN_EXPLODE_DELAY_MS);
        spawnExplode(SPAWN_EXPLODE_DELAY_MS);
        spawnExplode(SPAWN_EXPLODE_DELAY_MS);

        if (time.isAfter(TIME_EXPLODE_LOT_MS))
        {
            updaterExplode = this::updateExplodeLot;
        }
    }

    /**
     * Update explode lot routine.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateExplodeLot(double extrp)
    {
        tickExplode.update(extrp);
        spawnExplode(SPAWN_EXPLODE_FAST_DELAY_MS);

        if (time.isAfter(TIME_CITADEL_FALL_MS))
        {
            updaterExplode = this::updateExplodeMedium;
        }
    }

    /**
     * Update explode medium routine.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateExplodeMedium(double extrp)
    {
        tickExplode.update(extrp);
        spawnExplode(SPAWN_EXPLODE_MEDIUM_DELAY_MS);
        spawnExplode(SPAWN_EXPLODE_MEDIUM_DELAY_MS);
        spawnExplode(SPAWN_EXPLODE_MEDIUM_DELAY_MS);
        spawnExplode(SPAWN_EXPLODE_MEDIUM_DELAY_MS);
        spawnExplode(SPAWN_EXPLODE_MEDIUM_DELAY_MS);
        spawnExplode(SPAWN_EXPLODE_MEDIUM_DELAY_MS);

        if (time.isAfter(TIME_EXPLODE_END_MS))
        {
            updaterExplode = UpdatableVoid.getInstance();
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
     * Draw fade effect.
     * 
     * @param g The graphic output.
     */
    private void renderFade(Graphic g)
    {
        final int a = getAlpha();
        if (a > 0)
        {
            g.setColor(Constant.ALPHAS_BLACK[a]);
            g.drawRect(0, 0, getWidth(), getHeight(), true);
            g.setColor(ColorRgba.BLACK);
        }
    }

    /**
     * Draw band effect.
     * 
     * @param g The graphic output.
     */
    private void drawBand(Graphic g)
    {
        g.clear(0, 0, getWidth(), bandHeight);
        g.clear(0, getHeight() - bandHeight, getWidth(), bandHeight);
    }

    @Override
    public void update(double extrp)
    {
        time.update(extrp);
        handler.update(extrp);

        updaterFade.update(extrp);
        updaterCitadel.update(extrp);
        updaterValdyn.update(extrp);
        updaterExplode.update(extrp);
        info.update(extrp);

        if (device.isFiredOnce(DeviceMapping.FORCE_EXIT))
        {
            end(null);
        }
    }

    @Override
    public void render(Graphic g)
    {
        backcolor.render(g);
        clouds.render(g);
        citadel.render(g);
        handler.render(g);
        rendererValdyn.render(g);
        rendererFade.render(g);

        drawBand(g);
        info.render(g);
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        super.onTerminated(hasNextSequence);

        if (!hasNextSequence)
        {
            audio.stop();
            Engine.terminate();
        }
    }
}
