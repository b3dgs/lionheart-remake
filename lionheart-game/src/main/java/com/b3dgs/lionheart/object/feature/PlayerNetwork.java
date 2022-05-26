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
package com.b3dgs.lionheart.object.feature;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
import com.b3dgs.lionengine.game.feature.networkable.Networkable;
import com.b3dgs.lionengine.game.feature.networkable.Syncable;
import com.b3dgs.lionengine.geom.Coord;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.SpriteDigit;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionengine.io.DeviceController;
import com.b3dgs.lionengine.network.Packet;
import com.b3dgs.lionheart.Checkpoint;
import com.b3dgs.lionheart.CheckpointHandler;
import com.b3dgs.lionheart.CheckpointListener;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.object.EntityModel;

/**
 * Player network data.
 */
@FeatureInterface
public class PlayerNetwork extends FeatureModel implements Routine, Syncable, Recyclable
{
    private static final int TIME_START_DELAY = 2000;
    private static final int TIMING_SYNC_DELAY_MS = 1000;

    private static final int TYPE_READY = 0;
    private static final int TYPE_STARTED = TYPE_READY + 1;
    private static final int TYPE_TIME = TYPE_STARTED + 1;
    private static final int TYPE_REACH = TYPE_TIME + 1;

    private static final int TIME_X = 0;
    private static final int TIME_Y = 0;

    private static final String IMG_NUMBERS = "numbers.png";

    private final ImageBuffer number = Graphics.getImageBuffer(Medias.create(Folder.SPRITE, IMG_NUMBERS));
    private final SpriteDigit numberTime = Drawable.loadSpriteDigit(number, 8, 16, 8);
    private final Tick time = new Tick();
    private final Timing timeSync = new Timing();
    private final Map<Integer, Double> reachTime = new HashMap<>();

    private final SourceResolutionProvider source = services.get(SourceResolutionProvider.class);
    private final DeviceController device = services.get(DeviceController.class);
    private final CheckpointHandler checkpoint = services.get(CheckpointHandler.class);

    @FeatureGet private Networkable networkable;
    @FeatureGet private EntityModel model;

    private boolean ready;
    private boolean started;

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
        numberTime.setLocation(TIME_X, TIME_Y);
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

    private void syncReach()
    {
        final ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES + 1 + Double.BYTES);
        buffer.putInt(getSyncId());
        buffer.put(UtilConversion.fromUnsignedByte(TYPE_REACH));
        buffer.putDouble(time.elapsed());
        networkable.send(buffer);
    }

    private void updateNumberTime()
    {
        if (started)
        {
            numberTime.setValue(time.elapsedTime(source.getRate()));
        }
        else
        {
            numberTime.setValue(Math.max(0, TIME_START_DELAY - time.elapsedTime(source.getRate())));
        }
    }

    @Override
    public void update(double extrp)
    {
        time.update(extrp);
        updateNumberTime();

        if (networkable.isServerHandleClient())
        {
            if (!started && time.elapsedTime(source.getRate(), TIME_START_DELAY))
            {
                setStarted();
            }
            if (timeSync.elapsed(TIMING_SYNC_DELAY_MS))
            {
                syncTime();
                timeSync.restart();
            }
        }
        else if (networkable.isClient())
        {
            if (!ready && device.isFired())
            {
                setReady();
            }
        }
    }

    @Override
    public void render(Graphic g)
    {
        numberTime.render(g);
    }

    @Override
    public void onReceived(Packet packet)
    {
        final int type = packet.readByteUnsigned();
        if (type == TYPE_READY)
        {
            // TODO ensures everybody is ready
            time.start();
            timeSync.start();
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
            final double elapsed = time.elapsed();
            reachTime.putIfAbsent(packet.getClientId(), Double.valueOf(elapsed));

            if (packet.getClientId().equals(networkable.getClientId()))
            {
                time.stop();
                time.set(packet.readDouble());
                model.removeClientControl();
            }
        }
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        if (networkable.isServerHandleClient())
        {
            checkpoint.addListener(new CheckpointListener()
            {
                @Override
                public void notifyReachCheckpoint(Checkpoint checkpoint)
                {
                    syncReach();
                    model.removeClientControl();
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
    }

    @Override
    public void recycle()
    {
        ready = false;
        started = false;
        time.stop();
        timeSync.stop();
    }
}
