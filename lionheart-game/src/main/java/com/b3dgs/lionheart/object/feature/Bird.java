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

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListener;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.launchable.Launchable;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionengine.io.DeviceController;
import com.b3dgs.lionengine.io.DeviceControllerVoid;
import com.b3dgs.lionheart.Sfx;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.state.StateIdle;
import com.b3dgs.lionheart.object.state.StatePatrol;

/**
 * Bird feature implementation.
 * <ol>
 * <li>Fly vertical on patrol.</li>
 * <li>Hurt player on top, can be hit to be in platform mode.</li>
 * <li>Player can walk over in platform mode.</li>
 * <li>Move back to patrol after delay.</li>
 * </ol>
 */
@FeatureInterface
public final class Bird extends FeatureModel implements Routine, Recyclable, CollidableListener
{
    private static final int IDLE_TIME_MS = 3000;

    private final Tick tick = new Tick();

    private final SourceResolutionProvider source = services.get(SourceResolutionProvider.class);

    private final EntityModel model;
    private final StateHandler stateHandler;
    private final Launchable launchable;
    private final Hurtable hurtable;
    private final Glue glue;

    private DeviceController device;
    private boolean hit;
    private Force old;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @param model The model feature.
     * @param stateHandler The state feature.
     * @param launchable The launchable feature.
     * @param hurtable The hurtable feature.
     * @param glue The glue feature.
     * @throws LionEngineException If invalid arguments.
     */
    public Bird(Services services,
                Setup setup,
                EntityModel model,
                StateHandler stateHandler,
                Launchable launchable,
                Hurtable hurtable,
                Glue glue)
    {
        super(services, setup);

        this.model = model;
        this.stateHandler = stateHandler;
        this.launchable = launchable;
        this.hurtable = hurtable;
        this.glue = glue;
    }

    @Override
    public void update(double extrp)
    {
        tick.update(extrp);

        if (hit && !hurtable.isHurting())
        {
            hit = false;
            stateHandler.changeState(StateIdle.class);
        }
        if (tick.elapsedTime(source.getRate(), IDLE_TIME_MS))
        {
            stateHandler.changeState(StatePatrol.class);
            launchable.setVector(old);
            model.setInput(device);
            glue.recycle();
            tick.stop();
        }
    }

    @Override
    public void notifyCollided(Collidable collidable, Collision with, Collision by)
    {
        if (!hit && with.getName().startsWith(Anim.BODY) && by.getName().startsWith(Anim.ATTACK))
        {
            old = launchable.getDirection();
            launchable.setVector(null);
            tick.start();
            Sfx.MONSTER_HURT.play();
            hit = true;
            device = model.getInput();
            model.setInput(DeviceControllerVoid.getInstance());
            glue.start();
        }
    }

    @Override
    public void recycle()
    {
        tick.stop();
        hit = false;
        old = null;
        device = null;
    }
}
