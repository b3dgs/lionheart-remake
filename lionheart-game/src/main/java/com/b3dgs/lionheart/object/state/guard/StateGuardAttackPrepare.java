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
package com.b3dgs.lionheart.object.state.guard;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.State;
import com.b3dgs.lionheart.object.feature.Guard;
import com.b3dgs.lionheart.object.feature.Trackable;
import com.b3dgs.lionheart.object.state.StateTurn;

/**
 * Guard prepare attack state implementation.
 */
public final class StateGuardAttackPrepare extends State
{
    private static final int PREPARE_DELAY_MS = 350;

    private final Tick tick = new Tick();
    private final Trackable target = model.getServices().get(Trackable.class);

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    public StateGuardAttackPrepare(EntityModel model, Animation animation)
    {
        super(model, animation);

        final SourceResolutionProvider source = model.getServices().get(SourceResolutionProvider.class);

        addTransition(StateGuardAttack.class,
                      () -> tick.elapsedTime(source.getRate(), PREPARE_DELAY_MS)
                            && (mirrorable.is(Mirror.NONE) && target.getX() < transformable.getX()
                                || mirrorable.is(Mirror.HORIZONTAL) && target.getX() > transformable.getX())
                            && Double.compare(Math.abs(target.getX() - transformable.getX() - 12),
                                              Guard.ATTACK_DISTANCE_MAX) < 0);

        addTransition(StateGuardSalto.class,
                      () -> tick.elapsedTime(source.getRate(), PREPARE_DELAY_MS)
                            && (mirrorable.is(Mirror.NONE) && target.getX() < transformable.getX()
                                || mirrorable.is(Mirror.HORIZONTAL) && target.getX() > transformable.getX())
                            && Double.compare(Math.abs(target.getX() - transformable.getX() - 12),
                                              Guard.ATTACK_DISTANCE_MAX) >= 0);

        addTransition(StateTurn.class,
                      () -> tick.elapsedTime(source.getRate(), PREPARE_DELAY_MS)
                            && (mirrorable.is(Mirror.NONE) && target.getX() > transformable.getX()
                                || mirrorable.is(Mirror.HORIZONTAL) && target.getX() < transformable.getX()));
    }

    @Override
    public void enter()
    {
        super.enter();

        movement.zero();
        tick.restart();
    }

    @Override
    public void update(double extrp)
    {
        super.update(extrp);

        tick.update(extrp);
    }
}
