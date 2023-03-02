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

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.Updatable;
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
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionheart.WorldType;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.object.EntityModel;

/**
 * Laser Airship feature implementation.
 * <ol>
 * <li>Impact and smoke.</li>
 * </ol>
 */
@FeatureInterface
public final class Laser extends FeatureModel implements Routine, Recyclable
{
    private static final double VX = -5.0;
    private static final double VY = 2.5;
    private static final int EFFECT_DELAY_MS = 100;
    private static final int SMOKE_OFFSET_Y = -4;
    private static final String SMOKE_FILE = "Smoke.xml";

    private final Tick alive = new Tick();
    private final Tick effect = new Tick();

    private final SourceResolutionProvider source = services.get(SourceResolutionProvider.class);
    private final Spawner spawner = services.get(Spawner.class);

    private int stayDelay;
    private Updatable current;

    @FeatureGet private Transformable transformable;
    @FeatureGet private Identifiable identifiable;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Laser(Services services, Setup setup)
    {
        super(services, setup);
    }

    /**
     * Load configuration.
     * 
     * @param stayDelay The stay delay.
     * @param parent The parent reference.
     */
    public void load(int stayDelay, Identifiable parent)
    {
        this.stayDelay = stayDelay;

        parent.addListener(id -> identifiable.destroy());
    }

    /**
     * Update fire delay.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFire(double extrp)
    {
        alive.update(extrp);
        effect.update(extrp);

        if (stayDelay > 0 && alive.elapsedTime(source.getRate(), stayDelay))
        {
            identifiable.destroy();
        }
        else if (effect.elapsedTime(source.getRate(), EFFECT_DELAY_MS))
        {
            spawner.spawn(Medias.create(Folder.EFFECT, WorldType.AIRSHIP.getFolder(), SMOKE_FILE),
                          transformable.getX(),
                          transformable.getY() + SMOKE_OFFSET_Y)
                   .getFeature(EntityModel.class)
                   .getMovement()
                   .setDirection(VX, VY);
            effect.restart();
        }
    }

    @Override
    public void update(double extrp)
    {
        current.update(extrp);
    }

    @Override
    public void recycle()
    {
        current = this::updateFire;
        alive.restart();
        effect.restart();
    }
}
