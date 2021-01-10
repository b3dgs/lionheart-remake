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

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UpdatableVoid;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.Featurable;
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
import com.b3dgs.lionengine.game.feature.body.Body;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.launchable.Launcher;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionheart.Sfx;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.object.EntityModel;

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
    private static final int HEAD_OPENED_TICK = 80;
    private static final int HEAD_ATTACK_OFFSET_Y = 12;

    private final Transformable player = services.get(SwordShade.class).getFeature(Transformable.class);
    private final Spawner spawner = services.get(Spawner.class);
    private final Tick tick = new Tick();
    private final Animation walk;
    private final Animation attack;
    private final Animation dead;
    private final Updatable updater;

    private Featurable head;
    private Transformable headTransformable;
    private Collidable headCollidable;
    private Animatable headAnim;
    private Animation headOpen;

    private Updatable current;
    private Stats stats;
    private double minX;
    private double maxX;
    private double speed;
    private int step;
    private int headOffsetY;
    private double oldY;
    private double jump;

    @FeatureGet private Animatable animatable;
    @FeatureGet private Transformable transformable;
    @FeatureGet private Launcher launcher;
    @FeatureGet private Identifiable identifiable;
    @FeatureGet private Collidable collidable;
    @FeatureGet private Rasterable rasterable;
    @FeatureGet private EntityModel model;
    @FeatureGet private Body body;

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
        attack = config.getAnimation(Anim.ATTACK);
        dead = config.getAnimation(Anim.DEAD);

        updater = extrp ->
        {
            tick.update(extrp);

            if (step == 0 || step == 4)
            {
                if (minX < 0)
                {
                    minX = transformable.getX() - PATROL_MARGIN;
                    maxX = transformable.getX();
                    oldY = transformable.getY();
                }
                transformable.moveLocationX(extrp, speed);
                if (transformable.getX() < minX)
                {
                    transformable.teleportX(minX);
                    animatable.setAnimSpeed(-animatable.getAnimSpeed());
                    speed = -speed;
                }
                else if (transformable.getX() > maxX)
                {
                    transformable.teleportX(maxX);
                    animatable.setAnimSpeed(-animatable.getAnimSpeed());
                    speed = -speed;
                    step++;
                }
            }
            else if (step == 1)
            {
                launcher.setLevel(0);
                launcher.fire();
                headAnim.play(headOpen);
                animatable.play(attack);
                Sfx.BOSS2.play();
                step++;
                tick.restart();
            }
            else if (step == 2 && !headAnim.is(AnimState.PLAYING) && tick.elapsed(HEAD_OPENED_TICK))
            {
                launcher.setLevel(1);
                launcher.fire();
                tick.restart();
                step++;
            }
            else if (step == 3 && tick.elapsed(HEAD_OPENED_TICK))
            {
                animatable.play(walk);
                headAnim.play(headOpen);
                headAnim.setFrame(headOpen.getLast());
                headAnim.setAnimSpeed(-headAnim.getAnimSpeed());
                step++;
            }
            else if (step == 5)
            {
                launcher.setLevel(0);
                launcher.fire();
                headAnim.play(headOpen);
                Sfx.BOSS2.play();
                animatable.play(attack);
                tick.restart();
                step++;
            }
            else if (step == 6 && !headAnim.is(AnimState.PLAYING) && tick.elapsed(HEAD_OPENED_TICK))
            {
                oldY = transformable.getY();
                body.setGravity(4.5);
                body.setGravityMax(4.5);
                body.resetGravity();
                jump = 8.0;
                model.getJump().setDirection(0.0, jump);
                step++;
            }
            else if (step == 7)
            {
                transformable.moveLocationX(extrp, -1.0);
                if (transformable.getY() < oldY)
                {
                    jump -= 3.0;
                    transformable.teleportY(oldY);
                    body.resetGravity();
                    model.getJump().setDirection(0.0, jump);
                    if (jump < 0)
                    {
                        model.getJump().zero();
                        body.setGravity(0.0);
                        body.setGravityMax(0.0);
                        step++;
                    }
                }
            }
            else if (step == 8)
            {
                headAnim.play(headOpen);
                headAnim.setFrame(headOpen.getLast());
                headAnim.setAnimSpeed(-headAnim.getAnimSpeed());
                step++;
            }
            else if (step == 9 && !headAnim.is(AnimState.PLAYING))
            {
                animatable.play(walk);
                step = 0;
            }

            if (stats.getHealth() == 0)
            {
                current = UpdatableVoid.getInstance();
                animatable.play(dead);
                collidable.setEnabled(false);
            }
            else
            {
                if (animatable.getFrame() == 12)
                {
                    headOffsetY = HEAD_ATTACK_OFFSET_Y / 3;
                }
                else if (animatable.getFrame() == 13)
                {
                    headOffsetY = HEAD_ATTACK_OFFSET_Y / 2;
                }
                else if (animatable.getFrame() == 14)
                {
                    headOffsetY = HEAD_ATTACK_OFFSET_Y;
                }
                else
                {
                    headOffsetY = 0;
                }
                headTransformable.setLocation(transformable.getX() + HEAD_OFFSET_X, transformable.getY() + headOffsetY);
                headCollidable.setEnabled(headAnim.getFrame() == 6);

                if (player.getX() > transformable.getX())
                {
                    player.teleportX(transformable.getX());
                }
            }
        };
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        launcher.addListener(l -> l.ifIs(Spider.class, s -> s.track(-1)));
    }

    @Override
    public void update(double extrp)
    {
        current.update(extrp);

        if (stats.getHealth() == 0 && transformable.getY() < oldY)
        {
            transformable.teleportY(oldY);
            body.resetGravity();
            model.getJump().zero();
            body.setGravity(0.0);
            body.setGravityMax(0.0);
        }
    }

    @Override
    public void recycle()
    {
        head = spawner.spawn(Medias.create(setup.getMedia().getParentPath(), "Head.xml"), transformable);
        headTransformable = head.getFeature(Transformable.class);
        headCollidable = head.getFeature(Collidable.class);
        headAnim = head.getFeature(Animatable.class);
        final AnimationConfig headConfig = AnimationConfig.imports(new Configurer(head.getMedia()));
        headOpen = headConfig.getAnimation("open");

        minX = -1.0;
        maxX = -1.0;
        speed = -0.4;
        collidable.setEnabled(true);
        animatable.play(walk);
        stats = head.getFeature(Stats.class);
        current = updater;
        headOffsetY = 0;
        step = 0;
    }
}
