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

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.Updatable;
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
import com.b3dgs.lionengine.game.feature.rasterable.RasterableModel;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionheart.Settings;
import com.b3dgs.lionheart.Sfx;
import com.b3dgs.lionheart.WorldType;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.object.Editable;
import com.b3dgs.lionheart.object.XmlLoader;
import com.b3dgs.lionheart.object.XmlSaver;

/**
 * Geyzer feature implementation.
 * <ol>
 * <li>Start delay.</li>
 * <li>Move up on delay.</li>
 * <li>Move down on delay.</li>
 * </ol>
 */
@FeatureInterface
public final class Geyzer extends FeatureModel
                          implements XmlLoader, XmlSaver, Editable<GeyzerConfig>, Routine, Recyclable
{
    private static final double SPEED = 3.0;
    private static final String BOTTOM_FILE = "GeyzerBottom.xml";

    private final Tick tick = new Tick();
    private final List<Transformable> bottom = new ArrayList<>();

    private final SourceResolutionProvider source = services.get(SourceResolutionProvider.class);
    private final Spawner spawner = services.get(Spawner.class);
    private final Viewer viewer = services.get(Viewer.class);

    private GeyzerConfig config;
    private Updatable updater;
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

        if (setup.hasNode(GeyzerConfig.NODE_GEYZER))
        {
            config = new GeyzerConfig(setup.getRoot());
        }
    }

    /**
     * Update first delay.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFirst(double extrp)
    {
        tick.update(extrp);
        if (tick.elapsedTime(source.getRate(), config.getDelayFirst()))
        {
            tick.restart();
            updater = this::updateCheckRaise;
        }
    }

    /**
     * Update check raise delay.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateCheckRaise(double extrp)
    {
        tick.update(extrp);
        if (tick.elapsedTime(source.getRate(), config.getDelayStart()))
        {
            if (viewer.isViewable(transformable, 0, 0))
            {
                Sfx.SCENERY_GEYZER.play();
            }
            tick.restart();
            updater = this::updateRaise;
        }
    }

    /**
     * Update raise loop.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateRaise(double extrp)
    {
        current += SPEED * extrp;
        transformable.setLocationY(y + getCurrent() - 1);

        if (getCurrent() >= config.getHeight())
        {
            current = config.getHeight();
            tick.restart();
            updater = this::updateCheckFall;
        }
    }

    /**
     * Update check fall delay.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateCheckFall(double extrp)
    {
        tick.update(extrp);
        if (tick.elapsedTime(source.getRate(), config.getDelayDown()))
        {
            tick.restart();
            updater = this::updateFall;
        }
    }

    /**
     * Update fall loop.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFall(double extrp)
    {
        current -= SPEED * extrp;
        transformable.setLocationY(y + getCurrent() - 1);

        if (getCurrent() <= 0)
        {
            current = 0;
            tick.restart();
            updater = this::updateCheckRaise;
        }
    }

    /**
     * Get current height.
     * 
     * @return The current height.
     */
    private int getCurrent()
    {
        return (int) Math.floor(current);
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
            transformable.moveLocationY(1.0, 8);

            for (int i = 0; i <= Math.ceil(config.getHeight() / (double) transformable.getHeight()); i++)
            {
                final Featurable featurable = spawner.spawn(Medias.create(Folder.LIMB,
                                                                          WorldType.LAVA.getFolder(),
                                                                          BOTTOM_FILE),
                                                            transformable);
                final Animation idle = AnimationConfig.imports(new Configurer(featurable.getMedia()))
                                                      .getAnimation(Anim.IDLE);
                featurable.ifIs(Animatable.class, a -> a.play(idle));
                bottom.add(featurable.getFeature(Transformable.class));
                y = transformable.getY();
            }
            bottom.get(0).teleportY(y - 2);
        }
    }

    @Override
    public void save(Xml root)
    {
        config.save(root);
    }

    @Override
    public void update(double extrp)
    {
        updater.update(extrp);

        for (int i = 1; i < bottom.size(); i++)
        {
            bottom.get(i).teleportY(y + getCurrent() - transformable.getHeight() * i);
            bottom.get(i).getFeature(RasterableModel.class).setVisibility(bottom.get(i).getY() > 24);
        }
    }

    @Override
    public void recycle()
    {
        tick.restart();
        updater = this::updateFirst;
    }
}
