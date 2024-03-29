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

import java.io.IOException;
import java.nio.ByteBuffer;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UpdatableVoid;
import com.b3dgs.lionengine.UtilConversion;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.RoutineUpdate;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListener;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.networkable.Networkable;
import com.b3dgs.lionengine.game.feature.networkable.Syncable;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionengine.game.feature.tile.map.collision.Axis;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionResult;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableListener;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionengine.io.DeviceControllerVoid;
import com.b3dgs.lionengine.io.FileReading;
import com.b3dgs.lionengine.io.FileWriting;
import com.b3dgs.lionengine.network.Packet;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.constant.CollisionName;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.Snapshotable;
import com.b3dgs.lionheart.object.XmlLoader;
import com.b3dgs.lionheart.object.state.StateFall;
import com.b3dgs.lionheart.object.state.StateJump;
import com.b3dgs.lionheart.object.state.StatePatrol;
import com.b3dgs.lionheart.object.state.StatePatrolCeil;
import com.b3dgs.lionheart.object.state.StateTurn;

/**
 * Patrol feature implementation.
 * <p>
 * Move loop from starting position to defined amplitude with specified speed.
 * </p>
 */
@FeatureInterface
public final class Patrol extends FeatureModel implements XmlLoader, RoutineUpdate, TileCollidableListener,
                          CollidableListener, Syncable, Snapshotable, Recyclable
{
    private final SourceResolutionProvider source = services.get(SourceResolutionProvider.class);
    private final Trackable target = services.getOptional(Trackable.class).orElse(null);

    private final EntityModel model;
    private final StateHandler stateHandler;
    private final Mirrorable mirrorable;
    private final Transformable transformable;
    private final Rasterable rasterable;
    private final Stats stats;
    private final Patrols patrols;
    private final Networkable networkable;

    private final Tick tickSync = new Tick();
    private final Tick tick = new Tick();
    private final AnimationConfig anim;

    private int currentIndex;
    private double sh;
    private double sv;
    private int amplitude;
    private int offset;
    private boolean coll;
    private int proximity;
    private int sight;
    private int animOffset;
    private int delay;
    private boolean curve;
    private int skip;

    private double startX;
    private double startY;
    private double curveAngle;

    private Updatable checker;
    private boolean enabled = true;
    private boolean first = true;
    private double idle;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @param model The model feature.
     * @param stateHandler The state feature.
     * @param collidable The collidable feature.
     * @param mirrorable The mirrorable feature.
     * @param transformable The transformable feature.
     * @param rasterable The rasterable feature.
     * @param stats The stats feature.
     * @param patrols The patrols feature.
     * @param networkable The networkable feature.
     * @throws LionEngineException If invalid arguments.
     */
    public Patrol(Services services,
                  Setup setup,
                  EntityModel model,
                  StateHandler stateHandler,
                  Collidable collidable,
                  Mirrorable mirrorable,
                  Transformable transformable,
                  Rasterable rasterable,
                  Stats stats,
                  Patrols patrols,
                  Networkable networkable)
    {
        super(services, setup);

        this.model = model;
        this.stateHandler = stateHandler;
        this.mirrorable = mirrorable;
        this.transformable = transformable;
        this.rasterable = rasterable;
        this.stats = stats;
        this.patrols = patrols;
        this.networkable = networkable;

        anim = AnimationConfig.imports(setup);

        stateHandler.addListener((from, to) ->
        {
            if (stats.getHealth() > 0)
            {
                collidable.setEnabled(!coll || !Anim.TURN.equals(EntityModel.getAnimationName(to)));
            }
        });
        model.setInput(new DeviceControllerVoid()
        {
            @Override
            public double getHorizontalDirection()
            {
                return enabled
                       && (stateHandler.isState(StatePatrol.class) || stateHandler.isState(StatePatrolCeil.class)) ? sh
                                                                                                                   : 0.0;
            }

            @Override
            public double getVerticalDirection()
            {
                return !curve && enabled ? sv : 0.0;
            }
        });
        model.getMovement().setVelocity(1.0);

        applyMirror();
        mirrorable.update(1.0);

        rasterable.setAnimOffset(animOffset);
    }

    /**
     * Set custom patrol.
     * 
     * @param sh The horizontal patrol.
     * @param sv The vertical patrol.
     */
    public void set(double sh, double sv)
    {
        this.sh = sh;
        this.sv = sv;

        if (sh < 0)
        {
            mirrorable.mirror(Mirror.HORIZONTAL);
        }
        else if (sv < 0)
        {
            mirrorable.mirror(Mirror.VERTICAL);
        }
        else if (sh > 0 || sv > 0)
        {
            mirrorable.mirror(Mirror.NONE);
        }
    }

    /**
     * Stop patrol.
     */
    public void stop()
    {
        sh = 0.0;
        sv = 0.0;
    }

    /**
     * Disable patrol.
     */
    public void disable()
    {
        checker = UpdatableVoid.getInstance();
    }

    /**
     * Load next available patrol.
     */
    private void loadNextPatrol()
    {
        tick.stop();
        currentIndex++;
        if (currentIndex >= patrols.size())
        {
            currentIndex = 0;
        }
        final PatrolConfig config = patrols.get(currentIndex);

        config.getSh().ifPresent(h -> sh = h);
        config.getSv().ifPresent(v -> sv = v);
        config.getAmplitude().ifPresent(a -> amplitude = a);
        config.getOffset().ifPresent(o -> offset = o);
        config.getColl().ifPresent(c -> coll = c.booleanValue());
        config.getAnimOffset().ifPresent(o -> animOffset = o);
        config.getDelay().ifPresent(o ->
        {
            delay = o;
            tick.start();
        });
        proximity = 0;
        enabled = true;
        config.getProximity().ifPresent(p ->
        {
            proximity = p;
            config.getSight().ifPresent(s -> sight = s);
            enabled = false;
        });
        config.getCurve().ifPresent(c -> curve = c.booleanValue());

        checkAmplitude();
        applyMirror();

        first = true;
        startX = 0;
        startY = 0;

        rasterable.setAnimOffset(animOffset);
    }

    /**
     * Perform mirror computation if required.
     */
    public void applyMirror()
    {
        if (mirrorable != null
            && enabled
            && (patrols.size() == 0
                || currentIndex > -1 && patrols.get(currentIndex).getMirror().orElse(Boolean.TRUE).booleanValue()))
        {
            if (mirrorable.is(Mirror.NONE))
            {
                if (sh < 0)
                {
                    mirrorable.mirror(Mirror.HORIZONTAL);
                }
                else if (sv < 0)
                {
                    mirrorable.mirror(Mirror.VERTICAL);
                }
            }
            else if (mirrorable.is(Mirror.HORIZONTAL) && sh > 0 || mirrorable.is(Mirror.VERTICAL) && sv > 0)
            {
                mirrorable.mirror(Mirror.NONE);
            }
        }
    }

    /**
     * Check amplitude movement.
     */
    private void checkAmplitude()
    {
        if (curve || amplitude == 0)
        {
            checker = UpdatableVoid.getInstance();
        }
        else
        {
            checker = extrp ->
            {
                if (skip == 0
                    && (Double.compare(sh, 0.0) != 0 && Math.abs(startX - transformable.getX() - sh) >= amplitude
                        || Double.compare(sv, 0.0) != 0 && Math.abs(startY - transformable.getY() - sv) >= amplitude)
                    || skip == 2)
                {
                    if (sh > 0 && Double.compare(sv, 0) == 0)
                    {
                        transformable.teleportX(startX + amplitude);
                    }
                    else if (sh < 0 && Double.compare(sv, 0) == 0)
                    {
                        transformable.teleportX(startX - amplitude);
                    }
                    if (sv > 0 && Double.compare(sh, 0) == 0)
                    {
                        transformable.teleportY(startY + amplitude);
                    }
                    else if (sv < 0 && Double.compare(sh, 0) == 0)
                    {
                        transformable.teleportY(startY - amplitude);
                    }

                    if (Double.compare(sh, 0.0) != 0)
                    {
                        sh = -sh;
                    }
                    if (Double.compare(sv, 0.0) != 0)
                    {
                        sv = -sv;
                    }
                    model.getMovement().zero();
                    changeDirection();
                    if (patrols.size() > 1)
                    {
                        loadNextPatrol();
                    }
                    else
                    {
                        startX = transformable.getX();
                        startY = transformable.getY();
                    }
                    skip = 0;
                    sync();
                }
                if (amplitude < 0 && !stateHandler.isState(StateTurn.class))
                {
                    skip++;
                }
            };
        }
    }

    /**
     * Get horizontal patrol.
     * 
     * @return The horizontal patrol.
     */
    public double getSh()
    {
        return sh;
    }

    /**
     * Get vertical patrol.
     * 
     * @return The vertical patrol.
     */
    public double getSv()
    {
        return sv;
    }

    /**
     * Change direction side.
     */
    private void changeDirection()
    {
        if (anim.hasAnimation(Anim.TURN))
        {
            stateHandler.changeState(StateTurn.class);
        }
        else
        {
            applyMirror();
        }
    }

    @Override
    public void save(FileWriting file) throws IOException
    {
        file.writeDouble(tick.elapsed());
        file.writeInteger(currentIndex);
        file.writeDouble(sh);
        file.writeDouble(sv);
        file.writeInteger(amplitude);
        file.writeInteger(offset);
        file.writeBoolean(coll);
        file.writeInteger(proximity);
        file.writeInteger(sight);
        file.writeInteger(animOffset);
        file.writeInteger(delay);
        file.writeBoolean(curve);
        file.writeInteger(skip);
        file.writeDouble(startX);
        file.writeDouble(startY);
        file.writeDouble(curveAngle);
        file.writeBoolean(first);
        file.writeDouble(idle);
    }

    @Override
    public void load(FileReading file) throws IOException
    {
        tick.set(file.readDouble());
        currentIndex = file.readInteger();
        sh = file.readDouble();
        sv = file.readDouble();
        amplitude = file.readInteger();
        offset = file.readInteger();
        coll = file.readBoolean();
        proximity = file.readInteger();
        sight = file.readInteger();
        animOffset = file.readInteger();
        delay = file.readInteger();
        curve = file.readBoolean();
        skip = file.readInteger();
        startX = file.readDouble();
        startY = file.readDouble();
        curveAngle = file.readDouble();
        first = file.readBoolean();
        idle = file.readDouble();

        checkAmplitude();
        applyMirror();
        rasterable.setAnimOffset(animOffset);
    }

    @Override
    public void load(XmlReader root)
    {
        sh = 0;
        sv = 0;
        amplitude = 0;
        offset = 0;
        coll = false;
        animOffset = 0;

        currentIndex = -1;
        if (patrols.size() > 0)
        {
            loadNextPatrol();
            if (delay > 0)
            {
                sv = 0.0;
                sh = 0.0;
                currentIndex = -1;
            }
        }
    }

    @Override
    public void update(double extrp)
    {
        tick.update(extrp);
        if (tick.elapsedTime(source.getRate(), delay))
        {
            loadNextPatrol();
        }

        if (first)
        {
            first = false;
            startX = transformable.getX();
            startY = transformable.getY();
            if (offset > 0)
            {
                transformable.teleportX(startX + offset);
            }
        }
        if (!enabled)
        {
            if (target != null && Math.abs(transformable.getX() - target.getX()) < proximity)
            {
                enabled = sight == 0 || Math.abs(target.getY() - transformable.getY()) < sight;
            }
            else if (stats.getHealth() > 0)
            {
                idle = UtilMath.wrapDouble(idle + 0.15 * extrp, 0, 360);
                transformable.teleportY(startY + Math.sin(idle) * 2.0);
            }
        }
        if (curve)
        {
            curveAngle = UtilMath.wrapAngleDouble(curveAngle + sv * extrp);
            transformable.setLocationY(startY + Math.cos(curveAngle + 90) * amplitude);
        }
        if (!stateHandler.isState(StateJump.class) || stateHandler.isState(StateFall.class))
        {
            checker.update(extrp);
        }

        tickSync.update(extrp);
        if (tickSync.elapsedTime(source.getRate(), 500))
        {
            sync();
        }
    }

    private void sync()
    {
        if (networkable.isOwner())
        {
            final ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES * 4 + Float.BYTES * 6 + 2);
            buffer.putInt(getSyncId());
            buffer.put(UtilConversion.fromUnsignedByte(currentIndex));
            buffer.putFloat((float) sh);
            buffer.putFloat((float) sv);
            buffer.putInt(amplitude);
            buffer.putInt(offset);
            buffer.putInt(animOffset);
            buffer.put(UtilConversion.fromUnsignedByte(mirrorable.getMirror().ordinal()));
            buffer.putFloat((float) startX);
            buffer.putFloat((float) startY);
            buffer.putFloat((float) transformable.getX());
            buffer.putFloat((float) transformable.getY());
            networkable.send(buffer);
            tickSync.restart();
        }
    }

    @Override
    public void notifyCollided(Collidable collidable, Collision with, Collision by)
    {
        if (sh < 0 && by.getName().equals(CollisionName.RIGHT_VERTICAL)
            || sh > 0 && by.getName().equals(CollisionName.LEFT_VERTICAL))
        {
            sh = -sh;
            transformable.teleportX(transformable.getOldX() + sh);
            changeDirection();
        }
        if (CollisionName.COLL_SIGH.equals(with.getName()) && collidable.hasFeature(Trackable.class))
        {
            // FIXME target = collidable.getFeature(Trackable.class);
        }
    }

    @Override
    public void notifyTileCollided(CollisionResult result, CollisionCategory category)
    {
        if (result.startWithX(CollisionName.STEEP)
            && !result.endWithY(CollisionName.GROUND)
            && category.getAxis() == Axis.X
            && (result.containsX(CollisionName.LEFT) && sh > 0 || result.containsX(CollisionName.RIGHT) && sh < 0))
        {
            transformable.teleportX(transformable.getX() - sh * 5);
            sh = -sh;
        }
        if (category.getAxis() == Axis.Y && result.containsY(CollisionName.HORIZONTAL))
        {
            stateHandler.changeState(StatePatrolCeil.class);
            transformable.teleportY(result.getY() - 1.0);
        }
    }

    @Override
    public void onReceived(Packet packet)
    {
        currentIndex = packet.readByteUnsigned();
        sh = packet.readFloat();
        sv = packet.readFloat();
        amplitude = packet.readInt();
        offset = packet.readInt();
        animOffset = packet.readInt();
        mirrorable.mirror(Mirror.values()[packet.readByteUnsigned()]);
        startX = packet.readFloat();
        startY = packet.readFloat();
        transformable.teleport(packet.readFloat(), packet.readFloat());

        rasterable.setAnimOffset(animOffset);
    }

    @Override
    public void recycle()
    {
        sh = 0.0;
        sv = 0.0;
        amplitude = 0;
        offset = 0;
        coll = false;
        proximity = 0;
        sight = 0;
        animOffset = 0;
        delay = 0;
        curve = false;
        skip = 0;

        currentIndex = -1;
        startX = 0.0;
        startY = 0.0;
        checker = UpdatableVoid.getInstance();
        enabled = true;
        first = true;
        idle = 0.0;
        skip = 0;
        stateHandler.changeState(StatePatrol.class);
        if (patrols.size() > 0)
        {
            loadNextPatrol();
        }
        tickSync.restart();
    }
}
