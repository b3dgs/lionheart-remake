/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 *
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
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
package com.b3dgs.lionheart.android;

import android.os.Bundle;
import android.util.DisplayMetrics;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.android.ActivityGame;
import com.b3dgs.lionengine.android.graphic.EngineAndroid;
import com.b3dgs.lionengine.android.graphic.ScreenAndroid;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.audio.sc68.Sc68Format;
import com.b3dgs.lionengine.graphic.engine.Loader;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.GameConfig;
import com.b3dgs.lionheart.Loading;
import com.b3dgs.lionheart.Settings;

/**
 * Android entry point.
 */
public final class ActivityLionheart extends ActivityGame
{
    /**
     * Constructor.
     */
    public ActivityLionheart()
    {
        super();
    }

    @Override
    protected void start(Bundle bundle)
    {
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final double size = (double) Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels);
        final double ratio = Constant.RESOLUTION_OUTPUT.getHeight() / size;
        int width = (int) Math.round(Math.max(displayMetrics.widthPixels, displayMetrics.heightPixels) * ratio);
        int height = (int) Math.round(size * ratio);

        VirtualKeyboard.applyWidthRatio(width / (double) Constant.RESOLUTION_OUTPUT.getWidth());

        ScreenAndroid.setVirtualKeyboard(VirtualKeyboard.class);
        EngineAndroid.start(Constant.PROGRAM_NAME, Constant.PROGRAM_VERSION, this);
        AudioFactory.addFormat(Sc68Format.getFailsafe());

        Settings.load();
        final Settings settings = Settings.getInstance();
        AudioFactory.setVolume(settings.getVolumeMaster());

        final Resolution resolution = new Resolution(width, height, settings.getResolution(Constant.RESOLUTION_OUTPUT).getRate());
        Loader.start(Config.fullscreen(resolution), Loading.class, new GameConfig(), Boolean.FALSE);
    }
}
