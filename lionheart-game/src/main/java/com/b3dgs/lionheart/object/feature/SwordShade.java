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

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.RoutineRender;
import com.b3dgs.lionengine.game.feature.RoutineUpdate;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;
import com.b3dgs.lionheart.Constant;
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
public final class SwordShade extends FeatureModel implements RoutineUpdate, RoutineRender
{
    private static final int SHADE_FH = 10;
    private static final int SHADE_FV = 5;

    private final Mirrorable mirrorable;
    private final Transformable transformable;

    private final SpriteAnimated[] shades = new SpriteAnimated[Constant.STATS_MAX_SWORD];
    private final AnimationConfig config;
    private final Viewer viewer;

    private SpriteAnimated shade;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @param mirrorable The mirrorable feature.
     * @param transformable The transformable feature.
     * @param stateHandler The state feature.
     * @param stats The stats feature.
     * @throws LionEngineException If invalid arguments.
     */
    public SwordShade(Services services,
                      Setup setup,
                      Mirrorable mirrorable,
                      Transformable transformable,
                      StateHandler stateHandler,
                      Stats stats)
    {
        super(services, setup);

        this.mirrorable = mirrorable;
        this.transformable = transformable;

        viewer = services.get(Viewer.class);

        for (int i = 0; i < shades.length; i++)
        {
            shades[i] = Drawable.loadSpriteAnimated(Medias.create(setup.getMedia().getParentPath(),
                                                                  "shade" + i + ".png"),
                                                    SHADE_FH,
                                                    SHADE_FV);
            shades[i].load();
            shades[i].prepare();
            shades[i].setFrameOffsets(shades[i].getTileWidth() / 2, -shades[i].getTileHeight());
        }
        shade = shades[0];

        config = AnimationConfig.imports(setup);

        stateHandler.addListener((from, to) ->
        {
            final String name = Anim.SHADE + EntityModel.getAnimationName(to);
            if (config.hasAnimation(name))
            {
                shade.play(config.getAnimation(name));
                Sfx.VALDYN_SWORD.play();
            }
            else
            {
                shade.stop();
            }
        });

        stats.addListener((StatsSwordListener) level -> shade = shades[level]);
    }

    @Override
    public void update(double extrp)
    {
        shade.update(extrp);
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
