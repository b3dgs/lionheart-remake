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
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.XmlReader;
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
import com.b3dgs.lionheart.Settings;
import com.b3dgs.lionheart.Sfx;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.object.Editable;
import com.b3dgs.lionheart.object.XmlLoader;
import com.b3dgs.lionheart.object.XmlSaver;

/**
 * Geyzer feature implementation.
 * <ol>
 * <li>Move up on delay.</li>
 * </ol>
 */
@FeatureInterface
public final class Geyzer extends FeatureModel
                          implements XmlLoader, XmlSaver, Editable<GeyzerConfig>, Routine, Recyclable
{
    private static final double SPEED = 3.0;

    private final Tick tick = new Tick();
    private final List<Transformable> bottom = new ArrayList<>();
    private final Spawner spawner = services.get(Spawner.class);
    private final Viewer viewer = services.get(Viewer.class);

    private GeyzerConfig config;
    private int phase;
    private double y;
    private double current;
    private boolean first;

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

        if (setup.hasNode(GeyzerConfig.NODE_GEYZER))
        {
            config = new GeyzerConfig(setup.getRoot());
        }
    }

    @Override
    public GeyzerConfig getConfig()
    {
        return config;
    }

    @Override
    public void setConfig(GeyzerConfig config)
    {
        this.config = config;
    }

    @Override
    public void load(XmlReader root)
    {
        if (root.hasNode(GeyzerConfig.NODE_GEYZER))
        {
            config = new GeyzerConfig(root);
        }

        if (!Settings.isEditor())
        {
            bottom.add(spawner.spawn(Medias.create(Folder.LIMB, "lava", "GeyzerCalc.xml"), transformable)
                              .getFeature(Transformable.class));

            for (int i = 0; i < Math.ceil(config.getHeight() / (double) transformable.getHeight()); i++)
            {
                final Featurable featurable = spawner.spawn(Medias.create(Folder.LIMB, "lava", "GeyzerBottom.xml"),
                                                            transformable);
                final Animation idle = AnimationConfig.imports(new Configurer(featurable.getMedia()))
                                                      .getAnimation(Anim.IDLE);
                featurable.ifIs(Animatable.class, a -> a.play(idle));
                bottom.add(featurable.getFeature(Transformable.class));
                y = transformable.getY();
                bottom.get(0).teleportY(y - 28);
            }
        }
    }

    @Override
    public void save(Xml root)
    {
        if (config != null)
        {
            config.save(root);
        }
    }

    @Override
    public void update(double extrp)
    {
        if (config != null)
        {
            tick.update(extrp);

            if (first)
            {
                if (tick.elapsed(config.getDelayFirst()))
                {
                    first = false;
                    tick.restart();
                }
            }
            else
            {
                if (phase == 0 && tick.elapsed(config.getDelayStart()))
                {
                    phase = 1;
                    if (viewer.isViewable(transformable, 0, 0))
                    {
                        Sfx.SCENERY_GEYZER.play();
                    }
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
    }

    @Override
    public void recycle()
    {
        phase = 0;
        first = true;
        tick.restart();
    }
}
