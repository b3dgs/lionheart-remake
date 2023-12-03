/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionheart.object;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.UtilConversion;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.FramesConfig;
import com.b3dgs.lionengine.game.OriginConfig;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.CameraTracker;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Layerable;
import com.b3dgs.lionengine.game.feature.LayerableModel;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Spawner;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.body.Body;
import com.b3dgs.lionengine.game.feature.body.BodyConfig;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.networkable.Networkable;
import com.b3dgs.lionengine.game.feature.networkable.NetworkedDevice;
import com.b3dgs.lionengine.game.feature.networkable.Syncable;
import com.b3dgs.lionengine.game.feature.state.State;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.geom.Coord;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionengine.helper.DeviceControllerConfig;
import com.b3dgs.lionengine.helper.EntityChecker;
import com.b3dgs.lionengine.helper.EntityModelHelper;
import com.b3dgs.lionengine.io.DeviceController;
import com.b3dgs.lionengine.io.DeviceControllerVoid;
import com.b3dgs.lionengine.io.FileReading;
import com.b3dgs.lionengine.io.FileWriting;
import com.b3dgs.lionengine.network.NetworkType;
import com.b3dgs.lionengine.network.Packet;
import com.b3dgs.lionheart.CheckpointHandler;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.EntityConfig;
import com.b3dgs.lionheart.GameConfig;
import com.b3dgs.lionheart.GameType;
import com.b3dgs.lionheart.object.feature.BossDragonflyHead;
import com.b3dgs.lionheart.object.feature.BulletBounceOnGround;
import com.b3dgs.lionheart.object.feature.Floater;
import com.b3dgs.lionheart.object.feature.Guard;
import com.b3dgs.lionheart.object.feature.Patrol;
import com.b3dgs.lionheart.object.feature.Trackable;
import com.b3dgs.lionheart.object.state.StateFall;
import com.b3dgs.lionheart.object.state.StateHurt;
import com.b3dgs.lionheart.object.state.StateIdle;
import com.b3dgs.lionheart.object.state.StateIdleAnimal;
import com.b3dgs.lionheart.object.state.StateIdleDragon;
import com.b3dgs.lionheart.object.state.StateJump;
import com.b3dgs.lionheart.object.state.StateLianaSlide;
import com.b3dgs.lionheart.object.state.StateSlide;
import com.b3dgs.lionheart.object.state.attack.StateAttackAnimal;
import com.b3dgs.lionheart.object.state.attack.StateAttackDragon;

/**
 * Entity model implementation.
 */
