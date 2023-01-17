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
package com.b3dgs.lionheart;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import com.b3dgs.lionengine.SplitType;
import com.b3dgs.lionengine.network.Network;

/**
 * Game data configuration.
 */
public class GameConfig
{
    private final int players;
    private final GameType type;
    private final Optional<Network> network;
    private final Map<Integer, Integer> controls;
    private final InitConfig init;

    /**
     * Create first configuration.
     */
    public GameConfig()
    {
        this(1, GameType.ORIGINAL, Optional.empty(), Collections.emptyMap(), null);
    }

    /**
     * Create configuration.
     * 
     * @param players The players number.
     * @param game The game type.
     * @param network The network configuration if online, absent if local.
     */
    public GameConfig(int players, GameType game, Optional<Network> network)
    {
        super();

        this.players = players;
        type = game;
        this.network = network;
        controls = Collections.emptyMap();
        init = null;
    }

    /**
     * Create configuration.
     * 
     * @param players The players number.
     * @param game The game type.
     * @param network The network configuration if online, absent if local.
     * @param controls The player id as key, the control index as value.
     * @param init The init configuration.
     */
    public GameConfig(int players,
                      GameType game,
                      Optional<Network> network,
                      Map<Integer, Integer> controls,
                      InitConfig init)
    {
        super();

        this.players = players;
        type = game;
        this.network = network;
        this.controls = controls;
        this.init = init;
    }

    /**
     * Create with init config using existing configuration.
     * 
     * @param init The init config.
     * @return The new configuration.
     */
    public GameConfig with(InitConfig init)
    {
        return new GameConfig(players, type, network, controls, init);
    }

    /**
     * Get the players number.
     * 
     * @return The players number.
     */
    public int getPlayers()
    {
        return players;
    }

    /**
     * Get the game type.
     * 
     * @return The game type.
     */
    public GameType getType()
    {
        return type;
    }

    /**
     * Get the network configuration.
     * 
     * @return The network configuration.
     */
    public Optional<Network> getNetwork()
    {
        return network;
    }

    /**
     * Get player control index.
     * 
     * @param player The player id.
     * @return The control index.
     */
    public int getControl(int player)
    {
        return controls.get(Integer.valueOf(player)).intValue();
    }

    /**
     * Get the init stage configuration.
     * 
     * @return The init stage configuration.
     */
    public InitConfig getInit()
    {
        return init;
    }

    /**
     * Get split type.
     * 
     * @return The split type.
     */
    public SplitType getSplit()
    {
        final SplitType type;
        if (players == 1 || network.isPresent())
        {
            type = SplitType.NONE;
        }
        else if (players == 2)
        {
            type = SplitType.TWO_HORIZONTAL;
        }
        else
        {
            type = SplitType.FOUR;
        }
        return type;
    }
}
