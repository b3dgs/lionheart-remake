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

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
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
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;
import com.b3dgs.lionengine.graphic.engine.Sequence;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionDelegate;
import com.b3dgs.lionengine.helper.DeviceControllerConfig;
import com.b3dgs.lionengine.helper.MapTileHelper;
import com.b3dgs.lionheart.AppInfo;
import com.b3dgs.lionheart.CheckpointHandler;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.Time;
import com.b3dgs.lionheart.Util;
import com.b3dgs.lionheart.constant.Folder;

/**
 * Extro part 5 implementation.
 */
public final class Part5 extends Sequence
{
    private static final Animation OPEN = new Animation("open", 1, 8, 0.18, false, false);

    private final Sprite transform0a = get("transform0a.png");
    private final Sprite transform0b = get("transform0b.png");
    private final Sprite transform0c = get("transform0c.png");
    private final SpriteAnimated eyes = get("eyes.png", 2, 4);

    private static Sprite get(String file)
    {
        return Drawable.loadSprite(Medias.create(Folder.EXTRO, "part5", file));
    }

    private static SpriteAnimated get(String file, int horizontalFrames, int verticalFrames)
    {
        return Drawable.loadSpriteAnimated(Medias.create(Folder.EXTRO, "part5", file),
                                           horizontalFrames,
                                           verticalFrames);
    }

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

    private int alphaBack;
    private int alpha0b;
    private int flick0c;
    private int flicked0c;
    private boolean spawned;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     * @param time The time reference.
     * @param audio The audio reference.
     * @param alternative The alternative end.
     */
    public Part5(Context context, Time time, Audio audio, Boolean alternative)
    {
        super(context, Util.getResolution(Constant.RESOLUTION, context));

        this.time = time;
        this.audio = audio;

        services.add(new SourceResolutionDelegate(this::getWidth, this::getHeight, this::getRate));
        final Camera camera = services.create(Camera.class);
        camera.setView(0, 0, getWidth(), getHeight(), getHeight());

        services.add(context);
        services.add(new CameraTracker(services));
        services.add(new MapTileHelper(services));
        services.add(new CheckpointHandler(services));
        services.add(DeviceControllerConfig.create(services, Medias.create(Constant.INPUT_FILE_CUSTOM)));
        info = new AppInfo(this::getFps, services);

        handler.addComponent(new ComponentRefreshable());
        handler.addComponent(new ComponentDisplayable());
        handler.addListener(factory);

        load(Credits.class, time, audio, alternative);

        setSystemCursorVisible(false);
    }

    @Override
    public void load()
    {
        transform0a.load();
        transform0a.prepare();
        transform0a.setOrigin(Origin.CENTER_TOP);

        transform0b.load();
        transform0b.prepare();
        transform0b.setOrigin(Origin.CENTER_TOP);
        transform0b.setAlpha(alpha0b);

        transform0c.load();
        transform0c.prepare();
        transform0c.setOrigin(Origin.CENTER_TOP);

        eyes.load();
        eyes.prepare();
        eyes.setOrigin(Origin.CENTER_TOP);
    }

    @Override
    public void update(double extrp)
    {
        time.update(extrp);
        handler.update(extrp);

        if (time.isBefore(141200))
        {
            alphaBack += 3;
        }
        else if (time.isAfter(158400))
        {
            alphaBack -= 6;
        }
        alphaBack = UtilMath.clamp(alphaBack, 0, 255);

        final int x = getWidth() / 2 - 2;
        final int y = getHeight() / 2 - 54;
        if (time.isBetween(140300, 140500) && !spawned)
        {
            spawner.spawn(Medias.create(Folder.EXTRO, "part5", "Transform1.xml"), x, y);
            spawned = !spawned;
        }

        if (time.isBetween(140700, 142700))
        {
            alpha0b += 3;
            if (alpha0b < 256)
            {
                transform0b.setAlpha(UtilMath.clamp(alpha0b, 0, 255));
            }
        }

        if (time.isBetween(142700, 143590) && spawned)
        {
            spawner.spawn(Medias.create(Folder.EXTRO, "part5", "Transform2.xml"), x + 4, y);
            spawned = !spawned;
        }
        if (time.isBetween(143600, 145190) && !spawned)
        {
            spawner.spawn(Medias.create(Folder.EXTRO, "part5", "Transform3.xml"), x - 1, y);
            spawned = !spawned;
        }
        if (time.isBetween(145200, 146990) && spawned)
        {
            spawner.spawn(Medias.create(Folder.EXTRO, "part5", "Transform4.xml"), x - 2, y + 116);
            spawned = !spawned;
        }

        if (time.isBetween(147000, 147990) && !spawned)
        {
            spawner.spawn(Medias.create(Folder.EXTRO, "part5", "Transform1.xml"), x - 40, y + 32);
            spawned = !spawned;
        }
        if (time.isBetween(148000, 148990) && spawned)
        {
            spawner.spawn(Medias.create(Folder.EXTRO, "part5", "Transform1.xml"), x, y + 32);
            spawned = !spawned;
        }
        if (time.isBetween(149000, 150000) && !spawned)
        {
            spawner.spawn(Medias.create(Folder.EXTRO, "part5", "Transform1.xml"), x + 40, y + 32);
            spawned = !spawned;
        }

        if (time.isAfter(151000) && flicked0c < 6)
        {
            flick0c++;
            if (flick0c > 16)
            {
                flick0c -= 16;
                flicked0c++;
            }
            alpha0b = 255;
        }

        if (time.isBetween(152500, 153500) && spawned)
        {
            spawner.spawn(Medias.create(Folder.EXTRO, "part5", "Transform1.xml"), x, y);
            spawned = !spawned;
        }

        if (time.isBetween(153500, 155400))
        {
            alpha0b -= 3;
            if (alpha0b > -1)
            {
                transform0b.setAlpha(UtilMath.clamp(alpha0b, 0, 255));
            }
        }
        if (time.isAfter(155400) && eyes.getAnimState() == AnimState.STOPPED)
        {
            eyes.play(OPEN);
        }

        if (time.isAfter(159500))
        {
            end();
        }

        eyes.update(extrp);

        info.update(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        g.clear(0, 0, getWidth(), getHeight());

        transform0a.setLocation(getWidth() / 2, 24);
        transform0a.render(g);

        if (flick0c > 8 || flicked0c > 5)
        {
            transform0c.setLocation(getWidth() / 2, 24);
            transform0c.render(g);

            eyes.setLocation(getWidth() / 2, 57);
            eyes.render(g);
        }

        transform0b.setLocation(getWidth() / 2, 24);
        transform0b.render(g);

        handler.render(g);

        // Render fade in
        if (alphaBack < 255)
        {
            g.setColor(Constant.ALPHAS_BLACK[255 - alphaBack]);
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
