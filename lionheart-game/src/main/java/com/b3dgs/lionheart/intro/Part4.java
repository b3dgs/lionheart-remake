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
package com.b3dgs.lionheart.intro;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionengine.graphic.drawable.SpriteFont;
import com.b3dgs.lionengine.graphic.engine.Sequence;
import com.b3dgs.lionengine.helper.DeviceControllerConfig;
import com.b3dgs.lionengine.io.DeviceController;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.DeviceMapping;
import com.b3dgs.lionheart.Util;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.menu.Menu;

/**
 * Intro part 4 implementation.
 */
public final class Part4 extends Sequence
{
    /** Font. */
    private final SpriteFont font = Drawable.loadSpriteFont(Medias.create(Folder.SPRITES, "font.png"),
                                                            Medias.create(Folder.SPRITES, "fontdata.xml"),
                                                            12,
                                                            12);
    /** Pictures. */
    private final Sprite[] history = new Sprite[4];
    /** Input device reference. */
    private final DeviceController device;
    /** Audio. */
    private final Audio audio;
    /** Back alpha. */
    private double alphaBack;
    /** Alpha speed. */
    private double alphaSpeed = 3.0;
    /** Current seek. */
    private long seek;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     * @param audio The audio reference.
     */
    public Part4(Context context, Audio audio)
    {
        super(context, Util.getResolution(Constant.RESOLUTION, context));

        this.audio = audio;

        final Services services = new Services();
        services.add(context);
        device = DeviceControllerConfig.create(services, Medias.create("input.xml"));

        for (int i = 0; i < history.length; i++)
        {
            history[i] = Drawable.loadSprite(Medias.create("intro", "part4", "history" + i + ".png"));
            history[i].load();
        }

        load(Menu.class);
    }

    @Override
    public void load()
    {
        font.load();
    }

    @Override
    public void update(double extrp)
    {
        seek = audio.getTicks();

        // First Fade in
        if (seek > 113500 && seek < 197000)
        {
            alphaBack += alphaSpeed;
        }

        // First Fade out
        if (seek > 197000 && seek < 201000)
        {
            alphaBack += alphaSpeed;
        }
        alphaBack = UtilMath.clamp(alphaBack, 0.0, 255.0);

        if (alphaSpeed > 0 && (seek > 197000 || device.isFiredOnce(DeviceMapping.CTRL_RIGHT)))
        {
            alphaSpeed = -alphaSpeed * 2;
        }
        if (alphaBack == 0 && alphaSpeed < 0)
        {
            end();
        }
    }

    @Override
    public void render(Graphic g)
    {
        g.clear(0, 0, getWidth(), getHeight());

        // Render histories
        if (seek >= 113500)
        {
            history[0].setLocation(0, 0);
            history[0].render(g);
        }
        if (seek >= 130000)
        {
            history[1].setLocation(45, 20);
            history[1].render(g);
        }
        if (seek >= 154000)
        {
            history[2].setLocation(90, 40);
            history[2].render(g);
        }
        if (seek >= 180000)
        {
            history[3].setLocation(135, 60);
            history[3].render(g);
        }

        // Render texts
        if (seek >= 113500 && seek < 130000)
        {
            font.draw(g,
                      1,
                      history[0].getHeight() + 2,
                      Align.LEFT,
                      "Valdyn crossed the border and flew into %Norka's country. Everything had gone %smoothly until now. %Then he noticed a shape on the horizon %which rapidly grew bigger. One of Norka's %air-ship!");
        }
        if (seek >= 130000 && seek < 154000)
        {
            font.draw(g,
                      1,
                      history[1].getHeight() + 22,
                      Align.LEFT,
                      "Frantically, Valdyn urged the dragon %to fly faster. Maybe he could still escape! %But the airship grew ever larger. %It had neared to a distance of a few meters %when a explosion sounded and rapidly %expanding net flew towards Valdyn and %the dragon!");
        }
        if (seek >= 154000 && seek < 180000)
        {
            font.draw(g,
                      1,
                      history[2].getHeight() + 42,
                      Align.LEFT,
                      "They were caught by the net and were %swiftly hauled towards the ship. %Valdyn, who was having a hard time with %the panicking dragon, drew his sword and %hacked a way through the tough material. %Just before being drawn into the air-ship, %he jumped out and plummeted towards the %ground!");
        }
        if (seek >= 180000 && seek < 201000)
        {
            font.draw(g,
                      1,
                      history[3].getHeight() + 62,
                      Align.LEFT,
                      "Due to his new powers, Valdyn had survived %the long fall without even a scratch. %He stood forlornly in the swamps and %watched the air-ship fly out the sight. %He cursed. His mission wouldn't be so %easy now.");
        }

        // Render fade in
        if (alphaBack < 255)
        {
            g.setColor(Constant.ALPHAS_BLACK[255 - (int) alphaBack]);
            g.drawRect(0, 0, getWidth(), getHeight(), true);
        }
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        super.onTerminated(hasNextSequence);

        audio.stop();
    }
}
