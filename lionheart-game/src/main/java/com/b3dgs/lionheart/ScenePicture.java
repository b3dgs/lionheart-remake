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
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionengine.helper.DeviceControllerConfig;
import com.b3dgs.lionengine.io.DeviceController;
import com.b3dgs.lionheart.constant.Folder;

/**
 * Loading scene with picture.
 */
public final class ScenePicture extends Sequence
{
    /** Init fade speed. */
    static final int FADE_SPEED = 8;

    private static final int PIC_Y = 17;
    private static final int TEXT_Y = 186;
    private static final int PUSH_Y = 225;
    private static final int PUSH_BUTTON_DELAY_MS = 500;
    private static final int TEXT_HEIGHT_NORMAL = 36;
    private static final int TEXT_HEIGHT_MAX = 40;

    /** Default fade speed. */
    static int fadeSpeed = FADE_SPEED;

    private final String pushButton = Util.readLines(Medias.create(Folder.TEXT,
                                                                   Settings.getInstance().getLang(),
                                                                   "push.txt"))
                                          .get(0);
    private final SpriteFont font = Drawable.loadSpriteFont(Medias.create(Folder.SPRITE, "font.png"),
                                                            Medias.create(Folder.SPRITE, "fontdata.xml"),
                                                            12,
                                                            12);
    private final Tick tick = new Tick();
    private final SourceResolutionProvider source = new SourceResolutionDelegate(this::getWidth,
                                                                                 this::getHeight,
                                                                                 this::getRate);
    private final Image text;
    private final GameConfig config;
    private final Sprite picture;
    private final DeviceController device;
    private final DeviceController deviceCursor;
    private final AppInfo info;
    private final Boolean auto;

    private double fadePic = 255.0;
    private double fadeText = 255.0;
    private int speed = fadeSpeed;
    private int picYoffset;
    private boolean showPush;

    /**
     * Constructor.
     * 
     * @param context The context reference (must not be <code>null</code>).
     * @param config The config reference (must not be <code>null</code>).
     * @param pic The associated picture.
     * @param narrative The associated narrative text.
     * @throws LionEngineException If invalid argument.
     */
    public ScenePicture(Context context, GameConfig config, Media pic, String narrative)
    {
        this(context, config, pic, narrative, Boolean.FALSE);
    }

    /**
     * Constructor.
     * 
     * @param context The context reference (must not be <code>null</code>).
     * @param config The config reference (must not be <code>null</code>).
     * @param pic The associated picture.
     * @param narrative The associated narrative text.
     * @param auto <code>true</code> for auto skip, <code>false</code> for manual.
     * @throws LionEngineException If invalid argument.
     */
    public ScenePicture(Context context, GameConfig config, Media pic, String narrative, Boolean auto)
    {
        super(context, Util.getResolution(Constant.RESOLUTION, context), Util.getLoop(context.getConfig().getOutput()));

        this.config = config;
        this.auto = auto;

        final Services services = new Services();
        services.add(context);
        services.add(source);
        device = services.add(DeviceControllerConfig.create(services, Medias.create(Constant.INPUT_FILE_DEFAULT)));

        final Media mediaCursor = Medias.create(Constant.INPUT_FILE_CURSOR);
        deviceCursor = DeviceControllerConfig.create(services, mediaCursor);

        info = new AppInfo(this::getFps, services);

        picture = Drawable.loadSprite(pic);

        font.load();
        font.prepare();
        text = Drawable.loadImage(cacheText(narrative));
        text.setLocation(0, TEXT_Y + picYoffset);
        text.setOrigin(Origin.TOP_LEFT);

        setSystemCursorVisible(false);
        Util.setFilter(this, context, Util.getResolution(Constant.RESOLUTION, context), 2);
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
        final int textHeight = font.getTextHeight(str);
        picYoffset = TEXT_HEIGHT_NORMAL - textHeight;
        final ImageBuffer buffer = Graphics.createImageBuffer(getWidth(), textHeight + 8);
        buffer.prepare();

        final Graphic g = buffer.createGraphic();
        font.draw(g, getWidth() / 2 - 1, 0, Align.CENTER, str);
        g.dispose();

        return buffer;
    }

    /**
     * Update picture.
     * 
     * @param extrp The extrapolation value.
     */
    private void updatePicture(double extrp)
    {
        if (getFadeText() == 255)
        {
            fadePic = UtilMath.clamp(fadePic - speed * extrp, 0.0, 255.0);

            if (speed < 0 && getFadePic() == 255)
            {
                end();
            }
        }
    }

    /**
     * Update text.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateText(double extrp)
    {
        if (getFadePic() == 0)
        {
            fadeText = UtilMath.clamp(fadeText - speed * extrp, 0.0, 255.0);

            if (getFadeText() == 0)
            {
                updateFadedIn();
            }
        }
    }

    /**
     * Update on faded in.
     */
    private void updateFadedIn()
    {
        if (!tick.isStarted())
        {
            if (config.getInit().getStage().exists())
            {
                load(Scene.class, config);
            }
            tick.start();
        }
        else if (auto.booleanValue()
                 || device.isFiredOnce(DeviceMapping.ATTACK)
                 || deviceCursor.isFiredOnce(DeviceMapping.LEFT.getIndex()))
        {
            speed = -speed;
            showPush = false;
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
        if (speed > 0 && tick.elapsedTime(source.getRate(), PUSH_BUTTON_DELAY_MS))
        {
            showPush = !showPush;
            tick.restart();
        }
    }

    /**
     * Get fade text value.
     * 
     * @return The fade text value.
     */
    private int getFadeText()
    {
        return (int) Math.floor(fadeText);
    }

    /**
     * Get fade picture value.
     * 
     * @return The fade picture value.
     */
    private int getFadePic()
    {
        return (int) Math.floor(fadePic);
    }

    @Override
    public void load()
    {
        picture.load();
        picture.prepare();
        picture.setOrigin(Origin.CENTER_TOP);
        picture.setLocation(getWidth() / 2 - 1, PIC_Y + picYoffset);
    }

    @Override
    public void update(double extrp)
    {
        device.update(extrp);
        deviceCursor.update(extrp);

        updatePicture(extrp);
        updateText(extrp);
        updatePushButton(extrp);

        info.update(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        g.clear(0, 0, getWidth(), getHeight());

        picture.render(g);
        if (getFadePic() > 0)
        {
            g.setColor(Constant.ALPHAS_BLACK[getFadePic()]);
            g.drawRect((int) Math.round(picture.getX() - picture.getWidth() / 2),
                       (int) Math.round(picture.getY() + picYoffset),
                       picture.getWidth(),
                       picture.getHeight(),
                       true);
        }

        text.render(g);
        if (getFadeText() > 0)
        {
            g.setColor(Constant.ALPHAS_BLACK[getFadeText()]);
            g.drawRect(0,
                       (int) Math.round(text.getY() + picYoffset),
                       getWidth(),
                       TEXT_HEIGHT_MAX - picYoffset * 2,
                       true);
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
        super.onTerminated(hasNextSequence);

        picture.dispose();
        text.dispose();
        if (!hasNextSequence)
        {
            Engine.terminate();
        }
    }
}
