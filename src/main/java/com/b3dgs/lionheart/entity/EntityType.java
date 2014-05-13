/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionheart.entity;

import com.b3dgs.lionengine.game.ObjectType;
import com.b3dgs.lionengine.game.ObjectTypeConverter;
import com.b3dgs.lionheart.entity.item.Life;
import com.b3dgs.lionheart.entity.item.PotionBig;
import com.b3dgs.lionheart.entity.item.PotionBlack;
import com.b3dgs.lionheart.entity.item.PotionLittle;
import com.b3dgs.lionheart.entity.item.Sword2;
import com.b3dgs.lionheart.entity.item.Sword3;
import com.b3dgs.lionheart.entity.item.Sword4;
import com.b3dgs.lionheart.entity.item.Talisment;
import com.b3dgs.lionheart.entity.monster.Bee;
import com.b3dgs.lionheart.entity.monster.BeeLittle;
import com.b3dgs.lionheart.entity.monster.BumbleBee;
import com.b3dgs.lionheart.entity.monster.Crawling;
import com.b3dgs.lionheart.entity.monster.Dino;
import com.b3dgs.lionheart.entity.monster.Flower;
import com.b3dgs.lionheart.entity.monster.Fly;
import com.b3dgs.lionheart.entity.monster.Grasshopper;
import com.b3dgs.lionheart.entity.monster.Nest;
import com.b3dgs.lionheart.entity.monster.NestLittle;
import com.b3dgs.lionheart.entity.scenery.BeetleHorizontal;
import com.b3dgs.lionheart.entity.scenery.BeetleVertical;
import com.b3dgs.lionheart.entity.scenery.Bird;
import com.b3dgs.lionheart.entity.scenery.CarnivorousPlant;
import com.b3dgs.lionheart.entity.scenery.Dragon;
import com.b3dgs.lionheart.entity.scenery.Sheet;
import com.b3dgs.lionheart.entity.scenery.Spike1;
import com.b3dgs.lionheart.entity.scenery.Spike2;
import com.b3dgs.lionheart.entity.scenery.TurningAuto;
import com.b3dgs.lionheart.entity.scenery.TurningHit;

/**
 * List of entities type.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public enum EntityType implements ObjectType<Entity>
{
    /** Bee. */
    BEE(Bee.class),
    /** Bee little. */
    BEE_LITTLE(BeeLittle.class),
    /** Beetle horizontal. */
    BEETLE_HORIZONTAL(BeetleHorizontal.class),
    /** Beetle vertical. */
    BEETLE_VERTICAL(BeetleVertical.class),
    /** Bird. */
    BIRD(Bird.class),
    /** Bumble bee. */
    BUMBLE_BEE(BumbleBee.class),
    /** Carnivorous plant. */
    CARNIVOROUS_PLANT(CarnivorousPlant.class),
    /** Crawling. */
    CRAWLING(Crawling.class),
    /** Dino. */
    DINO(Dino.class),
    /** Dragon. */
    DRAGON(Dragon.class),
    /** Flower. */
    FLOWER(Flower.class),
    /** Fly. */
    FLY(Fly.class),
    /** Grasshopper. */
    GRASSHOPPER(Grasshopper.class),
    /** Life. */
    LIFE(Life.class),
    /** Nest. */
    NEST(Nest.class),
    /** NestLittle. */
    NEST_LITTLE(NestLittle.class),
    /** Potion big. */
    POTION_BIG(PotionBig.class),
    /** Potion black. */
    POTION_BLACK(PotionBlack.class),
    /** Potion little. */
    POTION_LITTLE(PotionLittle.class),
    /** Sheet. */
    SHEET(Sheet.class),
    /** Spike 1. */
    SPIKE1(Spike1.class),
    /** Spike 2. */
    SPIKE2(Spike2.class),
    /** Sword 2. */
    SWORD2(Sword2.class),
    /** Sword 3. */
    SWORD3(Sword3.class),
    /** Sword 4. */
    SWORD4(Sword4.class),
    /** Talisment. */
    TALISMENT(Talisment.class),
    /** Turning auto. */
    TURNING_AUTO(TurningAuto.class),
    /** Turning hit. */
    TURNING_HIT(TurningHit.class);

    /** Converter. */
    public static final ObjectTypeConverter<Entity, EntityType> CONVERTER = new ObjectTypeConverter<>(EntityType.class);

    /** Type. */
    private final Class<? extends Entity> type;

    /**
     * Constructor.
     * 
     * @param type The type reference.
     */
    private EntityType(Class<? extends Entity> type)
    {
        this.type = type;
    }

    /*
     * ObjectType
     */

    @Override
    public Class<? extends Entity> getType()
    {
        return type;
    }
}
