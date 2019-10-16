/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionheart.object;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.FramesConfig;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.AnimatableModel;
import com.b3dgs.lionengine.game.feature.DisplayableModel;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.LayerableModel;
import com.b3dgs.lionengine.game.feature.MirrorableModel;
import com.b3dgs.lionengine.game.feature.RefreshableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableModel;
import com.b3dgs.lionengine.game.feature.launchable.Launchable;
import com.b3dgs.lionengine.game.feature.launchable.LaunchableModel;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionengine.game.feature.rasterable.RasterableModel;
import com.b3dgs.lionengine.game.feature.rasterable.SetupSurfaceRastered;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.object.feature.Stats;

/**
 * Projectile implementation.
 */
public final class Projectile extends FeaturableModel
{
    /**
     * Constructor.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public Projectile(Services services, SetupSurfaceRastered setup)
    {
        super(services, setup);

        addFeature(new LayerableModel(services, setup));
        addFeature(new MirrorableModel(services, setup));

        final Transformable transformable = addFeatureAndGet(new TransformableModel(services, setup));
        final Collidable collidable = addFeatureAndGet(new CollidableModel(services, setup));
        collidable.setOrigin(Origin.MIDDLE);
        collidable.setGroup(Constant.COLL_GROUP_PROJECTILES);
        collidable.addAccept(Constant.COLL_GROUP_PLAYER);
        collidable.setCollisionVisibility(Constant.DEBUG);

        final FramesConfig config = FramesConfig.imports(setup);
        final SpriteAnimated sprite = Drawable.loadSpriteAnimated(setup.getSurface(),
                                                                  config.getHorizontal(),
                                                                  config.getVertical());
        sprite.setOrigin(Origin.MIDDLE);

        addFeature(new Stats(services, setup));
        final Animatable animatable = addFeatureAndGet(new AnimatableModel(services, setup, sprite));
        final Rasterable rasterable = addFeatureAndGet(new RasterableModel(services, setup));
        rasterable.setOrigin(Origin.MIDDLE);

        final Launchable launchable = addFeatureAndGet(new LaunchableModel(services, setup));
        final AnimationConfig anims = AnimationConfig.imports(setup);
        final Animation idle = anims.getAnimation(Anim.IDLE);
        sprite.play(idle);

        addFeature(new RefreshableModel(extrp ->
        {
            launchable.update(extrp);
            animatable.update(extrp);
            rasterable.update(extrp);
        }));

        final Viewer viewer = services.get(Viewer.class);
        addFeature(new DisplayableModel(g ->
        {
            sprite.setLocation(viewer, transformable);
            rasterable.render(g);
            collidable.render(g);
        }));
    }
}
