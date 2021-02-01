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

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Spawner;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionheart.Sfx;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.constant.Folder;

/**
 * Geyzer feature implementation.
 * <ol>
 * <li>Move up on delay.</li>
 * </ol>
 */
@FeatureInterface
public final class Geyzer extends FeatureModel implements Routine, Recyclable
{
    private static final double SPEED = 3.0;

    private final Tick tick = new Tick();
    private final List<Transformable> bottom = new ArrayList<>();
    private final Spawner spawner = services.get(Spawner.class);

    private GeyzerConfig config;
    private int phase;
    private double y;
    private double current;

    @FeatureGet private Transformable transformable;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Geyzer(Services services, Setup setup)
    {
        super(services, setup);
    }

    /**
     * Load configuration.
     * 
     * @param config The configuration to load.
     */
    public void load(GeyzerConfig config)
    {
        this.config = config;

        bottom.add(spawner.spawn(Medias.create(Folder.SCENERIES, "lava", "GeyzerCalc.xml"), transformable)
                          .getFeature(Transformable.class));

        for (int i = 0; i < config.getHeight() / transformable.getHeight(); i++)
        {
            final Featurable featurable = spawner.spawn(Medias.create(Folder.SCENERIES, "lava", "GeyzerBottom.xml"),
                                                        transformable);
            final Animation idle = AnimationConfig.imports(new Configurer(featurable.getMedia()))
                                                  .getAnimation(Anim.IDLE);
            featurable.getFeature(Animatable.class).play(idle);
            bottom.add(featurable.getFeature(Transformable.class));
            y = transformable.getY();
            bottom.get(0).teleportY(y - 28);
        }
    }

    @Override
    public void update(double extrp)
    {
        if (config != null)
        {
            tick.update(extrp);

            if (phase == 0 && tick.elapsed(config.getDelayStart()))
            {
                phase = 1;
                Sfx.SCENERY_GEYZER.play();
                tick.restart();
            }
            else if (phase == 1 && tick.elapsed(config.getDelayDown()))
            {
                phase = 0;
                tick.restart();
            }

            if (phase == 1 && current < config.getHeight())
            {
                current += SPEED;
                transformable.setLocationY(y + current);
            }
            else if (phase == 0 && current > 0)
            {
                current -= SPEED;
                transformable.setLocationY(y + current);
            }

            for (int i = 1; i < bottom.size(); i++)
            {
                bottom.get(i).teleportY(y + current - transformable.getHeight() * i);
            }
        }
    }

    @Override
    public void recycle()
    {
        phase = 0;
        tick.restart();
    }
}
