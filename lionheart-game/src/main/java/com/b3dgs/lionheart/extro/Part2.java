/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Updatable;
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
import com.b3dgs.lionengine.game.feature.collidable.ComponentCollision;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.RenderableVoid;
import com.b3dgs.lionengine.graphic.engine.Sequence;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionDelegate;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionengine.helper.DeviceControllerConfig;
import com.b3dgs.lionengine.helper.MapTileHelper;
import com.b3dgs.lionengine.io.DeviceController;
import com.b3dgs.lionheart.AppInfo;
import com.b3dgs.lionheart.CheatsProvider;
import com.b3dgs.lionheart.CheckpointHandler;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.DeviceMapping;
import com.b3dgs.lionheart.GameConfig;
import com.b3dgs.lionheart.LoadNextStage;
import com.b3dgs.lionheart.MapTileWater;
import com.b3dgs.lionheart.Time;
import com.b3dgs.lionheart.Util;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.object.feature.Trackable;

/**
 * Extro part 2 implementation.
 */
// CHECKSTYLE IGNORE LINE: FanOutComplexity|DataAbstractionCoupling
public class Part2 extends Sequence
{
    private static final int FADE_SPEED = 5;
    private static final int X = 24;
    private static final int Y = 110;
    private static final double BACKGROUND_SPEED = 1.2;

    private static final String PART2_FOLDER = "part2";
    private static final String FOLDER_DRAGONFLY = "dragonfly";
    private static final String DRAGON_EXTRO = "DragonExtro.xml";
    private static final String VALDYN = "Valdyn.xml";

    private static final int TIME_FADE_IN_MS = 23200;
    private static final int TIME_FADE_OUT_MS = 33100;

    /** Device controller reference. */
    final DeviceController device;
    /** Alpha speed. */
    int alphaSpeed = FADE_SPEED;

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
    private final AppInfo info;
    private final Time time;
    private final Audio audio;
    private final DragonEnd background;

    private Updatable updaterFade = this::updateFadeInInit;

    private Renderable rendererFade = this::renderFade;

    private double alpha = 255.0;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     * @param time The time reference.
     * @param audio The audio reference.
     * @param alternative The alternative end.
     */
    public Part2(Context context, Time time, Audio audio, Boolean alternative)
    {
        super(context, Util.getResolution(Constant.RESOLUTION, context), Util.getLoop());

        this.time = time;
        this.audio = audio;

        final Camera camera = services.create(Camera.class);
        camera.setView(0, 0, getWidth(), getHeight(), getHeight());

        services.add(context);
        services.add(new GameConfig());
        services.add(new CameraTracker(services));
        services.add(new MapTileHelper(services));
        services.add((CheatsProvider) () -> false);
        services.add(new CheckpointHandler(services));
        services.add(new MapTileWater(services));
        services.add((LoadNextStage) (next, delayMs, spawn) ->
        {
            // Mock
        });
        device = services.add(DeviceControllerConfig.create(services, Medias.create(Constant.INPUT_FILE_DEFAULT)));

        handler.addComponent(new ComponentRefreshable());
        handler.addComponent(new ComponentDisplayable());
        handler.addComponent(new ComponentCollision());
        handler.addListener(factory);

        final SourceResolutionProvider source = services.add(new SourceResolutionDelegate(this::getWidth,
                                                                                          this::getHeight,
                                                                                          this::getRate));
        background = new DragonEnd(source);
        info = new AppInfo(this::getFps, services);

        load(Part3.class, time, audio, alternative);

        setSystemCursorVisible(false);
        Util.setFilter(this);
    }

    /**
     * Update fade in time.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFadeInInit(double extrp)
    {
        if (time.isAfter(TIME_FADE_IN_MS))
        {
            updaterFade = this::updateFadeIn;
            rendererFade = this::renderFade;
        }
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
     * Get alpha value.
     * 
     * @return The alpha value.
     */
    private int getAlpha()
    {
        return (int) Math.floor(alpha);
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
            g.drawRect(0, 0, getWidth(), getHeight(), true);
            g.setColor(ColorRgba.BLACK);
        }
    }

    @Override
    public void load()
    {
        services.add(spawner.spawn(Medias.create(Folder.EXTRO, PART2_FOLDER, VALDYN), getWidth() / 2 + X, Y)
                            .getFeature(Trackable.class));
        spawner.spawn(Medias.create(Folder.ENTITY, FOLDER_DRAGONFLY, DRAGON_EXTRO), getWidth() / 2 + X, Y);
    }

    @Override
    public void update(double extrp)
    {
        time.update(extrp);
        background.update(extrp, BACKGROUND_SPEED * extrp, 0, 0);
        handler.update(extrp);
        updaterFade.update(extrp);
        info.update(extrp);

        if (device.isFiredOnce(DeviceMapping.FORCE_EXIT))
        {
            end(null);
        }
    }

    @Override
    public void render(Graphic g)
    {
        background.render(g);
        handler.render(g);
        rendererFade.render(g);
        info.render(g);
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        if (!hasNextSequence)
        {
            audio.stop();
            Engine.terminate();
        }
    }
}
