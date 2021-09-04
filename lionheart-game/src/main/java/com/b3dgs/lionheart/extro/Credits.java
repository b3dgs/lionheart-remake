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

import java.util.ArrayList;
import java.util.List;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UpdatableVoid;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.RenderableVoid;
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.lionengine.graphic.TextStyle;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionengine.graphic.engine.Sequence;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionDelegate;
import com.b3dgs.lionengine.helper.DeviceControllerConfig;
import com.b3dgs.lionengine.io.DeviceController;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.DeviceMapping;
import com.b3dgs.lionheart.Music;
import com.b3dgs.lionheart.Settings;
import com.b3dgs.lionheart.Time;
import com.b3dgs.lionheart.Util;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.menu.Menu;

/**
 * Credits implementation.
 */
public class Credits extends Sequence
{
    private static final ColorRgba COLOR = new ColorRgba(238, 238, 238);
    private static final double SCROLL_SPEED = 0.2;
    private static final int FADE_SPEED = 8;

    private static final String PART4_FOLDER = "part4";
    private static final String PART5_FOLDER = "part5";
    private static final String FILENAME_IMAGE = "credits.png";
    private static final String FILENAME_TEXT = "credits.txt";

    private static final char TEXT_CENTER = 'C';
    private static final int TEXT_SIZE_EMPTY = 12;
    private static final int TEXT_SIZE_SMALL = 11;
    private static final int TEXT_SIZE_SEPARATOR = 14;
    private static final int TEXT_SIZE_MEDIUM = 24;
    private static final int TEXT_BEGIN_INDEX = 4;
    private static final int TEXT_SMALL_OFFSET_WIDTH = 32;
    private static final int TEXT_SCROLL_END_HEIGHT = 48;

    private static final int TIME_START_MS = 175000;

    /** Device controller reference. */
    final DeviceController device;
    /** Alpha speed. */
    int alphaSpeed = FADE_SPEED;

    private final List<Text> texts = new ArrayList<>();
    private final Audio audioAlternative = AudioFactory.loadAudio(Music.CREDITS);
    private final Sprite credits;
    private final Time time;
    private final Audio audio;
    private final boolean alternative;
    private final int count;
    private final Text lastText;
    private final DeviceController deviceCursor;

    private Updatable updater = this::updateFadeIn;
    private Renderable rendererText = RenderableVoid.getInstance();
    private Renderable rendererFade = this::renderFade;
    private int textFirstToRender;

    private double alpha;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     * @param time The time reference.
     * @param audio The audio reference.
     * @param alternative The alternative end.
     */
    public Credits(Context context, Time time, Audio audio, Boolean alternative)
    {
        super(context, Util.getResolution(Constant.RESOLUTION, context));

        this.time = time;
        this.audio = audio;
        this.alternative = Boolean.TRUE.equals(alternative);
        audioAlternative.setVolume(Settings.getInstance().getVolumeMusic());

        if (this.alternative)
        {
            credits = Drawable.loadSprite(Medias.create(Folder.EXTRO, PART5_FOLDER, FILENAME_IMAGE));
        }
        else
        {
            alpha = 255;
            credits = Drawable.loadSprite(Medias.create(Folder.EXTRO, PART4_FOLDER, FILENAME_IMAGE));
        }

        final Services services = new Services();
        services.add(context);
        services.add(new SourceResolutionDelegate(this::getWidth, this::getHeight, this::getRate));
        device = services.add(DeviceControllerConfig.create(services,
                                                            Medias.create(Settings.getInstance().getInput())));
        device.setVisible(false);

        final Media mediaCursor = Medias.create(Constant.INPUT_FILE_CUSTOR);
        deviceCursor = DeviceControllerConfig.create(services, mediaCursor);

        loadTextLines();
        count = texts.size();
        lastText = texts.get(count - 1);

        setSystemCursorVisible(false);
    }

    @Override
    public void load()
    {
        credits.load();
        credits.prepare();
        credits.setOrigin(Origin.MIDDLE);
        credits.setLocation(getWidth() / 2, getHeight() / 2);
    }

    private void loadTextLines()
    {
        int y = getHeight();
        final List<String> lines = Util.readLines(Medias.create(Folder.TEXT,
                                                                Settings.getInstance().getLang(),
                                                                Folder.EXTRO,
                                                                FILENAME_TEXT));
        for (int i = 0; i < lines.size(); i++)
        {
            final String line = lines.get(i);
            if (!line.isEmpty())
            {
                y = loadTextLine(line, y);
            }
            else
            {
                y += TEXT_SIZE_EMPTY;
            }
        }
    }

