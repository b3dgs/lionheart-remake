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

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UpdatableVoid;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.DirectionNone;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListener;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.launchable.Launcher;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.object.XmlLoader;
import com.b3dgs.lionheart.object.XmlSaver;

/**
 * Catapult feature implementation.
 * <p>
 * Fire on hit and reload.
 * </p>
 */
@FeatureInterface
public final class Catapult extends FeatureModel implements XmlLoader, XmlSaver, Routine, Recyclable, CollidableListener
{
    private final Animation idle;
    private final Animation fire;
    private final Animation reload;

    private CatapultConfig config;
    private Direction vector = DirectionNone.INSTANCE;
    private Updatable updater = UpdatableVoid.getInstance();
    private boolean fired;

    @FeatureGet private Transformable transformable;
    @FeatureGet private Animatable animatable;
    @FeatureGet private Launcher launcher;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Catapult(Services services, Setup setup)
    {
        super(services, setup);

        final AnimationConfig config = AnimationConfig.imports(setup);
        idle = config.getAnimation(Anim.IDLE);
        fire = config.getAnimation(Anim.ATTACK);
        reload = config.getAnimation(Anim.TURN);

        load(setup.getRoot());
    }

    private void updateReload(@SuppressWarnings("unused") double extrp)
    {
        if (fired && animatable.is(AnimState.FINISHED))
        {
            animatable.play(reload);
            animatable.setFrame(reload.getLast());
            updater = this::updateReloaded;
        }
    }

    private void updateReloaded(@SuppressWarnings("unused") double extrp)
    {
        if (animatable.is(AnimState.FINISHED))
        {
            fired = false;
            animatable.play(idle);
            updater = UpdatableVoid.getInstance();
        }
    }

    @Override
    public void load(XmlReader root)
    {
        if (root.hasNode(CatapultConfig.NODE_CATAPULT))
        {
            config = new CatapultConfig(root);
            vector = new Force(config.getVx(), config.getVy());
        }
    }

    @Override
    public void save(Xml root)
    {
        config.save(root);
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        launcher.addListener(l ->
        {
            final Force direction = l.getDirection();
            final double vx = direction.getDirectionHorizontal();
            direction.setDestination(vx, 0.0);
        });
    }

    @Override
    public void update(double extrp)
    {
        updater.update(extrp);
    }

    @Override
    public void notifyCollided(Collidable collidable, Collision with, Collision by)
    {
        if (!fired && with.getName().startsWith(Anim.BODY) && by.getName().startsWith(Anim.ATTACK))
        {
            fired = true;
            animatable.play(fire);
            launcher.fire(vector);
            updater = this::updateReload;
        }
    }

    @Override
    public void recycle()
    {
        animatable.play(idle);
        fired = false;
    }
}
