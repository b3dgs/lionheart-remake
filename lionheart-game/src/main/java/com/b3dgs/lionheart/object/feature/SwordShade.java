/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;
import com.b3dgs.lionheart.Sfx;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.object.EntityModel;

/**
 * Sword shade feature implementation.
 * <p>
 * Add shade layer to sword attacks, sync to player animation.
 * </p>
 */
@FeatureInterface
public final class SwordShade extends FeatureModel implements Routine
{
    private final SpriteAnimated shade;
    private final AnimationConfig config;
    private final Viewer viewer;

    private Animation anim;

    @FeatureGet private Mirrorable mirrorable;
    @FeatureGet private Transformable transformable;
    @FeatureGet private StateHandler stateHandler;
    @FeatureGet private Animatable animatable;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public SwordShade(Services services, Setup setup)
    {
        super(services, setup);

        viewer = services.get(Viewer.class);

        shade = Drawable.loadSpriteAnimated(Medias.create(setup.getMedia().getParentPath(), "shade1.png"), 7, 7);
        shade.load();
        shade.prepare();
        shade.setFrameOffsets(shade.getTileWidth() / 2, -shade.getTileHeight());
        config = AnimationConfig.imports(setup);
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        stateHandler.addListener((from, to) ->
        {
            shade.stop();
            final String name = Anim.SHADE + EntityModel.getAnimationName(to);
            if (config.hasAnimation(name))
            {
                anim = config.getAnimation(name);
            }
        });
    }

    @Override
    public void update(double extrp)
    {
        shade.update(extrp);
        if (anim != null)
        {
            shade.play(anim);
            Sfx.VALDYN_ATTACK.play();
            anim = null;
        }
    }

    @Override
    public void render(Graphic g)
    {
        if (AnimState.PLAYING == shade.getAnimState())
        {
            shade.setMirror(mirrorable.getMirror());
            shade.setLocation(viewer, transformable);
            shade.render(g);
        }
    }
}
