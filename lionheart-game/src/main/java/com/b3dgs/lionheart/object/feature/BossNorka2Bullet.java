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
package com.b3dgs.lionheart.object.feature;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListener;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.launchable.Launchable;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.constant.Anim;

/**
 * Boss Norka 2 Bullet.
 */
@FeatureInterface
public final class BossNorka2Bullet extends FeatureModel implements Recyclable, CollidableListener
{
    private final Hurtable hurtable;
    private final Launchable launchable;
    private final Collidable collidable;

    private boolean reverted;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @param hurtable The hurtable feature.
     * @param launchable The launchable feature.
     * @param collidable The collidable feature.
     * @throws LionEngineException If invalid arguments.
     */
    public BossNorka2Bullet(Services services,
                            Setup setup,
                            Hurtable hurtable,
                            Launchable launchable,
                            Collidable collidable)
    {
        super(services, setup);

        this.hurtable = hurtable;
        this.launchable = launchable;
        this.collidable = collidable;
    }

    @Override
    public void notifyCollided(Collidable collidable, Collision with, Collision by)
    {
        if (reverted
            && with.getName().startsWith(Anim.ATTACK)
            && by.getName().startsWith(Anim.BODY)
            && collidable.hasFeature(BossNorka2.class))
        {
            collidable.getFeature(Hurtable.class).updateCollideAttack(collidable, by);
            hurtable.kill(true);
        }
        if (!reverted
            && with.getName().startsWith(Anim.BODY)
            && by.getName().startsWith(Anim.ATTACK)
            && !by.getName().endsWith(Anim.ATTACK))
        {
            final double sh = launchable.getDirection().getDirectionHorizontal();
            final double sv = launchable.getDirection().getDirectionVertical();
            launchable.getDirection().setDirection(-sh, sv);
            launchable.getDirection().setDestination(-sh, sv);
            this.collidable.addAccept(Constant.COLL_GROUP_ENEMIES);
            reverted = true;
        }
    }

    @Override
    public void recycle()
    {
        reverted = false;
        collidable.removeAccept(Constant.COLL_GROUP_ENEMIES);
    }
}
