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

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.object.Entity;
import com.b3dgs.lionheart.object.Routine;
import com.b3dgs.lionheart.object.state.StateTurn;

/**
 * Turning feature implementation.
 */
@FeatureInterface
public final class Turning extends FeatureModel implements Routine
{
    private static final double CURVE_FORCE = 4.0;
    private static final double CURVE_SPEED = 50.0;
    private static final int DELAY_BEFORE_ROTATE = 50;
    private static final int DELAY_BEFORE_SHAKE = 100;
    private static final int SHAKE_MAX_COUNT = 3;

    private final Tick tick = new Tick();
    private final Updatable checkRotate;
    private final Updatable shake;
    private Updatable checkShake;
    private Updatable current;
    private double curve;

    @FeatureGet private StateHandler stateHandler;
    @FeatureGet private Collidable collidable;
    @FeatureGet private Animatable animatable;
    @FeatureGet private Glue glue;

    /**
     * Create turning.
     */
    public Turning()
    {
        super();

        checkRotate = extrp ->
        {
            tick.update(extrp);
            if (tick.elapsed(DELAY_BEFORE_ROTATE))
            {
                stateHandler.changeState(StateTurn.class);
                tick.restart();
                glue.stop();
                glue.setGlue(false);
                collidable.setEnabled(false);
                current = checkShake;
            }
        };
        shake = extrp ->
        {
            curve += CURVE_SPEED;
            if (curve > SHAKE_MAX_COUNT * com.b3dgs.lionengine.Constant.MAX_DEGREE)
            {
                current = checkRotate;
                curve = 0;
                tick.start();
            }
        };
        checkShake = extrp ->
        {
            tick.update(extrp);
            if (tick.elapsed(DELAY_BEFORE_SHAKE) && animatable.getAnimState() == AnimState.FINISHED)
            {
                glue.start();
                glue.setTransformY(() -> UtilMath.sin(curve) * CURVE_FORCE);
                tick.stop();
                current = shake;
            }
        };
        current = checkShake;
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        stateHandler.addListener((from, to) ->
        {
            collidable.setEnabled(!Constant.ANIM_NAME_TURN.equals(Entity.getAnimationName(to)));
        });
        tick.start();
    }

    @Override
    public void update(double extrp)
    {
        current.update(extrp);
    }
}
