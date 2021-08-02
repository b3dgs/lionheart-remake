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
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionengine.game.DirectionNone;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.body.Body;
import com.b3dgs.lionengine.game.feature.rasterable.SetupSurfaceRastered;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroup;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroupModel;
import com.b3dgs.lionengine.io.DeviceController;
import com.b3dgs.lionengine.io.DeviceControllerVoid;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.EntityConfig;
import com.b3dgs.lionheart.constant.CollisionName;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.XmlLoader;
import com.b3dgs.lionheart.object.XmlSaver;
import com.b3dgs.lionheart.object.state.StateFall;
import com.b3dgs.lionheart.object.state.StateJump;
import com.b3dgs.lionheart.object.state.StatePatrol;

/**
 * Jumper feature implementation.
 * <ol>
 * <li>Jump automatically on border or slope.</li>
 * </ol>
 */
@FeatureInterface
public final class Jumper extends FeatureModel implements XmlLoader, XmlSaver, Routine
{
    private static final String NODE = "jumper";
    private static final String ATT_OFFSET = "offset";

    private final Tick jumpStopTick = new Tick();
    private final MapTile map;
    private final MapTileGroup mapGroup;
    private final DeviceController jumpControl = new DeviceControllerVoid()
    {
        @Override
        public double getHorizontalDirection()
        {
            return move;
        }

        @Override
        public double getVerticalDirection()
        {
            return jumpPress ? 1.0 : 0.0;
        }
    };
    private final int offset = setup.getInteger(0, ATT_OFFSET, NODE);

    private DeviceController oldControl;
    private int jumpTick;
    private double move;
    private boolean jump;
    private boolean jumpPress;

    @FeatureGet private Transformable transformable;
    @FeatureGet private EntityModel model;
    @FeatureGet private StateHandler handler;
    @FeatureGet private Body body;
    @FeatureGet private Mirrorable mirrorable;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Jumper(Services services, SetupSurfaceRastered setup)
    {
        super(services, setup);

        map = services.get(MapTile.class);
        mapGroup = map.getFeature(MapTileGroup.class);
    }

    /**
     * Set the jump value.
     * 
     * @param jumpTick The jump value.
     */
    public void setJump(int jumpTick)
    {
        this.jumpTick = jumpTick;
    }

    /**
     * Check if tile is no ground.
     * 
     * @param side The side to check.
     * @return <code>true</code> if no group, <code>false</code> else.
     */
    private boolean isNone(int side)
    {
        final String group = mapGroup.getGroup(map.getTile(transformable,
                                                           map.getTileWidth() * side,
                                                           (int) body.getGravity() - 5 + offset));
        return MapTileGroupModel.NO_GROUP_NAME.equals(group) || CollisionName.SPIKE.equals(group) || group == null;
    }

    @Override
    public void load(XmlReader root)
    {
        setJump(root.getInteger(0, EntityConfig.ATT_JUMP));
    }

    @Override
    public void save(Xml root)
    {
        if (jumpTick > 0)
        {
            root.writeInteger(EntityConfig.ATT_JUMP, jumpTick);
        }
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        handler.addListener((f, t) ->
        {
            if (t == StateJump.class)
            {
                if (mirrorable.is(Mirror.HORIZONTAL))
                {
                    move = -1.1;
                }

                if (mirrorable.is(Mirror.NONE))
                {
                    move = 1.1;
                }

                body.setGravityMax(Constant.GRAVITY / 3);
                model.getJump().setVelocity(0.1);
                model.getMovement().setDirection(move, 0.0);
                jumpStopTick.restart();
            }
            else if (oldControl != null && f == StateFall.class && t == StatePatrol.class)
            {
                move = 0.0;
                model.setInput(oldControl);
                body.setGravityMax(Constant.GRAVITY);
                model.getJump().setDirection(DirectionNone.INSTANCE);
                model.getMovement().setDirection(DirectionNone.INSTANCE);
                oldControl = null;
                jump = false;
                jumpPress = false;
                jumpStopTick.stop();
            }
        });
    }

    @Override
    public void update(double extrp)
    {
        if (!jump
            && handler.isState(StatePatrol.class)
            && (isNone(-1) && model.getMovement().getDirectionHorizontal() < 0
                || isNone(1) && model.getMovement().getDirectionHorizontal() > 0))
        {
            oldControl = model.getInput();
            model.setInput(jumpControl);
            jump = true;
            jumpPress = true;
        }
        else
        {
            jumpStopTick.update(extrp);
            if (jumpStopTick.elapsed(jumpTick))
            {
                jumpPress = false;
            }
        }
    }
}
