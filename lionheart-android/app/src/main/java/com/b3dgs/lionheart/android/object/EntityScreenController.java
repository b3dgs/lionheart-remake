/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 *
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
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
package com.b3dgs.lionheart.android.object;

import com.b3dgs.lionengine.android.Mouse;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.geom.Rectangle;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.Routine;

/**
 * Entity updating implementation.
 */
@FeatureInterface
final class EntityScreenController extends FeatureModel implements Routine
{
    private final VirtualDeviceButton virtual;

    @FeatureGet private EntityModel model;

    /**
     * Create updater.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    EntityScreenController(Services services, Setup setup)
    {
        super();

        final Mouse mouse = services.get(Mouse.class);
        virtual = new VirtualDeviceButton(mouse);

        virtual.addButton(new Rectangle(335, 125, 32, 32), VirtualDeviceButton.LEFT, "<");
        virtual.addButton(new Rectangle(368, 125, 32, 32), VirtualDeviceButton.RIGHT, ">");
        virtual.addButton(new Rectangle(335, 160, 64, 32), VirtualDeviceButton.DOWN, "\\/");

        virtual.addButton(new Rectangle(0, 100, 32, 36), VirtualDeviceButton.UP, "J");
        virtual.addButton(new Rectangle(0, 140, 32, 36), VirtualDeviceButton.CONTROL, "F");
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        model.setInput(virtual);
    }

    @Override
    public void update(double extrp)
    {
        virtual.update(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        virtual.render(g);
    }
}
