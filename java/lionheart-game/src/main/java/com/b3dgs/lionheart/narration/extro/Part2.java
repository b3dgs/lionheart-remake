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
package com.b3dgs.lionheart.narration.extro;

import java.util.Optional;
import java.util.function.Consumer;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.CameraTracker;
import com.b3dgs.lionengine.game.feature.ComponentDisplayable;
import com.b3dgs.lionengine.game.feature.ComponentRefreshable;
import com.b3dgs.lionengine.game.feature.ComponentUpdater;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Spawner;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.ComponentCollision;
import com.b3dgs.lionengine.geom.Coord;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionDelegate;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionengine.helper.MapTileHelper;
import com.b3dgs.lionheart.CheatsProvider;
import com.b3dgs.lionheart.CheckpointHandler;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.FadeSide;
import com.b3dgs.lionheart.GameConfig;
import com.b3dgs.lionheart.LoadNextStage;
import com.b3dgs.lionheart.MapTileWater;
import com.b3dgs.lionheart.Util;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.narration.NarrationFactory;
import com.b3dgs.lionheart.object.feature.Trackable;

/**
 * Extro part 2 implementation.
 */
// CHECKSTYLE IGNORE LINE: FanOutComplexity|DataAbstractionCoupling
public final class Part2
{
    private static final int FADE_SPEED = 5;
    private static final int X = 24;
    private static final int Y = 110;
    private static final double BACKGROUND_SPEED = 1.2;

    private static final String PART2_FOLDER = "part2";
    private static final String FOLDER_DRAGONFLY = "dragonfly";
    private static final String DRAGON_EXTRO = "DragonExtro.xml";
    private static final String VALDYN = "Valdyn.xml";

    private static final int TIME_FADE_IN_MS = 23_200;
    private static final int TIME_FADE_OUT_MS = 33_100;

    private final Services services = new Services();
    private final Camera camera = services.create(Camera.class);
    private final Factory factory = services.create(Factory.class);
    private final Handler handler = services.create(Handler.class);
    private final Spawner spawner = services.add((Spawner) (media, x, y) ->
    {
        final Featurable featurable = factory.create(media);
        featurable.getFeature(Transformable.class).teleport(x, y);
        handler.add(featurable);
        return featurable;
    });
    private final Context context;
    private final DragonEnd background;
    private final Resolution resolution;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public Part2(Context context)
    {
        super();

        this.context = context;
        resolution = Util.getResolution(Constant.RESOLUTION, context);

        services.add(context);
        services.add(new GameConfig());
        services.add(new CameraTracker(services));
        services.add(new MapTileHelper(services));
        services.add((CheatsProvider) () -> false);
        services.add(new CheckpointHandler(services));
        services.add(new MapTileWater(services));
        services.add(new LoadNextStage()
        {
            @Override
            public void reloadStage()
            {
                // Mock
            }

            @Override
            public void loadNextStage(String next, int delayMs, Optional<Coord> spawn)
            {
                // Mock
            }
        });

        handler.addComponent(new ComponentRefreshable());
        handler.addComponent(new ComponentDisplayable());
        handler.addComponent((ComponentUpdater) new ComponentCollision(camera));
        handler.addListener(factory);

        final SourceResolutionProvider source = services.add(new SourceResolutionDelegate(resolution));
        background = new DragonEnd(source);
    }

    /**
     * Init resolution.
     * 
     * @param context The context reference.
     * @param source The source resolution.
     */
    public void initResolution(Context context, Consumer<Resolution> source)
    {
        source.accept(resolution);
    }

    /**
     * Called when the resolution changed.
     * 
     * @param width The new screen width.
     * @param height The new screen height.
     */
    public void onResolutionChanged(int width, int height)
    {
        camera.setView(0, 0, width, height, height);
    }

    /**
     * Load part.
     * 
     * @param narration The narration factory.
     * @param source The source resolution.
     */
    public void load(NarrationFactory narration, Consumer<Resolution> source)
    {
        services.add(spawner.spawn(Medias.create(Folder.EXTRO, PART2_FOLDER, VALDYN),
                                   resolution.getWidth() / 2.0 + X,
                                   Y)
                            .getFeature(Trackable.class));
        spawner.spawn(Medias.create(Folder.ENTITY, FOLDER_DRAGONFLY, DRAGON_EXTRO), resolution.getWidth() / 2.0 + X, Y);

        init(narration, source);
    }

    private void init(NarrationFactory n, Consumer<Resolution> source)
    {
        n.add(TIME_FADE_IN_MS, () -> initResolution(context, source));
        n.add(TIME_FADE_IN_MS, TIME_FADE_OUT_MS + 3000, this::update, this::render);
        n.add(TIME_FADE_IN_MS, FadeSide.IN, FADE_SPEED);
        n.add(TIME_FADE_OUT_MS, FadeSide.OUT, FADE_SPEED);
    }

    private void update(double extrp)
    {
        background.update(extrp, BACKGROUND_SPEED * extrp, 0, 0);
        handler.update(extrp);
    }

    private void render(Graphic g)
    {
        background.render(g);
        handler.render(g);
    }
}
