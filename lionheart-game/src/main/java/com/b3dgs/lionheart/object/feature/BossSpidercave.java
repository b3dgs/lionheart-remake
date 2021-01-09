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

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UpdatableVoid;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Spawner;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.launchable.Launcher;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionheart.WorldType;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.constant.Folder;

/**
 * Boss Spider cave feature implementation.
 * <ol>
 * <li>Patrol horizontal.</li>
 * <li>Spawn spider.</li>
 * <li>Open eyes and throw bowl.</li>
 * <li>Close eyes and patrol back.</li>
 * <li>Spawn spider.</li>
 * <li>Open eyes and attack jump.</li>
 * <li>Close eyes.</li>
 * </ol>
 */
@FeatureInterface
public final class BossSpidercave extends FeatureModel implements Routine, Recyclable
{
    private static final int HEAD_OFFSET_X = -32;
    private static final int PATROL_MARGIN = 80;

    private final Transformable player = services.get(SwordShade.class).getFeature(Transformable.class);
    private final Spawner spawner = services.get(Spawner.class);
    private final Animation walk;
    private final Animation dead;
    private final Updatable updater;

    private Updatable current;
    private Transformable head;
    private Stats stats;
    private double minX;
    private double maxX;
    private double speed;

    @FeatureGet private Animatable animatable;
    @FeatureGet private Transformable transformable;
    @FeatureGet private Launcher launcher;
    @FeatureGet private Identifiable identifiable;
    @FeatureGet private Collidable collidable;
    @FeatureGet private Rasterable rasterable;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public BossSpidercave(Services services, Setup setup)
    {
        super(services, setup);

        final AnimationConfig config = AnimationConfig.imports(setup);
        walk = config.getAnimation(Anim.WALK);
        dead = config.getAnimation(Anim.DEAD);

        updater = extrp ->
        {
            if (minX < 0)
            {
                minX = transformable.getX() - PATROL_MARGIN;
                maxX = transformable.getX();
            }
            transformable.moveLocationX(extrp, speed);
            if (transformable.getX() < minX)
            {
                transformable.teleportX(minX);
                speed = -speed;
            }
            else if (transformable.getX() > maxX)
            {
                transformable.teleportX(maxX);
                speed = -speed;
            }
            head.setLocation(transformable.getX() + HEAD_OFFSET_X, transformable.getY());

            if (stats.getHealth() == 0)
            {
                current = UpdatableVoid.getInstance();
                animatable.play(dead);
                collidable.setEnabled(false);
            }
        };
    }

    @Override
    public void update(double extrp)
    {
        current.update(extrp);
    }

    @Override
    public void recycle()
    {
        minX = -1.0;
        maxX = -1.0;
        speed = -0.4;
        collidable.setEnabled(true);
        animatable.play(walk);
        head = spawner.spawn(Medias.create(Folder.BOSS, WorldType.SPIDERCAVE1.getFolder(), "Head.xml"), transformable)
                      .getFeature(Transformable.class);
        stats = head.getFeature(Stats.class);
        current = updater;
    }
}
