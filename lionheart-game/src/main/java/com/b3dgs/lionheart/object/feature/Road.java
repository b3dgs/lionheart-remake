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
package com.b3dgs.lionheart.object.feature;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilFolder;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.background.BackgroundElement;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionheart.constant.Folder;

/**
 * Road feature implementation.
 */
@FeatureInterface
public final class Road extends FeatureModel implements Routine
{
    private final Camera camera = services.get(Camera.class);
    private final BackgroundElement road;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Road(Services services, Setup setup)
    {
        super(services, setup);

        final String path = UtilFolder.getPathSeparator(Medias.getSeparator(), Folder.FOREGROUND, "airship");
        final Sprite sprite0 = Drawable.loadSprite(Medias.create(path, "road.png"));
        sprite0.load();
        sprite0.prepare();
        road = new BackgroundElement(0, 0, sprite0);
        road.setOffsetY(-176);
    }

    @Override
    public void update(double extrp)
    {
        road.setOffsetX(UtilMath.wrapDouble(road.getOffsetX() - 3 * extrp, 0.0, road.getRenderable().getWidth()));
    }

    @Override
    public void render(Graphic g)
    {
        if (camera.getX() > 8291)
        {
            final Sprite sprite0 = (Sprite) road.getRenderable();
            final int w0 = (int) Math.ceil(camera.getWidth() / (double) sprite0.getWidth());
            final int y0 = (int) (camera.getHeight() + road.getOffsetY());
            if (y0 >= -sprite0.getHeight() && y0 < camera.getHeight())
            {
                for (int j = 0; j <= w0; j++)
                {
                    sprite0.setLocation(sprite0.getWidth() * j + road.getOffsetX() - sprite0.getWidth(),
                                        camera.getViewpointY(y0));
                    sprite0.render(g);
                }
            }
        }
    }
}
