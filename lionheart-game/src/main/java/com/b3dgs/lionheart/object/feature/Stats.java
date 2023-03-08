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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilConversion;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.Alterable;
import com.b3dgs.lionengine.game.Damages;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.networkable.Networkable;
import com.b3dgs.lionengine.game.feature.networkable.Syncable;
import com.b3dgs.lionengine.io.FileReading;
import com.b3dgs.lionengine.io.FileWriting;
import com.b3dgs.lionengine.network.Packet;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.InitConfig;
import com.b3dgs.lionheart.Sfx;
import com.b3dgs.lionheart.object.Snapshotable;

/**
 * Stats feature implementation.
 * <ol>
 * <li>Player health, count before death.</li>
 * <li>Talisment, count until next sword level.</li>
 * <li>Life, count before end.</li>
 * <li>Damages, applied on monster hit.</li>
 * </ol>
 */
@FeatureInterface
public final class Stats extends FeatureModel implements Snapshotable, Recyclable, Syncable
{
    private final List<StatsListener> listeners = new ArrayList<>();
    private final Alterable health = new Alterable(Constant.STATS_MAX_HEALTH);
    private final Alterable talisment = new Alterable(Constant.STATS_MAX_TALISMENT);
    private final Alterable life = new Alterable(Constant.STATS_MAX_LIFE);
    private final Damages damages = new Damages(1, 1);
    private final StatsConfig config;
    private int sword;
    private boolean amulet;
    private int credits;
    private boolean win;

    @FeatureGet private Networkable networkable;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Stats(Services services, Setup setup)
    {
        super(services, setup);

        config = StatsConfig.imports(setup);
    }

    /**
     * Init Lionhard difficulty.
     */
    public void initLionhard()
    {
        health.setMax(health.getMax() * 2);
        health.fill();
    }

    /**
     * Add stats listener.
     * 
     * @param listener The listener reference.
     */
    public void addListener(StatsListener listener)
    {
        listeners.add(listener);
    }

    /**
     * Apply init.
     * 
     * @param config The init config to apply.
     */
    public void apply(InitConfig config)
    {
        health.setMax(config.getHealthMax());
        health.fill();
        talisment.set(config.getTalisment());
        life.set(config.getLife());
        sword = config.getSword();
        damages.setDamages(sword + 1, sword + 1);
        amulet = config.isAmulet();
        final int n = listeners.size();
        for (int i = 0; i < n; i++)
        {
            listeners.get(i).notifyNextSword(sword);
        }
        credits = config.getCredits();
    }

    /**
     * Apply config.
     * 
     * @param config The config to apply.
     */
    public void apply(TakeableConfig config)
    {
        if (!networkable.isClient())
        {
            health.increase(config.getHealth());
            if (talisment.isFull())
            {
                talisment.reset();
                health.setMax(UtilMath.clamp(health.getMax() + 1, 1, Constant.STATS_MAX_HEART));
            }
            else
            {
                talisment.increase(config.getTalisment());
            }
            life.increase(config.getLife());
            if (config.isAmulet())
            {
                amulet = true;
            }

            final int nextSword = config.getSword();
            if (nextSword > 0 && sword != nextSword)
            {
                sword = nextSword;
                damages.setDamages(sword + 1, sword + 1);

                final int n = listeners.size();
                for (int i = 0; i < n; i++)
                {
                    listeners.get(i).notifyNextSword(sword);
                }
            }
            syncApply(config);
        }
    }

    /**
     * Apply damages.
     * 
     * @param damages The damages to apply.
     * @return <code>true</code> if empty health, <code>false</code> else.
     */
    public boolean applyDamages(int damages)
    {
        if (networkable.isClient())
        {
            return false;
        }
        applyDamagesInternal(damages);
        syncHurt(damages);
        return health.isEmpty();
    }

    /**
     * Set win.
     */
    public void win()
    {
        win = true;
    }

    /**
     * Add one heart.
     */
    public void increaseMaxHealth()
    {
        health.setMax(Math.min(health.getMax() + 1, Constant.STATS_MAX_HEART));
    }

    /**
     * Remove one heart.
     */
    public void decreaseMaxHealth()
    {
        health.setMax(Math.max(1, health.getMax() - 1));
    }

    /**
     * Remove one life.
     */
    public void decreaseLife()
    {
        life.decrease(1);
    }

    /**
     * Fill health to max value.
     */
    public void fillHealth()
    {
        health.fill();
    }

    /**
     * Get the current health.
     * 
     * @return The current health.
     */
    public int getHealth()
    {
        return health.getCurrent();
    }