    private int loadTextLine(String line, int oldY)
    {
        int y = oldY;
        final int size = Integer.parseInt(line.substring(1, 3));
        final Text text;

        if (line.charAt(0) == TEXT_CENTER)
        {
            text = Graphics.createText(com.b3dgs.lionengine.Constant.FONT_SERIF, size, TextStyle.NORMAL);
            text.setAlign(Align.CENTER);
            text.setLocation(getWidth() / 2, y);
            y += size;
        }
        else
        {
            if (size == TEXT_SIZE_SMALL)
            {
                text = Graphics.createText(com.b3dgs.lionengine.Constant.FONT_SERIF, size, TextStyle.BOLD);
            }
            else
            {
                text = Graphics.createText(com.b3dgs.lionengine.Constant.FONT_SERIF, size, TextStyle.NORMAL);
            }

            y += size;
            text.setAlign(Align.LEFT);

            if (size == TEXT_SIZE_SEPARATOR || size == TEXT_SIZE_MEDIUM)
            {
                text.setLocation(getWidth() / 2 - credits.getWidth() / 2 + 2, y);
                y += TEXT_SIZE_SMALL;
            }
            else if (size == TEXT_SIZE_SMALL)
            {
                y += 2;
                text.setLocation(getWidth() / 2 - credits.getWidth() / 2 + TEXT_SMALL_OFFSET_WIDTH, y);
            }
            else
            {
                text.setLocation(getWidth() / 2 - credits.getWidth() / 2, y);
            }
        }
        text.setText(line.substring(TEXT_BEGIN_INDEX));
        text.setColor(COLOR);
        texts.add(text);

        return y;
    }

    /**
     * Update fade in effect.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFadeIn(double extrp)
    {
        alpha += alphaSpeed * extrp;

        if (alpha > 255)
        {
            alpha = 255;
            updater = this::updateStart;
            rendererFade = RenderableVoid.getInstance();
        }
    }

    /**
     * Update start credit action.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateStart(double extrp)
    {
        if (!alternative || time.isAfter(TIME_START_MS))
        {
            audio.stop();
            audioAlternative.play();
            updater = this::updateScroll;
            rendererText = this::renderText;
        }
    }

    /**
     * Update scroll text until end.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateScroll(double extrp)
    {
        if (lastText.getLocationY() > getHeight() - TEXT_SCROLL_END_HEIGHT)
        {
            for (int i = 0; i < count; i++)
            {
                final Text text = texts.get(i);
                text.setLocation(text.getLocationX(), text.getLocationY() - SCROLL_SPEED);
            }
        }
        else if (device.isFired(DeviceMapping.CTRL_RIGHT) || deviceCursor.isFiredOnce(DeviceMapping.LEFT))
        {
            updater = this::updateFadeOut;
            rendererFade = this::renderFade;
        }
    }

    /**
     * Update fade in effect.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFadeOut(double extrp)
    {
        alpha -= alphaSpeed * extrp;

        if (alpha > 0)
        {
            alpha = 0;
            end(Menu.class);
            updater = UpdatableVoid.getInstance();
        }
    }

    /**
     * Render fade effect.
     * 
     * @param g The graphic output.
     */
    private void renderFade(Graphic g)
    {
        g.setColor(Constant.ALPHAS_BLACK[255 - (int) Math.floor(alpha)]);
        g.drawRect(0, 0, getWidth(), getHeight(), true);
    }

    /**
     * Render text routine.
     * 
     * @param g The graphic output.
     */
    private void renderText(Graphic g)
    {
        for (int i = textFirstToRender; i < count; i++)
        {
            final Text text = texts.get(i);
            final double y = text.getLocationY();
            if (y < -text.getHeight())
            {
                textFirstToRender = i;
            }
            else if (y < getHeight())
            {
                text.render(g);
            }
            else
            {
                break;
            }
        }
    }

    @Override
    public void update(double extrp)
    {
        device.update(extrp);
        deviceCursor.update(extrp);
        time.update(extrp);
        updater.update(extrp);

        if (device.isFiredOnce(DeviceMapping.FORCE_EXIT))
        {
            end(null);
        }
    }

    @Override
    public void render(Graphic g)
    {
        g.clear(0, 0, getWidth(), getHeight());

        credits.render(g);
        rendererText.render(g);
        rendererFade.render(g);
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        super.onTerminated(hasNextSequence);

        audio.stop();
        audioAlternative.stop();
    }
}
