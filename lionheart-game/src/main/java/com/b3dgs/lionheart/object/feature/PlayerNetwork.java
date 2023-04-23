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
package com.b3dgs.lionheart.object.feature;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.UtilConversion;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.networkable.Networkable;
import com.b3dgs.lionengine.game.feature.networkable.Syncable;
import com.b3dgs.lionengine.geom.Coord;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.SpriteDigit;
import com.b3dgs.lionengine.graphic.drawable.SpriteFont;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionengine.io.DeviceController;
import com.b3dgs.lionengine.network.Packet;
import com.b3dgs.lionheart.Checkpoint;
import com.b3dgs.lionheart.CheckpointHandler;
import com.b3dgs.lionheart.CheckpointListener;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.landscape.ForegroundWater;
import com.b3dgs.lionheart.object.EntityModel;

/**
 * Player network data.
 */
@FeatureInterface
public class PlayerNetwork extends FeatureModel implements Routine, Syncable, Recyclable
{
    private static final int TIME_START_DELAY = 1000;
    private static final int TIMING_SYNC_DELAY_MS = 1000;
    private static final int TIMING_SYNC_WATER_DELAY_MS = 1000;

    private static final int TYPE_READY = 0;
    private static final int TYPE_STARTED = TYPE_READY + 1;
    private static final int TYPE_TIME = TYPE_STARTED + 1;
    private static final int TYPE_REACH = TYPE_TIME + 1;
    private static final int TYPE_WATER = TYPE_REACH + 1;

    private static final int TIME_X = 0;
    private static final int TIME_Y = 0;

    private static final String IMG_NUMBERS = "numbers.png";

    private final ImageBuffer number = Graphics.getImageBuffer(Medias.create(Folder.SPRITE, IMG_NUMBERS));
    private final SpriteDigit numberTime = Drawable.loadSpriteDigit(number, 8, 16, 8);
    private final SpriteFont font = Drawable.loadSpriteFont(Medias.create(Folder.SPRITE, "font.png"),
                                                            Medias.create(Folder.SPRITE, "fontdata.xml"),
                                                            12,
                                                            12);
    private final Tick time = new Tick();
    private final Timing timeSync = new Timing();
    private final Timing timeWater = new Timing();
    private final Map<Integer, Boolean> clientsReady = new HashMap<>();
    private final Map<Integer, Integer> reachTime = new TreeMap<>();

    private final SourceResolutionProvider source = services.get(SourceResolutionProvider.class);
    private final DeviceController device = services.get(DeviceController.class);
    private final CheckpointHandler checkpoint = services.get(CheckpointHandler.class);
    private final Map<Integer, String> clients = services.get(ConcurrentHashMap.class);

    @FeatureGet private Networkable networkable;

    private boolean ready;
    private boolean started;
    private EntityModel model;

    /**
     * Create feature.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public PlayerNetwork(Services services, Setup setup)
    {
        super(services, setup);

        number.prepare();
        numberTime.prepare();

        font.load();
        font.prepare();
    }

    /**
     * Set associated player model.
     * 
     * @param model The model reference.
     */
    public void setModel(EntityModel model)
    {
        this.model = model;
    }

