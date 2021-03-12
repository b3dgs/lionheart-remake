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
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.UtilMath;
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
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionengine.helper.MapTileHelper;
import com.b3dgs.lionheart.CheckpointHandler;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.intro.Intro;

/**
 * Extro part 5 implementation.
 */
public final class Part5
{
    private static final Animation OPEN = new Animation("open", 1, 8, 0.18, false, false);

    private final Sprite transform0a = get("transform0a.png");
    private final Sprite transform0b = get("transform0b.png");
    private final Sprite transform0c = get("transform0c.png");
    private final SpriteAnimated transform1 = get("transform1.png", 5, 2);
    private final SpriteAnimated transform2 = get("transform2.png", 4, 3);
    private final SpriteAnimated transform3 = get("transform3.png", 10, 2);
    private final SpriteAnimated transform4 = get("transform4.png", 4, 2);
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
    private final Spawner spawner = services.add(new Spawner()
    {
        @Override
        public Featurable spawn(Media media, double x, double y)
        {
            final Featurable featurable = factory.create(media);
            featurable.getFeature(Transformable.class).teleport(x, y);
            handler.add(featurable);
            return featurable;
        }
    });

    private int alphaBack;
    private int alpha0b;
    private int flick0c;
    private int flicked0c;
    private boolean spawned;

    /**
     * Constructor.
     */
    public Part5()
    {
        super();

        final SourceResolutionProvider source = services.add(new SourceResolutionProvider()
        {
            @Override
            public int getWidth()
            {
                return Constant.NATIVE_RESOLUTION.getWidth();
            }

            @Override
            public int getHeight()
            {
                return Constant.NATIVE_RESOLUTION.getHeight();
            }

            @Override
            public int getRate()
            {
                return Constant.NATIVE_RESOLUTION.getRate();
            }
        });
        final Camera camera = services.create(Camera.class);
        camera.setView(0, 0, source.getWidth(), source.getHeight(), source.getHeight());

        services.add(new CameraTracker(services));
        services.add(new MapTileHelper(services));
        services.add(new CheckpointHandler(services));

        handler.addComponent(new ComponentRefreshable());
        handler.addComponent(new ComponentDisplayable());
        handler.addListener(factory);
    }

    /**
     * Load part.
     */
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

        transform1.load();
        transform1.prepare();

        transform2.load();
        transform2.prepare();

        transform3.load();
        transform3.prepare();

        eyes.load();
        eyes.prepare();
        eyes.setOrigin(Origin.CENTER_TOP);
    }

    /**
     * Update part.
     * 
     * @param seek The current seek.
     * @param extrp The extrapolation value.
     */
    public void update(long seek, double extrp)
    {
        handler.update(extrp);

        if (seek > 135000 && seek < 138000)
        {
            alphaBack += 3;
        }
        else if (seek > 161000)
        {
            alphaBack -= 3;
        }
        alphaBack = UtilMath.clamp(alphaBack, 0, 255);

        if (seek > 139600 && seek < 139700 && !spawned)
        {
            spawner.spawn(Medias.create(Folder.EXTRO, "part5", "Transform1.xml"), 210, 64);
            spawned = !spawned;
        }

        if (seek > 140000 && seek < 145000)
        {
            alpha0b += 3;
            if (alpha0b < 256)
            {
                transform0b.setAlpha(UtilMath.clamp(alpha0b, 0, 255));
            }
        }

        if (seek > 146000 && seek < 146100 && spawned)
        {
            spawner.spawn(Medias.create(Folder.EXTRO, "part5", "Transform2.xml"), 216, 88);
            spawned = !spawned;
        }
        if (seek > 146500 && seek < 146600 && !spawned)
        {
            spawner.spawn(Medias.create(Folder.EXTRO, "part5", "Transform3.xml"), 210, 160);
            spawned = !spawned;
        }
        if (seek > 147200 && seek < 147300 && spawned)
        {
            spawner.spawn(Medias.create(Folder.EXTRO, "part5", "Transform4.xml"), 210, 160);
            spawned = !spawned;
        }

        if (seek > 150000 && flicked0c < 6)
        {
            flick0c++;
            if (flick0c > 16)
            {
                flick0c -= 16;
                flicked0c++;
            }
            alpha0b = 255;
        }
        if (seek > 154000 && seek < 155000)
        {
            alpha0b -= 3;
            if (alpha0b > -1)
            {
                transform0b.setAlpha(UtilMath.clamp(alpha0b, 0, 255));
            }
        }
        if (seek > 155000 && eyes.getAnimState() == AnimState.STOPPED)
        {
            eyes.play(OPEN);
        }

        eyes.update(extrp);
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

        transform0a.setLocation(width / 2, 24);
        transform0a.render(g);

        if (flick0c > 8 || flicked0c > 5)
        {
            transform0c.setLocation(width / 2, 24);
            transform0c.render(g);

            eyes.setLocation(width / 2, 57);
            eyes.render(g);
        }

        transform0b.setLocation(width / 2, 24);
        transform0b.render(g);

        handler.render(g);

        // Render fade in
        if (alphaBack < 255)
        {
            g.setColor(Intro.ALPHAS_BLACK[255 - alphaBack]);
            g.drawRect(0, 0, width, height, true);
        }
    }
}
