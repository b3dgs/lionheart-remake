/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.networkable.Networkable;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.Text;

/**
 * Clients list display.
 */
@FeatureInterface
public class ClientsList extends FeatureModel implements Routine
{
    private static final Text TEXT = Graphics.createText(9);

    private final Camera camera = services.get(Camera.class);
    private final Map<Integer, String> clients = services.getOptional(ConcurrentHashMap.class).orElse(null);

    private final Transformable transformable;
    private final Networkable networkable;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @param transformable The transformable feature.
     * @param networkable The networkable feature.
     * @throws LionEngineException If invalid arguments.
     */
    public ClientsList(Services services, Setup setup, Transformable transformable, Networkable networkable)
    {
        super(services, setup);

        this.transformable = transformable;
        this.networkable = networkable;
    }

    @Override
    public void render(Graphic g)
    {
        if (networkable.isServer())
        {
            final String name = clients.get(networkable.getClientId());
            if (name != null)
            {
                TEXT.draw(g,
                          (int) Math.round(camera.getViewpointX(transformable.getX())),
                          (int) Math.round(camera.getViewpointY(transformable.getY())
                                           - transformable.getHeight()
                                           - TEXT.getSize()),
                          Align.CENTER,
                          name);
            }
        }
    }
}
