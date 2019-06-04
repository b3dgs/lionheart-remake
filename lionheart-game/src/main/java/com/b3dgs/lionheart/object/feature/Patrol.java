/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionheart.object.feature;

import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UpdatableVoid;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.body.Body;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.rasterable.SetupSurfaceRastered;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.InputDeviceControlVoid;
import com.b3dgs.lionheart.object.Entity;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.Routine;
import com.b3dgs.lionheart.object.state.StatePatrol;
import com.b3dgs.lionheart.object.state.StateTurn;

/**
 * Patrol feature implementation.
 */
@FeatureInterface
public final class Patrol extends FeatureModel implements Routine
{
    private final Updatable checker;
    private final boolean mirror;
    private final boolean coll;
    private final int amplitude;

    private double sh;
    private double sv;
    private double moved;

    @FeatureGet private Transformable transformable;
    @FeatureGet private EntityModel model;
    @FeatureGet private StateHandler stateHandler;
    @FeatureGet private Collidable collidable;
    @FeatureGet private Mirrorable mirrorable;
    @FeatureGet private Body body;

    /**
     * Create patrol.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public Patrol(Services services, SetupSurfaceRastered setup)
    {
        super();

        final PatrolConfig config = PatrolConfig.imports(setup);
        sh = config.getSh();
        sv = config.getSv();
        amplitude = config.getAmplitude();
        mirror = config.hasMirror();
        coll = config.hasColl();

        final AnimationConfig anim = AnimationConfig.imports(setup);
        if (amplitude > 0)
        {
            checker = extrp ->
            {
                if (Math.abs(moved) > amplitude)
                {
                    moved = 0.0;
                    if (Double.compare(sh, 0.0) != 0)
                    {
                        sh = -sh;
                    }
                    if (Double.compare(sv, 0.0) != 0)
                    {
                        sv = -sv;
                    }
                    if (anim.hasAnimation(Constant.ANIM_NAME_TURN))
                    {
                        stateHandler.changeState(StateTurn.class);
                    }
                }
            };
        }
        else
        {
            checker = UpdatableVoid.getInstance();
        }
    }

    /**
     * Perform mirror computation if required.
     */
    public void applyMirror()
    {
        if (sh < 0 && mirrorable.is(Mirror.NONE))
        {
            mirrorable.mirror(Mirror.HORIZONTAL);
        }
        else if (sh > 0 && mirrorable.is(Mirror.HORIZONTAL))
        {
            mirrorable.mirror(Mirror.NONE);
        }
        if (sv < 0 && mirrorable.is(Mirror.NONE))
        {
            mirrorable.mirror(Mirror.VERTICAL);
        }
        else if (sv > 0 && mirrorable.is(Mirror.VERTICAL))
        {
            mirrorable.mirror(Mirror.NONE);
        }
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        stateHandler.changeState(StatePatrol.class);
        stateHandler.addListener((from, to) ->
        {
            collidable.setEnabled(!coll || !Constant.ANIM_NAME_TURN.equals(Entity.getAnimationName(to)));
        });
        model.setInput(new InputDeviceControlVoid()
        {
            @Override
            public double getHorizontalDirection()
            {
                return sh;
            }

            @Override
            public double getVerticalDirection()
            {
                return sv;
            }
        });
    }

    @Override
    public void update(double extrp)
    {
        checker.update(extrp);
        moved += model.getMovement().getDirectionHorizontal();
    }
}
