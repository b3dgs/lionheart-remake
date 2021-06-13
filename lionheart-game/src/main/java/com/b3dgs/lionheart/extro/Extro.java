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
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.engine.Sequence;
import com.b3dgs.lionengine.helper.DeviceControllerConfig;
import com.b3dgs.lionheart.Music;
import com.b3dgs.lionheart.Settings;
import com.b3dgs.lionheart.Util;

/**
 * Extro implementation.
 */
public final class Extro extends Sequence
{
    private static final int MIN_HEIGHT = 208;
    private static final int MAX_WIDTH = 400;
    private static final int MARGIN_WIDTH = 80;

    private final Audio audio;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     * @param alternative The alternative end.
     */
    public Extro(Context context, Boolean alternative)
    {
        super(context, Util.getResolution(context, MIN_HEIGHT, MAX_WIDTH, MARGIN_WIDTH));

        audio = AudioFactory.loadAudio(Music.EXTRO);
        audio.setVolume(Settings.getInstance().getVolumeMusic());

        final Services services = new Services();
        services.add(context);
        services.add(DeviceControllerConfig.create(services, Medias.create("input.xml"))).setVisible(false);

        load(Part1.class, audio, alternative);

        setSystemCursorVisible(false);
    }

    @Override
    public void load()
    {
        // Nothing to do
    }

    @Override
    protected void onLoaded(double extrp, Graphic g)
    {
        audio.play();
    }

    @Override
    public void update(double extrp)
    {
        end();
    }

    @Override
    public void render(Graphic g)
    {
        // Nothing to do
    }
}