// CHECKSTYLE IGNORE LINE: FanOutComplexity
@FeatureInterface
public final class EntityModel extends EntityModelHelper implements Snapshotable, XmlLoader, XmlSaver,
                               Editable<ModelConfig>, Routine, Recyclable, Syncable
{
    private static final String NODE_ALWAYS_UPDATE = "alwaysUpdate";
    private static final int PREFIX = State.class.getSimpleName().length();

    private static final double DEFAULT_MOVEMENT_VELOCITY = 0.12;
    private static final double DEFAULT_MOVEMENT_SENSIBILITY = 0.1;
    private static final double DEFAULT_JUMP_VELOCITY = 0.22;
    private static final double DEFAULT_JUMP_SENSIBILITY = 0.1;

    /**
     * Get animation name from state class.
     * 
     * @param state The state class.
     * @return The animation name.
     */
    public static String getAnimationName(Class<? extends State> state)
    {
        return state.getSimpleName().substring(PREFIX).toLowerCase(Locale.ENGLISH);
    }

    private final MapTile map = services.get(MapTile.class);
    private final CheckpointHandler checkpoint = services.getOptional(CheckpointHandler.class).orElse(null);
    private final ClassLoader loader = services.getOptional(ClassLoader.class).orElse(getClass().getClassLoader());
    private final SourceResolutionProvider source = services.get(SourceResolutionProvider.class);
    private final Spawner spawner = services.get(Spawner.class);
    private final GameConfig game = services.get(GameConfig.class);
    private final boolean hasGravity = setup.hasNode(BodyConfig.NODE_BODY);

    private final Identifiable identifiable;
    private final Mirrorable mirrorable;
    private final Body body;
    private final Transformable transformable;
    private final EntityChecker checker;
    private final StateHandler state;
    private final Networkable networkable;

    private final Force movement = new Force();
    private final Force jump = new Force();
    private final Origin origin = OriginConfig.imports(setup);
    private final Boolean mirror = new ModelConfig(setup.getRoot()).getMirror().orElse(Boolean.FALSE);
    private final AtomicBoolean collideSword = new AtomicBoolean();
    private final int frames;

    private Camera camera = services.get(Camera.class);
    private ModelConfig config = new ModelConfig();
    private CameraTracker tracker;
    private boolean jumpOnHurt = true;
    private NetworkedDevice networkedDevice;
    private DeviceController deviceNetwork;
    private boolean ignoreGlue;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @param identifiable The identifiable feature.
     * @param mirrorable The mirrorable feature.
     * @param body The body feature.
     * @param transformable The transformable feature.
     * @param collidable The collidable feature.
     * @param checker The checker feature.
     * @param state The state feature.
     * @param networkable The networkable feature.
     * @throws LionEngineException If invalid arguments.
     */
    public EntityModel(Services services,
                       Setup setup,
                       Identifiable identifiable,
                       Mirrorable mirrorable,
                       Body body,
                       Transformable transformable,
                       Collidable collidable,
                       EntityChecker checker,
                       StateHandler state,
                       Networkable networkable)
    {
        super(services, setup);

        this.identifiable = identifiable;
        this.mirrorable = mirrorable;
        this.body = body;
        this.transformable = transformable;
        this.checker = checker;
        this.state = state;
        this.networkable = networkable;

        final FramesConfig config = FramesConfig.imports(setup);
        frames = config.getHorizontal() * config.getVertical();

        if (setup.hasNode(ModelConfig.NODE_MODEL))
        {
            this.config = new ModelConfig(setup.getRoot());
        }

        collidable.setCollisionVisibility(Constant.DEBUG_COLLISIONS);

        state.addListener(this::syncState);
    }

    @Override
    public void save(FileWriting file) throws IOException
    {
        file.writeDouble(transformable.getX());
        file.writeDouble(transformable.getY());
        file.writeDouble(movement.getDirectionHorizontal());
        file.writeDouble(movement.getDirectionVertical());
        file.writeDouble(jump.getDirectionHorizontal());
        file.writeDouble(jump.getDirectionVertical());
    }

    @Override
    public void load(FileReading file) throws IOException
    {
        transformable.teleport(file.readDouble(), file.readDouble());
        movement.setDirection(file.readDouble(), file.readDouble());
        jump.setDirection(file.readDouble(), file.readDouble());
    }

    private static final int TYPE_CONTROL = 0;
    private static final int TYPE_STATE = TYPE_CONTROL + 1;
    private static final int TYPE_STOP = TYPE_STATE + 1;

    private void syncState(Class<? extends State> old, Class<? extends State> next)
    {
        if (networkable.isServerHandleClient()
            && !next.equals(old)
            && !next.equals(StateJump.class)
            && !next.equals(StateFall.class))
        {
            final String str = next.getName();
            final ByteBuffer buffer = StandardCharsets.UTF_8.encode(str);
            final ByteBuffer data = ByteBuffer.allocate(Integer.BYTES + Float.BYTES * 2 + 2 + buffer.capacity());
            data.putInt(getSyncId());
            data.put(UtilConversion.fromUnsignedByte(TYPE_STATE));
            data.putFloat((float) transformable.getX());
            data.putFloat((float) transformable.getY());
            // data.putFloat((float) body.getDirectionVertical());
            // data.putFloat((float) movement.getDirectionHorizontal());
            // data.putFloat((float) jump.getDirectionVertical());
            // data.put(UtilConversion.fromUnsignedByte(str.length()));
            data.put(buffer);
            networkable.send(data);
        }
    }

    /**
     * Give player control.
     */
    public void giveClientControl()
    {
        if (networkable.isServerHandleClient())
        {
            final ByteBuffer data = ByteBuffer.allocate(Integer.BYTES + 1);
            data.putInt(getSyncId());
            data.put(UtilConversion.fromUnsignedByte(TYPE_CONTROL));
            networkable.send(data);
        }
        else if (networkable.isClient())
        {
            setInput(services.get(DeviceController.class));
            networkedDevice.set(services.get(DeviceController.class));

            tracker.addFeature(new LayerableModel(getFeature(Layerable.class).getLayerRefresh().intValue() + 1));
            tracker.setOffset(0, getFeature(Transformable.class).getHeight() / 2 + 8);
            tracker.track(this);
        }
    }

    /**
     * Remove control.
     */
    public void removeControl()
    {
        setInput(DeviceControllerVoid.getInstance());
        movement.zero();
        jump.zero();
        state.changeState(StateIdle.class);

        if (deviceNetwork != null)
        {
            networkedDevice.remove(services.get(DeviceController.class));

            if (game.getNetwork().get().is(NetworkType.SERVER))
            {
                final ByteBuffer data = ByteBuffer.allocate(Integer.BYTES + 1);
                data.putInt(getSyncId());
                data.put(UtilConversion.fromUnsignedByte(TYPE_STOP));
                networkable.send(data);
            }
        }
    }

    @Override
    public void onConnected()
    {
        if (networkedDevice != null)
        {
            if (!networkable.isClient())
            {
                final Services s = new Services();
                s.add(networkedDevice.getVirtual());
                deviceNetwork = DeviceControllerConfig.create(s, Medias.create("input_network.xml"));
                setInput(deviceNetwork);
            }
        }
    }

    /**
     * Set camera used.
     * 
     * @param camera The camera used.
     */
    public void setCamera(Camera camera)
    {
        this.camera = camera;
    }

    /**
     * Set camera tracker.
     * 
     * @param tracker The tracker reference.
     */
    public void setTracker(CameraTracker tracker)
    {
        this.tracker = tracker;
    }

    /**
     * Set the next stage.
     * 
     * @param next The next stage.
     * @param nextSpawn The next spawn.
     */
    public void setNext(Optional<String> next, Optional<Coord> nextSpawn)
    {
        config = new ModelConfig(false, true, next, nextSpawn);
    }

    /**
     * Update mirror depending of current mirror and movement.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateMirror(double extrp)
    {
        if (!hasFeature(Patrol.class)
            && !hasFeature(BulletBounceOnGround.class)
            && !hasFeature(Guard.class)
            && !hasFeature(Floater.class)
            && !hasFeature(BossDragonflyHead.class)
            && !state.isState(StateHurt.class)
            && !state.isState(StateSlide.class)
            && !state.isState(StateLianaSlide.class)
            && !state.isState(StateIdleAnimal.class)
            && !state.isState(StateAttackAnimal.class)
            && !state.isState(StateIdleDragon.class)
            && !state.isState(StateAttackDragon.class))
        {
            if (mirrorable.is(Mirror.NONE) && movement.getDirectionHorizontal() < 0.0)
            {
                mirrorable.mirror(Mirror.HORIZONTAL);
            }
            else if (mirrorable.is(Mirror.HORIZONTAL) && movement.getDirectionHorizontal() > 0.0)
            {
                mirrorable.mirror(Mirror.NONE);
            }
        }
        mirrorable.update(extrp);
    }

    @Override
    public void update(double extrp)
    {
        if (deviceNetwork != null)
        {
            deviceNetwork.update(extrp);
        }

        jump.update(extrp);
        movement.update(extrp);
        transformable.moveLocation(extrp, body, movement, jump);
        updateMirror(extrp);

        if (transformable.getX() < -source.getWidth()
            || transformable.getX() > map.getWidth() + source.getWidth()
            || transformable.getY() < -source.getHeight()
            || transformable.getY() > map.getHeight() + source.getHeight())
        {
            identifiable.destroy();
        }
    }

    /**
     * Perform jump on hit.
     */
    public void jumpHit()
    {
        final double vy = UtilMath.clamp(Math.abs(body.getDirectionVertical() * 0.65),
                                         Constant.JUMP_MIN,
                                         Constant.JUMP_HIT);
        jump.setDirection(new Force(0, vy));
        jump.setDirectionMaximum(new Force(0, vy));

        body.resetGravity();
        collideSword.set(true);
    }

    /**
     * Get collide sword.
     * 
     * @return The collide sword.
     */
    public boolean getCollideSword()
    {
        return collideSword.get();
    }

    /**
     * Reset collide sword.
     */
    public void resetCollideSword()
    {
        collideSword.set(false);
    }

    /**
     * Set jump on hurt flag.
     * 
     * @param jumpOnHurt <code>true</code> to enable, <code>false</code> else.
     */
    public void setJumpOnHurt(boolean jumpOnHurt)
    {
        this.jumpOnHurt = jumpOnHurt;
    }

    /**
     * Set ignore glue flag.
     * 
     * @param ignoreGlue The ignore glue flag.
     */
    public void setIgnoreGlue(boolean ignoreGlue)
    {
        this.ignoreGlue = ignoreGlue;
    }

    /**
     * Check if ignore glue.
     * 
     * @return <code>true</code> if ignore glue, <code>false</code> else.
     */
    public boolean isIgnoreGlue()
    {
        return ignoreGlue;
    }

    /**
     * Get the camera reference.
     * 
     * @return The camera reference.
     */
    public Camera getCamera()
    {
        return camera;
    }

    /**
     * Get the map reference.
     * 
     * @return The map reference.
     */
    public MapTile getMap()
    {
        return map;
    }

    /**
     * Get the checkpoint reference.
     * 
     * @return The checkpoint reference.
     */
    public CheckpointHandler getCheckpoint()
    {
        return checkpoint;
    }

    /**
     * Get the spawner reference.
     * 
     * @return The spawner reference.
     */
    public Spawner getSpawner()
    {
        return spawner;
    }

    /**
     * Get the services reference.
     * 
     * @return The services reference.
     */
    public Services getServices()
    {
        return services;
    }

    /**
     * Get the camera tracker reference.
     * 
     * @return The camera tracker reference.
     */
    public CameraTracker getTracker()
    {
        return tracker;
    }

    /**
     * Get the movement force.
     * 
     * @return The movement force.
     */
    public Force getMovement()
    {
        return movement;
    }

    /**
     * Get the jump force.
     * 
     * @return The jump force.
     */
    public Force getJump()
    {
        return jump;
    }

    /**
     * Check if has gravity.
     * 
     * @return <code>true</code> if has gravity, <code>false</code> else.
     */
    public boolean hasGravity()
    {
        return hasGravity;
    }

    /**
     * Get the frames number.
     * 
     * @return The frames number.
     */
    public int getFrames()
    {
        return frames;
    }

    /**
     * Check if jump on hurt is enabled.
     * 
     * @return <code>true</code> if enabled, <code>false</code> else.
     */
    public boolean getJumpOnHurt()
    {
        return jumpOnHurt;
    }

    @Override
    public ModelConfig getConfig()
    {
        return config;
    }

    @Override
    public void setConfig(ModelConfig config)
    {
        this.config = config;
    }

    @Override
    public void load(XmlReader root)
    {
        if (root.hasNode(ModelConfig.NODE_MODEL))
        {
            config = new ModelConfig(root);
        }
        if (root.hasNode(BodyConfig.NODE_BODY))
        {
            final BodyConfig c = BodyConfig.imports(root);
            body.setGravity(c.getGravity());
            body.setGravityMax(c.getGravityMax());
        }
        mirrorable.mirror(config.getMirror().orElse(mirror).booleanValue() ? Mirror.HORIZONTAL : Mirror.NONE);
        mirrorable.update(1.0);
    }

    @Override
    public void save(Xml root)
    {
        root.writeString(EntityConfig.ATT_FILE, setup.getMedia().getPath());

        root.writeDouble(EntityConfig.ATT_TX,
                         (transformable.getX() - origin.getX(0.0, transformable.getWidth())) / map.getTileWidth() - 1);
        root.writeDouble(EntityConfig.ATT_TY,
                         transformable.getY() / map.getTileHeight() + map.getInTileHeight(transformable) - 1);

        config.save(root);
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        if (hasFeature(NetworkedDevice.class))
        {
            networkedDevice = getFeature(NetworkedDevice.class);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onReceived(Packet packet)
    {
        final ByteBuffer buffer = packet.buffer();

        final int type = packet.readByteUnsigned();
        if (type == TYPE_CONTROL)
        {
            giveClientControl();
        }
        else if (type == TYPE_STATE)
        {
            transformable.teleport(buffer.getFloat(), buffer.getFloat());
            // body.setForce(buffer.getFloat());
            // movement.setDirection(buffer.getFloat(), movement.getDirectionVertical());
            // jump.setDirection(jump.getDirectionHorizontal(), buffer.getFloat());

            // final byte[] str = new byte[UtilConversion.toUnsignedByte(buffer.get())];
            // buffer.get(str);
            // final String name = StandardCharsets.UTF_8.decode(ByteBuffer.wrap(str)).toString();
            // try
            // {
            // state.changeState((Class<? extends State>) loader.loadClass(name));
            // state.postUpdate();
            // }
            // catch (final ClassNotFoundException exception)
            // {
            // Verbose.exception(exception);
            // }
        }
        else if (type == TYPE_STOP)
        {
            removeControl();
        }
    }

    @Override
    public void recycle()
    {
        if (services.getOptional(Trackable.class).isPresent())
        {
            if (game.getType().is(GameType.STORY, GameType.TRAINING))
            {
                final boolean alwaysUpdate = Boolean.parseBoolean(setup.getTextDefault("false", NODE_ALWAYS_UPDATE));
                checker.setCheckerUpdate(() -> alwaysUpdate
                                               || camera.isViewable(transformable, 0, transformable.getHeight()));
            }
            checker.setCheckerRender(() -> camera.isViewable(transformable, 0, transformable.getHeight() * 2));
        }

        movement.setVelocity(DEFAULT_MOVEMENT_VELOCITY);
        movement.setSensibility(DEFAULT_MOVEMENT_SENSIBILITY);
        movement.zero();

        jump.setVelocity(DEFAULT_JUMP_VELOCITY);
        jump.setSensibility(DEFAULT_JUMP_SENSIBILITY);
        jump.setDestination(0.0, 0.0);
        jump.zero();
    }
}
