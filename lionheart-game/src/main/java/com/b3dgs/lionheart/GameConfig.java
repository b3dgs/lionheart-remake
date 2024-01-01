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
    private final GameType type;
    private final int players;
    private final Optional<Network> network;
    private final Optional<String> stages;
    private final Map<Integer, Integer> controls;
    private final InitConfig init;
    private final boolean oneButton;

    /**
     * Create first configuration.
     */
    public GameConfig()
    {
        this(GameType.STORY, 1, Optional.empty(), Optional.empty(), true, Collections.emptyMap(), null);
    }

    /**
     * Create configuration.
     * 
     * @param type The game type.
     * @param players The players number.
     * @param network The network configuration if online, absent if local.
     * @param stages The stages set.
     * @param oneButton <code>true</code> for one button mode, <code>false</code> two buttons.
     * @param controls The player id as key, the control index as value.
     * @param init The init configuration.
     */
    public GameConfig(GameType type,
                      int players,
                      Optional<Network> network,
                      Optional<String> stages,
                      boolean oneButton,
                      Map<Integer, Integer> controls,
                      InitConfig init)
    {
        super();

        this.type = type;
        this.players = players;
        this.network = network;
        this.stages = stages;
        this.controls = controls;
        this.init = init;
        this.oneButton = oneButton;
    }

    /**
     * Create with custom type.
     * 
     * @param type The type.
     * @param players The players number.
     * @param oneButton <code>true</code> for one button mode, <code>false</code> two buttons.
     * @param controls The custom controls.
     * @return The new configuration.
     */
    public GameConfig with(GameType type, int players, boolean oneButton, Map<Integer, Integer> controls)
    {
        return new GameConfig(type, players, network, stages, oneButton, controls, init);
    }

    /**
     * Create with stages set using existing configuration.
     * 
     * @param stages The stages set.
     * @return The new configuration.
     */
    public GameConfig with(String stages)
    {
        return new GameConfig(type, players, network, Optional.ofNullable(stages), oneButton, controls, init);
    }

    /**
     * Create with custom button.
     * 
     * @param oneButton <code>true</code> for one button mode, <code>false</code> two buttons.
     * @return The new configuration.
     */
    public GameConfig with(boolean oneButton)
    {
        return new GameConfig(type, players, network, stages, oneButton, controls, init);
    }

    /**
     * Create with init config using existing configuration.
     * 
     * @param init The init config.
     * @return The new configuration.
     */
    public GameConfig with(InitConfig init)
    {
        return new GameConfig(type, players, network, stages, oneButton, controls, init);
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
     * Get the players number.
     * 
     * @return The players number.
     */
    public int getPlayers()
    {
        return players;
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
     * Get the stages set.
     * 
     * @return The stages set.
     */
    public Optional<String> getStages()
    {
        return stages;
    }

    /**
     * Get the one button flag.
     * 
     * @return <code>true</code> for one button mode, <code>false</code> two buttons.
     */
    public boolean isOneButton()
    {
        return oneButton;
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