    /**
     * Get the max health.
     * 
     * @return The max health.
     */
    public int getHealthMax()
    {
        return health.getMax();
    }

    /**
     * Get the current talisment.
     * 
     * @return The current talisment.
     */
    public int getTalisment()
    {
        return talisment.getCurrent();
    }

    /**
     * Get the current life.
     * 
     * @return The current life.
     */
    public int getLife()
    {
        return life.getCurrent();
    }

    /**
     * Get random damages.
     * 
     * @return The random damages.
     */
    public int getDamages()
    {
        return damages.getRandom();
    }

    /**
     * Get the sword level.
     * 
     * @return The sword level.
     */
    public int getSword()
    {
        return sword;
    }

    /**
     * Get the credits count.
     * 
     * @return The credits count.
     */
    public int getCredits()
    {
        return credits;
    }

    /**
     * Check if has amulet.
     * 
     * @return <code>true</code> if has amulet, <code>false</code> else.
     */
    public Boolean hasAmulet()
    {
        return Boolean.valueOf(amulet);
    }

    /**
     * Check if won.
     * 
     * @return <code>true</code> if won, <code>false</code> else.
     */
    public boolean hasWin()
    {
        return win;
    }

    /**
     * Apply damages.
     * 
     * @param damages The damages to apply.
     */
    private void applyDamagesInternal(int damages)
    {
        if (health.decrease(damages) > 0 && health.isEmpty())
        {
            final int n = listeners.size();
            for (int i = 0; i < n; i++)
            {
                listeners.get(i).notifyDead();
            }
        }
    }

    private void syncApply(TakeableConfig config)
    {
        if (networkable.isConnected())
        {
            final String str = config.getEffect().getPath();
            final ByteBuffer buffer = StandardCharsets.UTF_8.encode(str);
            final ByteBuffer data = ByteBuffer.allocate(Integer.BYTES + buffer.capacity() + 8);
            data.putInt(getSyncId());
            data.put(UtilConversion.fromUnsignedByte(0));
            data.put(UtilConversion.fromUnsignedByte(str.length()));
            data.put(buffer);
            data.put(UtilConversion.fromUnsignedByte(config.getSfx().ordinal()));
            data.put(UtilConversion.fromUnsignedByte(config.getHealth()));
            data.put(UtilConversion.fromUnsignedByte(config.getTalisment()));
            data.put(UtilConversion.fromUnsignedByte(config.getLife()));
            data.put(UtilConversion.fromUnsignedByte(config.getSword()));
            data.put(UtilConversion.fromUnsignedByte(UtilConversion.boolToInt(config.isAmulet())));
            networkable.send(data);
        }
    }

    private void syncHurt(int damages)
    {
        if (networkable.isConnected())
        {
            final ByteBuffer data = ByteBuffer.allocate(Integer.BYTES + 2);
            data.putInt(getSyncId());
            data.put(UtilConversion.fromUnsignedByte(1));
            data.put(UtilConversion.fromUnsignedByte(damages));
            networkable.send(data);
        }
    }

    @Override
    public void save(FileWriting file) throws IOException
    {
        file.writeInteger(health.getMax());
        file.writeInteger(health.getCurrent());
        file.writeInteger(talisment.getCurrent());
        file.writeInteger(life.getCurrent());
        file.writeInteger(sword);
        file.writeBoolean(amulet);
        file.writeInteger(credits);
        file.writeBoolean(win);
    }

    @Override
    public void load(FileReading file) throws IOException
    {
        health.setMax(file.readInteger());
        health.set(file.readInteger());
        talisment.set(file.readInteger());
        life.set(file.readInteger());
        sword = file.readInteger();
        amulet = file.readBoolean();
        credits = file.readInteger();
        win = file.readBoolean();

        damages.setDamages(sword + 1, sword + 1);
    }

    @Override
    public void onReceived(Packet packet)
    {
        final int type = packet.readByteUnsigned();
        if (type == 0)
        {
            final TakeableConfig config = new TakeableConfig(packet.readMedia(),
                                                             Sfx.values()[packet.readByteUnsigned()].name(),
                                                             packet.readByteUnsigned(),
                                                             packet.readByteUnsigned(),
                                                             packet.readByteUnsigned(),
                                                             packet.readByteUnsigned(),
                                                             packet.readBool());
            apply(config);
        }
        else if (type == 1)
        {
            applyDamagesInternal(packet.readByteUnsigned());
        }
    }

    @Override
    public void recycle()
    {
        health.setMax(config.getHealth());
        health.fill();
        life.set(config.getLife());
        damages.setDamages(config.getDamages(), config.getDamages());
        sword = 0;
        win = false;
    }
}
