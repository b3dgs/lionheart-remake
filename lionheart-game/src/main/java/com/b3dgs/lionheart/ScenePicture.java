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

import java.util.Map.Entry;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.UtilReflection;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionengine.graphic.drawable.SpriteFont;
import com.b3dgs.lionengine.graphic.engine.Sequence;
import com.b3dgs.lionengine.helper.InputControllerConfig;
import com.b3dgs.lionengine.io.InputDeviceControl;
import com.b3dgs.lionengine.io.InputDeviceDirectional;
import com.b3dgs.lionheart.constant.Folder;

/**
 * Loading scene with picture.
 */
public final class ScenePicture extends Sequence
{
    private static final int PIC_Y = 17;
    private static final int TEXT_Y = 186;
    private static final int PUSH_Y = 225;
    private static final String PUSH_BUTTON_MESSAGE = "Push button";
    private static final int PUSH_BUTTON_TICK = 30;

    private final Tick tick = new Tick();
    private final Media stage;
    private final Sprite picture;
    private final SpriteFont font;
    private final InputDeviceControl input;

    private int fadePic;
    private int fadeText;
    private int speed = 8;
    private boolean showPush;

    /**
     * Constructor.
     * 
     * @param context The context reference (must not be <code>null</code>).
     * @param stage The associated stage.
     * @throws LionEngineException If invalid argument.
     */
    public ScenePicture(Context context, Media stage)
    {
        super(context, Constant.MENU_RESOLUTION);

        try
        {
            final InputControllerConfig config = InputControllerConfig.imports(new Services(),
                                                                               new Configurer(Medias.create(Folder.PLAYERS,
                                                                                                            "default",
                                                                                                            "Valdyn.xml")));
            input = UtilReflection.createReduce(config.getControl(), getInputDevice(InputDeviceDirectional.class));

            for (final Entry<Integer, Integer> entry : config.getCodes().entrySet())
            {
                input.setFireButton(entry.getKey(), entry.getValue());
            }
        }
        catch (final NoSuchMethodException exception)
        {
            throw new LionEngineException(exception);
        }

        final StageConfig config = StageConfig.imports(new Configurer(stage));

        this.stage = stage;
        picture = Drawable.loadSprite(config.getPic().get());
        font = Drawable.loadSpriteFont(Medias.create(Folder.SPRITES, "font.png"),
                                       Medias.create(Folder.SPRITES, "fontdata.xml"),
                                       12,
                                       12);
        font.setText(config.getText().get());
    }

    @Override
    public void load()
    {
        picture.load();
        picture.prepare();
        picture.setOrigin(Origin.CENTER_TOP);
        picture.setLocation(getWidth() / 2 - 1, PIC_Y);
        picture.setFade(0, -255);

        font.load();
        font.prepare();
        font.setOrigin(Origin.TOP_LEFT);
        font.setAlign(Align.CENTER);
        font.setLocation(getWidth() / 2 - 12, TEXT_Y);
        font.setFade(0, -255);

        fadePic = 0;
        fadeText = 0;

        tick.stop();
    }

    @Override
    public void update(double extrp)
    {
        updatePicture();
        updateText();
        updatePushButton(extrp);
    }

    /**
     * Update picture.
     */
    private void updatePicture()
    {
        if (fadeText == 0)
        {
            final int oldPic = fadePic;
            fadePic = UtilMath.clamp(fadePic + speed, 0, 255);
            if (oldPic != fadePic)
            {
                picture.setFade(fadePic, -255);
            }
            if (speed < 0 && fadePic == 0)
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
        if (fadePic == 255)
        {
            final int oldText = fadeText;
            fadeText = UtilMath.clamp(fadeText + speed, 0, 255);
            if (oldText != fadeText)
            {
                font.setFade(fadeText, -255);
            }
            if (fadeText == 255)
            {
                if (!tick.isStarted())
                {
                    load(Scene.class, stage);
                    tick.start();
                }
                else if (input.isFireButtonOnce(Constant.FIRE1))
                {
                    speed = -speed;
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
        if (tick.elapsed(PUSH_BUTTON_TICK))
        {
            showPush = !showPush;
            tick.restart();
        }
    }

    @Override
    public void render(Graphic g)
    {
        g.clear(0, 0, getWidth(), getHeight());
        picture.render(g);
        font.render(g);

        if (showPush)
        {
            font.draw(g, getWidth() / 2, PUSH_Y, Align.CENTER, PUSH_BUTTON_MESSAGE);
        }
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        picture.dispose();
        font.dispose();
    }
}
