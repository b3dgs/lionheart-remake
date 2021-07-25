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
package com.b3dgs.lionheart.extro;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilMath;
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
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.engine.Sequence;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionDelegate;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionengine.helper.DeviceControllerConfig;
import com.b3dgs.lionengine.helper.MapTileHelper;
import com.b3dgs.lionengine.io.DeviceController;
import com.b3dgs.lionheart.AppInfo;
import com.b3dgs.lionheart.CheckpointHandler;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.DeviceMapping;
import com.b3dgs.lionheart.LoadNextStage;
import com.b3dgs.lionheart.MapTileWater;
import com.b3dgs.lionheart.Settings;
import com.b3dgs.lionheart.Time;
import com.b3dgs.lionheart.Util;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.object.feature.SwordShade;

/**
 * Extro part 2 implementation.
 */
public class Part2 extends Sequence
{
    /** Device controller reference. */
    final DeviceController device;
    /** Alpha speed. */
    int alphaSpeed = 3;

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
    private int alpha;

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
        super(context, Util.getResolution(Constant.RESOLUTION, context));

        this.time = time;
        this.audio = audio;

        final Camera camera = services.create(Camera.class);
        camera.setView(0, 0, getWidth(), getHeight(), getHeight());

        services.add(context);
        services.add(new CameraTracker(services));
        services.add(new MapTileHelper(services));
        services.add(new CheckpointHandler(services));
        services.add(new MapTileWater(services));
        services.add((LoadNextStage) (next, tickDelay, spawn) ->
        {
            // Mock
        });
        device = services.add(DeviceControllerConfig.create(services, Medias.create(Settings.getInstance().getInput())));

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
    }

    @Override
    public void load()
    {
        services.add(spawner.spawn(Medias.create(Folder.EXTRO, "part2", "Valdyn.xml"), getWidth() / 2 + 16, 100)
                            .getFeature(SwordShade.class));
        spawner.spawn(Medias.create(Folder.ENTITY, "dragonfly", "DragonExtro.xml"), getWidth() / 2 + 16, 100);
    }

    @Override
    public void update(double extrp)
    {
        time.update(extrp);
        background.update(extrp, 1.0, 0, 0);
        handler.update(extrp);

        if (time.isBefore(25000) && alpha < 255)
        {
            alpha = UtilMath.clamp(alpha + alphaSpeed, 0, 255);
        }
        else if (time.isAfter(32500) && alpha > 0)
        {
            alpha = UtilMath.clamp(alpha - alphaSpeed, 0, 255);
        }

        if (time.isAfter(34500))
        {
            end();
        }
        if (device.isFiredOnce(DeviceMapping.FORCE_EXIT))
        {
            end(null);
        }

        info.update(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        background.render(g);
        handler.render(g);

        if (alpha < 255)
        {
            g.setColor(Constant.ALPHAS_BLACK[255 - alpha]);
            g.drawRect(0, 0, getWidth(), getHeight(), true);
        }

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
