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
package com.b3dgs.lionheart.object.state;

import java.util.Optional;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.geom.Coord;
import com.b3dgs.lionengine.graphic.engine.Sequencer;
import com.b3dgs.lionheart.CheatsProvider;
import com.b3dgs.lionheart.CheckpointHandler;
import com.b3dgs.lionheart.Difficulty;
import com.b3dgs.lionheart.InitConfig;
import com.b3dgs.lionheart.landscape.Landscape;
import com.b3dgs.lionheart.menu.Continue;
import com.b3dgs.lionheart.menu.Menu;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.State;
import com.b3dgs.lionheart.object.feature.Stats;

/**
 * Respawn state implementation.
 */
public final class StateRespawn extends State
{
    private final Stats stats = model.getFeature(Stats.class);
    private final CheckpointHandler checkpoint = model.getCheckpoint();
    private final Landscape landscape = model.getServices().get(Landscape.class);
    private final Difficulty difficulty = model.getServices().get(Difficulty.class);
    private final boolean cheats = model.getServices().get(CheatsProvider.class).getCheats();

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    StateRespawn(EntityModel model, Animation animation)
    {
        super(model, animation);

        addTransition(StateIdle.class, () -> true);
    }

    @Override
    public void enter()
    {
        if (stats.getLife() == 0)
        {
            final Sequencer sequencer = model.getServices().get(Sequencer.class);
            if (stats.getCredits() > 0)
            {
                sequencer.end(Continue.class,
                              new InitConfig(model.getServices().get(Media.class),
                                             stats.getHealthMax(),
                                             stats.getTalisment(),
                                             stats.getLife(),
                                             stats.getSword(),
                                             stats.hasAmulet().booleanValue(),
                                             stats.getCredits() - 1,
                                             difficulty,
                                             false,
                                             Optional.empty()));
            }
            else
            {
                sequencer.end(Menu.class);
            }
        }
        else
        {
            final Coord check = checkpoint.getCurrent(transformable);
            transformable.teleport(check.getX(), check.getY());
            body.resetGravity();
            model.getCamera().resetInterval(transformable);
            model.getTracker().track(transformable);
            movement.zero();
            jump.zero();
            mirrorable.mirror(Mirror.NONE);
            stats.fillHealth();
            if (!cheats)
            {
                stats.decreaseLife();
            }
            landscape.reset();
        }
    }
}
