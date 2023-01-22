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

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.AnimatorFrameListener;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.State;

/**
 * Grip soar state implementation.
 */
public final class StateGripSoar extends State
{
    private static final int FRAME_5 = 5;
    private static final int FRAME_8 = 8;

    private static final int OFFSET_1 = 10;
    private static final int OFFSET_5 = -30;
    private static final int OFFSET_8 = -55;

    private static final double SOAR_SPEED = 1.02;

    private final Camera camera;
    private final MapTile map;
    private double offset;
    private double offset2;
    /** Handle frame vertical specific offset for rendering. */
    private final AnimatorFrameListener listener;

    /** Specific frame offset computed by listener. */
    private int frameOffset;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    public StateGripSoar(EntityModel model, Animation animation)
    {
        super(model, animation);

        camera = model.getCamera();
        map = model.getMap();
        listener = (AnimatorFrameListener) frame ->
        {
            if (frame - animation.getFirst() < FRAME_5)
            {
                frameOffset = OFFSET_1;
            }
            if (frame - animation.getFirst() >= FRAME_5 && frame - animation.getFirst() < FRAME_8)
            {
                frameOffset = OFFSET_5;
            }
            else if (frame - animation.getFirst() >= FRAME_8)
            {
                frameOffset = OFFSET_8;
            }
            rasterable.setFrameOffsets(0, frameOffset);
        };

        addTransition(StateIdle.class, () -> is(AnimState.FINISHED));
    }

    @Override
    public void enter()
    {
        super.enter();

        movement.zero();
        animatable.addListener(listener);

        offset = 0.0;
        offset2 = 0.0;
        frameOffset = OFFSET_1;
        animatable.setFrame(animation.getFirst());
    }

    @Override
    public void update(double extrp)
    {
        offset += SOAR_SPEED * extrp;

        if (camera.getViewpointY(transformable.getY() + offset) < 155
            && camera.getY() + camera.getHeight() < map.getHeight())
        {
            camera.setShake(0, (int) (offset - offset2));
        }
        else
        {
            offset2 += SOAR_SPEED * extrp;
        }
        rasterable.setFrameOffsets(0, frameOffset);
        body.resetGravity();
    }

    @Override
    public void exit()
    {
        super.exit();

        offset = 0.0;
        offset2 = 0.0;
        frameOffset = 0;
        rasterable.setFrameOffsets(0, 0);
        transformable.teleportY(transformable.getY() + 55.0);
        animatable.removeListener(listener);
        camera.setShake(0, 0);
    }
}
