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

import java.util.ArrayList;
import java.util.List;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.Alterable;
import com.b3dgs.lionengine.game.Damages;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.InitConfig;

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
public final class Stats extends FeatureModel implements Recyclable
{
    private final List<StatsListener> listeners = new ArrayList<>();
    private final Alterable health = new Alterable(Constant.STATS_MAX_HEALTH);
    private final Alterable talisment = new Alterable(Constant.STATS_MAX_TALISMENT);
    private final Alterable life = new Alterable(Constant.STATS_MAX_LIFE);
    private final Damages damages = new Damages(1, 1);
    private final StatsConfig config;
    private int sword;
    private Boolean amulet;
    private int credits;
    private boolean win;

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
        health.increase(config.getHealth());
        if (talisment.isFull())
        {
            talisment.reset();
            health.setMax(UtilMath.clamp(health.getMax() + 1, 1, 8));
        }
        else
        {
            talisment.increase(config.getTalisment());
        }
        life.increase(config.getLife());
        if (config.isAmulet())
        {
            amulet = Boolean.TRUE;
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
    }

    /**
     * Apply damages.
     * 
     * @param damages The damages to apply.
     * @return <code>true</code> if empty health, <code>false</code> else.
     */
    public boolean applyDamages(int damages)
    {
        health.decrease(damages);
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
        return amulet;
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
