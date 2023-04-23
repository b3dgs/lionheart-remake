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

import java.util.ArrayList;
import java.util.List;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionheart.object.XmlLoader;
import com.b3dgs.lionheart.object.XmlSaver;

/**
 * Patrols configuration.
 */
@FeatureInterface
public final class Patrols extends FeatureModel implements XmlLoader, XmlSaver
{
    private final List<PatrolConfig> patrols = new ArrayList<>();

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Patrols(Services services, Setup setup)
    {
        super(services, setup);

        load(setup.getRoot());
    }

    /**
     * Get patrols.
     * 
     * @return The patrols.
     */
    public List<PatrolConfig> get()
    {
        return patrols;
    }

    /**
     * Get the patrol.
     * 
     * @param index The index number.
     * @return The patrol configuration.
     */
    public PatrolConfig get(int index)
    {
        return patrols.get(index);
    }

    /**
     * Get the number of patrols.
     * 
     * @return The patrols number.
     */
    public int size()
    {
        return patrols.size();
    }

    @Override
    public void load(XmlReader root)
    {
        Check.notNull(root);

        if (root.hasNode(PatrolConfig.NODE_PATROL))
        {
            patrols.clear();
            for (final XmlReader node : root.getChildren(PatrolConfig.NODE_PATROL))
            {
                patrols.add(new PatrolConfig(node));
            }
        }
    }

    @Override
    public void save(Xml root)
    {
        for (final PatrolConfig config : patrols)
        {
            config.save(root);
        }
    }
}
