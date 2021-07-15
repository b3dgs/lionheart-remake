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
package com.b3dgs.lionheart;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Image;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionengine.graphic.drawable.SpriteFont;
import com.b3dgs.lionengine.graphic.engine.Sequence;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionDelegate;
import com.b3dgs.lionengine.helper.DeviceControllerConfig;
import com.b3dgs.lionengine.io.DeviceController;
import com.b3dgs.lionheart.constant.Folder;

/**
 * Loading scene with picture.
 */
public final class ScenePicture extends Sequence
{
    private static final int PIC_Y = 17;
    private static final int TEXT_Y = 186;
    private static final int PUSH_Y = 225;
    private static final int PUSH_BUTTON_TICK = 30;

    private final String pushButton = Util.readLines(Medias.create(Folder.TEXT,
                                                                   Settings.getInstance().getLang(),
                                                                   "push.txt"))
                                          .get(0);
    private final SpriteFont font = Drawable.loadSpriteFont(Medias.create(Folder.SPRITE, "font.png"),
                                                            Medias.create(Folder.SPRITE, "fontdata.xml"),
                                                            12,
                                                            12);
    private final Image text;

    private final Tick tick = new Tick();
    private final Media stage;
    private final InitConfig init;
    private final Sprite picture;
    private final DeviceController device;
    private final AppInfo info;

    private int fadePic = 255;
    private int fadeText = 255;
    private int speed = 6;
    private boolean showPush;

    /**
     * Constructor.
     * 
     * @param context The context reference (must not be <code>null</code>).
     * @param stage The stage media.
     * @param init The init config.
     * @param pic The associated picture.
     * @param narrative The associated narrative text.
     * @throws LionEngineException If invalid argument.
     */
    public ScenePicture(Context context, Media stage, InitConfig init, Media pic, String narrative)
    {
        super(context, Util.getResolution(Constant.RESOLUTION, context));

        this.stage = stage;
        this.init = init;

        if (!stage.exists())
        {
            speed = 255;
        }

        final Services services = new Services();
        services.add(context);
        services.add(new SourceResolutionDelegate(this::getWidth, this::getHeight, this::getRate));
        device = services.add(DeviceControllerConfig.create(services, Medias.create(Constant.INPUT_FILE_CUSTOM)));
        info = new AppInfo(this::getFps, services);

        picture = Drawable.loadSprite(pic);

        font.load();
        font.prepare();
        text = Drawable.loadImage(cacheText(narrative));
        text.setLocation(0, TEXT_Y);
        text.setOrigin(Origin.TOP_LEFT);

        setSystemCursorVisible(false);
    }

    /**
     * Cache narrative text.
     * 
     * @param text The text string.
     * @return The cached text.
     */
    private ImageBuffer cacheText(String text)
    {
        final String str = Util.toFontText(Medias.create(Folder.TEXT, Settings.getInstance().getLang(), text));
        final ImageBuffer buffer = Graphics.createImageBuffer(getWidth(), font.getTextHeight(str) + 8);
        buffer.prepare();

        final Graphic g = buffer.createGraphic();
        font.draw(g, getWidth() / 2 - 1, 0, Align.CENTER, str);
        g.dispose();

        return buffer;
    }

    /**
     * Update picture.
     */
    private void updatePicture()
    {
        if (fadeText == 255)
        {
            fadePic = UtilMath.clamp(fadePic - speed, 0, 255);
            if (speed < 0 && fadePic == 255)
            {
                end();
            }
        }
    }

    /**
     * Update text.
     */
    private void updateText()
    {
        if (fadePic == 0)
        {
            fadeText = UtilMath.clamp(fadeText - speed, 0, 255);
            if (fadeText == 0)
            {
                if (!tick.isStarted())
                {
                    if (stage.exists())
                    {
                        load(Scene.class, stage, init);
                    }
                    tick.start();
                }
                else if (device.isFiredOnce(DeviceMapping.CTRL_RIGHT))
                {
                    speed = -speed;
                    showPush = false;
                }
            }
        }
    }

    /**
     * Update push button.
     * 
     * @param extrp The extrapolation value.
     */
    private void updatePushButton(double extrp)
    {
        tick.update(extrp);
        if (speed > 0 && tick.elapsed(PUSH_BUTTON_TICK))
        {
            showPush = !showPush;
            tick.restart();
        }
    }

    @Override
    public void load()
    {
        picture.load();
        picture.prepare();
        picture.setOrigin(Origin.CENTER_TOP);
        picture.setLocation(getWidth() / 2 - 1, PIC_Y);
    }

    @Override
    public void update(double extrp)
    {
        updatePicture();
        updateText();
        updatePushButton(extrp);

        info.update(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        g.clear(0, 0, getWidth(), getHeight());

        picture.render(g);
        if (fadePic > 0)
        {
            g.setColor(Constant.ALPHAS_BLACK[fadePic]);
            g.drawRect((int) picture.getX() - picture.getWidth() / 2,
                       (int) picture.getY(),
                       picture.getWidth(),
                       picture.getHeight(),
                       true);
        }

        text.render(g);
        if (fadeText > 0)
        {
            g.setColor(Constant.ALPHAS_BLACK[fadeText]);
            g.drawRect(0, (int) text.getY(), getWidth(), 40, true);
        }

        g.setColor(ColorRgba.WHITE);
        if (showPush)
        {
            font.draw(g, getWidth() / 2, PUSH_Y, Align.CENTER, pushButton);
        }

        info.render(g);
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        picture.dispose();
        text.dispose();
        if (!hasNextSequence)
        {
            Engine.terminate();
        }
    }
}