    /**
     * Set the ready flag. Called by client to allow start when all ready.
     */
    private void setReady()
    {
        ready = true;

        final ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES + 1);
        buffer.putInt(getSyncId());
        buffer.put(UtilConversion.fromUnsignedByte(TYPE_READY));
        networkable.send(buffer);
    }

    /**
     * Set the started flag. Called by server to trigger start on all ready.
     */
    private void setStarted()
    {
        started = true;

        final ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES + 1);
        buffer.putInt(getSyncId());
        buffer.put(UtilConversion.fromUnsignedByte(TYPE_STARTED));
        networkable.send(buffer);

        time.restart();
        timeSync.restart();
    }

    private void syncTime()
    {
        final ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES * 2 + 1);
        buffer.putInt(getSyncId());
        buffer.put(UtilConversion.fromUnsignedByte(TYPE_TIME));
        buffer.putInt((int) time.elapsed());
        networkable.send(buffer);
    }

    private void syncReach(Transformable transformable)
    {
        final Integer id = transformable.getFeature(Networkable.class).getClientId();
        if (!reachTime.containsKey(id))
        {
            reachTime.put(id, Integer.valueOf((int) time.elapsed()));

            final ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES * 3 + 1);
            buffer.putInt(getSyncId());
            buffer.put(UtilConversion.fromUnsignedByte(TYPE_REACH));
            buffer.putInt(id.intValue());
            buffer.putInt(reachTime.get(id).intValue());
            networkable.send(buffer);
        }
    }

    private void syncWater()
    {
        final ForegroundWater water = services.getOptional(ForegroundWater.class).orElse(null);
        if (water != null)
        {
            final ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES + Double.BYTES + 1);
            buffer.putInt(getSyncId());
            buffer.put(UtilConversion.fromUnsignedByte(TYPE_WATER));
            buffer.putDouble(water.getHeight());
            networkable.send(buffer);
        }
    }

    private void updateNumberTime()
    {
        numberTime.setLocation(TIME_X, TIME_Y);
        if (started)
        {
            numberTime.setValue(time.elapsedTime(source.getRate()));
        }
        else
        {
            numberTime.setValue(Math.max(0, TIME_START_DELAY - time.elapsedTime(source.getRate())));
        }
    }

    private boolean isAllReady()
    {
        for (final Integer id : clients.keySet())
        {
            if (!Boolean.TRUE.equals(clientsReady.get(id)))
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        checkpoint.addListener(new CheckpointListener()
        {
            @Override
            public void notifyReachCheckpoint(Transformable player, Checkpoint checkpoint, int index)
            {
                syncReach(player);
            }

            @Override
            public void notifyReachStage(String next, Optional<Coord> spawn)
            {
                // Nothing to do
            }

            @Override
            public void notifyReachBoss(double x, double y)
            {
                // Nothing to do
            }
        });
    }

    @Override
    public void update(double extrp)
    {
        if (networkable.isServer())
        {
            time.update(extrp);
            updateNumberTime();

            if (!started && time.elapsedTime(source.getRate(), TIME_START_DELAY))
            {
                setStarted();
            }
            if (timeSync.elapsed(TIMING_SYNC_DELAY_MS))
            {
                syncTime();
                timeSync.restart();
            }

            if (timeWater.elapsed(TIMING_SYNC_WATER_DELAY_MS))
            {
                syncWater();
                timeWater.restart();
            }
        }
        else if (networkable.isClient())
        {
            time.update(extrp);
            updateNumberTime();

            if (!ready && device.isFired())
            {
                setReady();
            }
        }
    }

    @Override
    public void render(Graphic g)
    {
        if (reachTime.containsKey(networkable.getClientId()))
        {
            int i = 0;
            for (final Entry<Integer, Integer> client : reachTime.entrySet())
            {
                final int y = i * numberTime.getHeight();
                font.draw(g, 0, y, Align.LEFT, clients.get(client.getKey()));
                numberTime.setLocation(50, y);

                final double frameTime = Constant.ONE_SECOND_IN_MILLI / source.getRate();
                numberTime.setValue((int) StrictMath.floor(client.getValue().intValue() * frameTime));
                numberTime.render(g);
                i++;
            }
        }
        else
        {
            numberTime.render(g);
        }
    }

    @Override
    public void onConnected()
    {
        if (networkable.isClient())
        {
            services.add(this);
        }
    }

    @Override
    public void onReceived(Packet packet)
    {
        final int type = packet.readByteUnsigned();
        if (type == TYPE_READY)
        {
            clientsReady.put(packet.getClientSourceId(), Boolean.TRUE);
            if (networkable.isServer() && isAllReady())
            {
                time.start();
                timeSync.start();
            }
        }
        else if (type == TYPE_STARTED)
        {
            started = true;
            time.restart();
            timeSync.restart();
            model.giveClientControl();
        }
        else if (type == TYPE_TIME)
        {
            if (!reachTime.containsKey(packet.getClientId()))
            {
                time.set(packet.readInt());
            }
        }
        else if (type == TYPE_REACH)
        {
            final Integer id = Integer.valueOf(packet.readInt());
            final int reach = packet.readInt();
            if (id.equals(networkable.getClientId()))
            {
                time.stop();
                time.set(reach);
            }
            reachTime.putIfAbsent(id, Integer.valueOf(reach));
        }
        else if (type == TYPE_WATER)
        {
            services.get(ForegroundWater.class).setHeight(packet.readDouble());
        }
    }

    @Override
    public void recycle()
    {
        ready = false;
        started = false;
        time.stop();
        timeSync.stop();
        timeWater.restart();
    }
}
