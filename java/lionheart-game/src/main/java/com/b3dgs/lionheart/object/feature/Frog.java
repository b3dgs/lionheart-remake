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

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.RoutineUpdate;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionheart.Sfx;
import com.b3dgs.lionheart.constant.Anim;

/**
 * Frog feature implementation.
 * <ol>
 * <li>Come from back, await, move forward.</li>
 * </ol>
 */
@FeatureInterface
public final class Frog extends FeatureModel implements RoutineUpdate, Recyclable
{
    private static final int AWAIT_DELAY_MS = 1000;
    private static final double SPEED = 3.5;

    private final SourceResolutionProvider source = services.get(SourceResolutionProvider.class);
    private final Viewer viewer = services.get(Viewer.class);

    private final Transformable transformable;
    private final Animatable animatable;
    private final Rasterable rasterable;
    private final Collidable collidable;
    private final Identifiable identifiable;

    private final Tick tick = new Tick();
    private final Animation idle;
    private final Animation turn;

    private int phase;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @param transformable The transformable feature.
     * @param animatable The animatable feature.
     * @param rasterable The rasterable feature.
     * @param collidable The collidable feature.
     * @param identifiable The identifiable feature.
     * @throws LionEngineException If invalid arguments.
     */
    public Frog(Services services,
                Setup setup,
                Transformable transformable,
                Animatable animatable,
                Rasterable rasterable,
                Collidable collidable,
                Identifiable identifiable)
    {
        super(services, setup);

        this.transformable = transformable;
        this.animatable = animatable;
        this.rasterable = rasterable;
        this.collidable = collidable;
        this.identifiable = identifiable;

        final AnimationConfig config = AnimationConfig.imports(setup);
        idle = config.getAnimation(Anim.IDLE);
        turn = config.getAnimation(Anim.TURN);
    }

    @Override
    public void update(double extrp)
    {
        tick.update(extrp);

        if (phase == 0 && transformable.getX() > viewer.getX())
        {
            phase = 1;
        }
        else if (phase == 1 && transformable.getX() < viewer.getX() - transformable.getWidth())
        {
            rasterable.setVisibility(true);
            tick.stop();
            phase = 2;
        }
        else if (phase == 2)
        {
            transformable.moveLocationX(extrp, SPEED);
            if (transformable.getX() > viewer.getX())
            {
                transformable.teleportX(viewer.getX());
                tick.start();
            }
            if (tick.elapsedTime(source.getRate(), AWAIT_DELAY_MS))
            {
                collidable.setEnabled(true);
                animatable.play(turn);
                Sfx.MONSTER_FROG.play();
                phase = 3;
            }
        }
        else if (phase == 3)
        {
            transformable.moveLocationX(extrp, SPEED);
            if (transformable.getX() > viewer.getX() + viewer.getWidth() + transformable.getWidth())
            {
                identifiable.destroy();
            }
        }
    }

    @Override
    public void recycle()
    {
        animatable.play(idle);
        rasterable.setVisibility(false);
        collidable.setEnabled(false);
        phase = 0;
    }
}
