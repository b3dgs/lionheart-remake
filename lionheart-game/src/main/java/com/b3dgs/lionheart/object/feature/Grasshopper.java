/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.launchable.Launcher;
import com.b3dgs.lionengine.game.feature.rasterable.SetupSurfaceRastered;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.io.InputDeviceControlVoid;
import com.b3dgs.lionheart.Sfx;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.state.StateWalk;

/**
 * Grasshopper feature implementation.
 * <ol>
 * <li>Follow player until defined distance.</li>
 * <li>Fire fly on timing.</li>
 * </ol>
 */
@FeatureInterface
public final class Grasshopper extends FeatureModel implements Routine
{
    private static final int JUMP_TICK = 5;

    private final Tick jumpTick = new Tick();
    private final MapTile map;
    private final Transformable track;

    private double move;
    private boolean jump;

    @FeatureGet private Transformable transformable;
    @FeatureGet private Mirrorable mirrorable;
    @FeatureGet private Launcher launcher;
    @FeatureGet private EntityModel model;
    @FeatureGet private StateHandler handler;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Grasshopper(Services services, SetupSurfaceRastered setup)
    {
        super(services, setup);

        map = services.get(MapTile.class);
        track = services.get(SwordShade.class).getFeature(Transformable.class);
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        model.setInput(new InputDeviceControlVoid()
        {
            @Override
            public double getHorizontalDirection()
            {
                return move;
            }

            @Override
            public double getVerticalDirection()
            {
                return jump ? 1.0 : 0.0;
            }

            @Override
            public boolean isUpButtonOnce()
            {
                return jump;
            }
        });
    }

    @Override
    public void update(double extrp)
    {
        if (jump)
        {
            jumpTick.update(extrp);
            if (jumpTick.elapsed(JUMP_TICK))
            {
                jump = false;
            }
        }
        else if (handler.isState(StateWalk.class)
                 && (map.getTile(transformable, 8, 0) == null && map.getTile(transformable, -8, 0) != null
                     || map.getTile(transformable, 8, 0) != null && map.getTile(transformable, -8, 0) == null))
        {
            jump = true;
            jumpTick.restart();
            if (map.getTile(transformable, 8, 0) == null)
            {
                move = 1.2;
            }
            else if (map.getTile(transformable, -8, 0) == null)
            {
                move = -1.2;
            }
            Sfx.GRASSHOPPER_JUMP.play();
        }
        else
        {
            if (track.getX() - transformable.getX() > 100)
            {
                move = 1.0;
            }
            else if (track.getX() - transformable.getX() < -112)
            {
                move = -1.0;
            }
            else
            {
                move = 0.0;

                if (transformable.getX() > track.getX())
                {
                    mirrorable.mirror(Mirror.HORIZONTAL);
                }
                else
                {
                    mirrorable.mirror(Mirror.NONE);
                }

                launcher.fire();
            }
        }
    }
}
