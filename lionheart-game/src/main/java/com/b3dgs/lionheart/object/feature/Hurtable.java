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

import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.OptionalInt;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UpdatableVoid;
import com.b3dgs.lionengine.UtilConversion;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.FramesConfig;
import com.b3dgs.lionengine.game.OriginConfig;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.RoutineRender;
import com.b3dgs.lionengine.game.feature.RoutineUpdate;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Spawner;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.body.Body;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListener;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListenerVoid;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.networkable.Networkable;
import com.b3dgs.lionengine.game.feature.networkable.Syncable;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionengine.game.feature.tile.map.collision.Axis;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionCategory;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionResult;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableListener;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionengine.helper.EntityChecker;
import com.b3dgs.lionengine.network.Packet;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.LoadNextStage;
import com.b3dgs.lionheart.MapTileWater;
import com.b3dgs.lionheart.Sfx;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.constant.CollisionName;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.SetupEntity;
import com.b3dgs.lionheart.object.state.StateDie;
import com.b3dgs.lionheart.object.state.StateGripIdle;
import com.b3dgs.lionheart.object.state.StateGripSoar;
import com.b3dgs.lionheart.object.state.StateHurt;
import com.b3dgs.lionheart.object.state.StateIdleAnimal;
import com.b3dgs.lionheart.object.state.StateLianaIdle;
import com.b3dgs.lionheart.object.state.StateLianaSoar;
import com.b3dgs.lionheart.object.state.StateLianaWalk;
import com.b3dgs.lionheart.object.state.attack.StateAttackGrip;

/**
 * Hurtable feature implementation.
 * <p>
 * Represents something that can be hurt by attack.
 * </p>
 */
@FeatureInterface
public final class Hurtable extends FeatureModel implements RoutineUpdate, RoutineRender, CollidableListener,
                            TileCollidableListener, Syncable, Recyclable
{
    private static final int NEXT_DELAY_MS = 5000;
    private static final int HURT_RECOVER_ATTACK_DELAY_MS = 300;
    private static final int HURT_RECOVER_BODY_DELAY_MS = 2000;
    private static final int HURT_FLICKER_DURATION_MS = 2000;
    private static final int HURT_FLICKER_SWITCH_DELAY_MS = 130;
    private static final int SPIKE_DAMAGES = 1;

    private final SourceResolutionProvider source = services.get(SourceResolutionProvider.class);
    private final Spawner spawner = services.get(Spawner.class);
    private final Viewer viewer = services.get(Viewer.class);
    private final LoadNextStage stage = services.get(LoadNextStage.class);
    private final MapTileWater mapWater = services.get(MapTileWater.class);

    private final Identifiable identifiable;
    private final Transformable transformable;
    private final Body body;
    private final Mirrorable mirrorable;
    private final StateHandler stateHandler;
    private final Collidable collidable;
    private final TileCollidable tileCollidable;
    private final EntityModel model;
    private final EntityChecker checker;
    private final Stats stats;
    private final Rasterable rasterable;
    private final Networkable networkable;

    private final Force hurtForce = new Force();
    private final Tick recover = new Tick();
    private final Tick flicker = new Tick();
    private final SpriteAnimated shade;
    private final double hurtForceValue;
    private final Optional<Media> effect;
    private final int effectOffsetX;
    private final OptionalInt frame;
    private final boolean interrupt;
    private final boolean persist;
    private final boolean fall;
    private final Sfx sfx;
    private final boolean boss;

    private CollidableListener currentCollide;
    private TileCollidableListener currentTile;
    private Updatable flickerCurrent;
    private boolean enabled;
    private boolean shield;
    private boolean invincibility;
    private double oldGravity;
    private double oldGravityMax;
    private boolean shading;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @param identifiable The identifiable feature.
     * @param transformable The transformable feature.
     * @param body The body feature.
     * @param mirrorable The mirrorable feature.
     * @param stateHandler The state feature.
     * @param collidable The collidable feature.
     * @param tileCollidable The tile collidable feature.
     * @param model The model feature.
     * @param checker The checker feature.
     * @param stats The stats feature.
     * @param rasterable The rasterable feature.
     * @param networkable The networkable feature.
     * @throws LionEngineException If invalid arguments.
     */
    public Hurtable(Services services,
                    SetupEntity setup,
                    Identifiable identifiable,
                    Transformable transformable,
                    Body body,
                    Mirrorable mirrorable,
                    StateHandler stateHandler,
                    Collidable collidable,
                    TileCollidable tileCollidable,
                    EntityModel model,
                    EntityChecker checker,
                    Stats stats,
                    Rasterable rasterable,
                    Networkable networkable)
    {
        super(services, setup);

        this.identifiable = identifiable;
        this.transformable = transformable;
        this.body = body;
        this.mirrorable = mirrorable;
        this.stateHandler = stateHandler;
        this.collidable = collidable;
        this.tileCollidable = tileCollidable;
        this.model = model;
        this.checker = checker;
        this.stats = stats;
        this.rasterable = rasterable;
        this.networkable = networkable;

        final HurtableConfig config = HurtableConfig.imports(setup);
        frame = config.getFrame();
        effect = config.getEffect();
        effectOffsetX = config.getOffsetX();
        persist = config.hasPersist();
        interrupt = config.hasInterrupt();
        fall = config.hasFall();
        sfx = config.getSfx().map(Sfx::valueOf).orElse(Sfx.MONSTER_HURT);
        boss = config.hasBoss();

        if (setup.getShade() == null || frame.isPresent() && frame.getAsInt() < 0)
        {
            shade = null;
        }
        else
        {
            shade = Drawable.loadSpriteAnimated(setup.getShade(),
                                                setup.getInteger(FramesConfig.ATT_HORIZONTAL, SetupEntity.NODE_SHADE),
                                                setup.getInteger(FramesConfig.ATT_VERTICAL, SetupEntity.NODE_SHADE));
            shade.setOrigin(OriginConfig.imports(setup));
        }

        hurtForce.setDestination(0.0, 0.0);
        hurtForce.setSensibility(0.5);
        hurtForce.setVelocity(0.14);
        hurtForceValue = config.getBackward().orElse(0.0);

        if (fall)
        {
            body.setGravity(0.0);
            body.setGravityMax(0.0);
            tileCollidable.setEnabled(false);
            collidable.setEnabled(true);
        }
    }

    /**
     * Trigger hurt effect.
     */
    public void hurt()
    {
        if (!invincibility)
        {
            stateHandler.changeState(StateHurt.class);
            recover.restart();
        }
    }

    /**
     * Trigger hurt effect with damages.
     */
    public void hurtDamages()
    {
        if (!invincibility && stats.getHealth() > 0)
        {
            if (stats.applyDamages(1))
            {
                stateHandler.changeState(StateDie.class);
            }
            else
            {
                stateHandler.changeState(StateHurt.class);
                Sfx.VALDYN_HURT.play();
                recover.restart();
                hurtJump();
            }
        }
    }

    /**
     * Set the enabled flag.
     * 
     * @param enabled The enabled flag.
     */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    /**
     * Set the shield flag.
     * 
     * @param shield The shield flag.
     */
    public void setShield(boolean shield)
    {
        this.shield = shield;
    }

    /**
     * Set invincibility flag.
     * 
     * @param invincibility <code>true</code> if invincibility, <code>false</code> else.
     */
    public void setInvincibility(boolean invincibility)
    {
        this.invincibility = invincibility;
    }

    /**
     * Set shade frame.
     * 
     * @param frame The shade frame.
     */
    public void setShading(int frame)
    {
        if (frame > 0)
        {
            shade.setFrame(frame);
            shading = true;
        }
        else
        {
            shading = false;
        }
    }

    /**
     * Set shade offset.
     * 
     * @param ox The horizontal offset.
     * @param oy The vertical offset.
     */
    public void setShadeOffset(int ox, int oy)
    {
        shade.setFrameOffsets(ox, oy);
    }

    /**
     * Check if hurting.
     * 
     * @return <code>true</code> if hurting, <code>false</code> else.
     */
    public boolean isHurting()
    {
        return !recover.elapsedTime(source.getRate(), HURT_RECOVER_ATTACK_DELAY_MS);
    }

    /**
     * Check if hurting body.
     * 
     * @return <code>true</code> if hurting, <code>false</code> else.
     */
    public boolean isHurtingBody()
    {
        return !recover.elapsedTime(source.getRate(), HURT_RECOVER_BODY_DELAY_MS);
    }

    /**
     * Get the hurt frame.
     * 
     * @return The hurt frame.
     */
    public OptionalInt getFrame()
    {
        return frame;
    }

    /**
     * Get the interrupt flag.
     * 
     * @return The interrupt flag.
     */
    public boolean hasInterrupt()
    {
        return interrupt;
    }

    /**
     * Update collidable checking.
     * 
     * @param collidable The collidable reference.
     * @param with The collision with.
     * @param by The collision by.
     */
    private void updateCollide(Collidable collidable, Collision with, Collision by)
    {
        if (enabled
            && !shield
            && (Constant.COLL_GROUP_PLAYER.equals(collidable.getGroup())
                && recover.elapsedTime(source.getRate(), HURT_RECOVER_BODY_DELAY_MS)
                || !Constant.COLL_GROUP_PLAYER.equals(this.collidable.getGroup())
                   && recover.elapsedTime(source.getRate(), HURT_RECOVER_ATTACK_DELAY_MS))
            && Double.compare(hurtForce.getDirectionHorizontal(), 0.0) == 0
            && with.getName().startsWith(CollisionName.BODY)
            && by.getName().startsWith(Anim.ATTACK))
        {
            updateCollideAttack(collidable, by);
        }
        if (!invincibility
            && !Constant.COLL_GROUP_PLAYER.equals(collidable.getGroup())
            && recover.elapsedTime(source.getRate(), HURT_RECOVER_BODY_DELAY_MS)
            && with.getName().startsWith(Anim.BODY)
            && by.getName().startsWith(Anim.ATTACK))
        {
            updateCollideBody(collidable);
        }
    }

    /**
     * Update collide with sword.
     * 
     * @param collidable The collidable reference.
     * @param by The collision by.
     */
    public void updateCollideAttack(Collidable collidable, Collision by)
    {
        sfx.play();
        int damages = collidable.getFeature(Stats.class).getDamages();
        if (by.getName().startsWith(Anim.ATTACK_FALL))
        {
            collidable.getFeature(EntityModel.class).jumpHit();
            if (!boss)
            {
                damages *= 2;
            }
        }
        if (stats.getHealthMax() > 0 && stats.applyDamages(damages))
        {
            onKilled();
        }
        if (model.getMovement().isDecreasingHorizontal())
        {
            mirrorable.mirror(Mirror.NONE);
        }

        if (stats.getHealth() > 0)
        {
            final int side = UtilMath.getSign(transformable.getX() - collidable.getFeature(Transformable.class).getX());
            if (Constant.COLL_GROUP_PLAYER.equals(this.collidable.getGroup())
                && Constant.COLL_GROUP_PLAYER.equals(collidable.getGroup()))
            {
                hurtForce.setDirection(1.2 * side, 0.0);
            }
            else
            {
                hurtForce.setDirection(hurtForceValue * side, 0.0);
            }
        }
        else if (hasFeature(Patrol.class))
        {
            getFeature(Patrol.class).stop();
        }
        hurt();

        if (Constant.COLL_GROUP_PLAYER.equals(this.collidable.getGroup()))
        {
            if (stats.getHealth() == 0)
            {
                Sfx.VALDYN_DIE.play();
                stateHandler.changeState(StateDie.class);
            }
            else
            {
                hurtJump();
            }
        }
    }

    private void onKilled()
    {
        if (fall)
        {
            oldGravity = body.getGravity();
            oldGravityMax = body.getGravityMax();
            body.setGravity(0.25);
            body.setGravityMax(4.0);
            tileCollidable.setEnabled(true);
            checker.setCheckerUpdate(() -> true);
        }
        currentCollide = CollidableListenerVoid.getInstance();
        model.getMovement().zero();
        collidable.setEnabled(false);
    }

    /**
     * Update collide with body.
     * 
     * @param collidable The collidable reference.
     */
    private void updateCollideBody(Collidable collidable)
    {
        collidable.ifIs(BulletDestroyOnPlayer.class, b -> b.ifIs(Hurtable.class, h -> h.kill(true)));

        if (stats.applyDamages(collidable.getFeature(Stats.class).getDamages()))
        {
            if (hasFeature(Trackable.class))
            {
                stateHandler.changeState(StateDie.class);
            }
        }
        else
        {
            if (hasFeature(Trackable.class))
            {
                Sfx.VALDYN_HURT.play();
                if (!stateHandler.isState(StateGripIdle.class)
                    && !stateHandler.isState(StateGripSoar.class)
                    && !stateHandler.isState(StateLianaSoar.class)
                    && !stateHandler.isState(StateLianaIdle.class)
                    && !stateHandler.isState(StateLianaWalk.class)
                    && !stateHandler.isState(StateAttackGrip.class)
                    && !stateHandler.isState(StateIdleAnimal.class))
                {
                    stateHandler.changeState(StateHurt.class);
                }
                hurtJump();
            }
        }
        recover.restart();
    }

    /**
     * Update tile collision checking.
     * 
     * @param result The collision result.
     * @param category The category reference.
     */
    private void updateTile(CollisionResult result, CollisionCategory category)
    {
        if (!invincibility
            && recover.elapsedTime(source.getRate(), HURT_RECOVER_BODY_DELAY_MS)
            && (category.getAxis() == Axis.Y && result.contains(CollisionName.SPIKE)
                || (category.getName().equals(CollisionName.KNEE_CENTER)
                    || category.getName().startsWith(CollisionName.KNEE_X_CENTER))
                   && result.contains(CollisionName.SPIKE)))
        {
            if (stats.applyDamages(SPIKE_DAMAGES))
            {
                if (hasFeature(Trackable.class))
                {
                    Sfx.VALDYN_DIE.play();
                    stateHandler.changeState(StateDie.class);
                }
                else
                {
                    kill();
                }
            }
            else
            {
                stateHandler.changeState(StateHurt.class);
                if (hasFeature(Trackable.class))
                {
                    Sfx.VALDYN_HURT.play();
                    hurtJump();
                }
            }
            recover.restart();
        }
        if (fall && stats.getHealth() == 0)
        {
            if (effect.isPresent())
            {
                spawner.spawn(effect.get(), transformable.getX(), transformable.getY() + transformable.getHeight() / 2)
                       .getFeature(Rasterable.class)
                       .setAnimOffset2(rasterable.getAnimOffset2());
            }
            identifiable.destroy();
        }
    }

    /**
     * Force jump on hurt.
     */
    private void hurtJump()
    {
        flicker.restart();
        flickerCurrent = this::updateFlicker;
    }

    /**
     * Update flicker effect on hurt.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFlicker(double extrp)
    {
        if (!stateHandler.isState(StateHurt.class))
        {
            rasterable.setVisibility(flicker.elapsedTime(source.getRate())
                                     % HURT_FLICKER_SWITCH_DELAY_MS < HURT_FLICKER_SWITCH_DELAY_MS / 2);
        }
        if (flicker.elapsedTime(source.getRate(), HURT_FLICKER_DURATION_MS))
        {
            flickerCurrent = UpdatableVoid.getInstance();
            rasterable.setVisibility(true);
        }
    }

    /**
     * Spawn effect and destroy.
     */
    public void kill()
    {
        kill(false);
    }

    /**
     * Spawn effect and destroy.
     * 
     * @param force <code>true</code> to kill forced, <code>false</code> else.
     */
    public void kill(boolean force)
    {
        if (force || stats.getHealthMax() > 0 && !fall)
        {
            if (effect.isPresent())
            {
                spawner.spawn(effect.get(),
                              transformable.getX() + effectOffsetX,
                              transformable.getY() + transformable.getHeight() / 2)
                       .getFeature(Rasterable.class)
                       .setAnimOffset2(rasterable.getAnimOffset2());
            }
            if (!persist)
            {
                identifiable.destroy();
            }
            model.getConfig().getNext().ifPresent(next -> stage.loadNextStage(next, NEXT_DELAY_MS));
        }

        syncKill(force);
    }

    private void syncKill(boolean force)
    {
        if (networkable.isOwner())
        {
            final ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES + 1);
            buffer.putInt(getSyncId());
            buffer.put(UtilConversion.fromUnsignedByte(UtilConversion.boolToInt(force)));
            networkable.send(buffer);
        }
    }

    @Override
    public void update(double extrp)
    {
        recover.update(extrp);
        flicker.update(extrp);

        hurtForce.update(extrp);
        flickerCurrent.update(extrp);
        model.getMovement().addDirection(extrp, hurtForce);

        if (fall && stats.getHealth() == 0 && transformable.getY() < mapWater.getCurrent())
        {
            kill(true);
        }
    }

    @Override
    public void render(Graphic g)
    {
        if (shading)
        {
            shade.setMirror(mirrorable.getMirror());
            shade.setLocation(viewer, transformable);
            shade.render(g);
        }
    }

    @Override
    public void onReceived(Packet packet)
    {
        kill(packet.readBool());
    }

    @Override
    public void notifyCollided(Collidable collidable, Collision with, Collision by)
    {
        currentCollide.notifyCollided(collidable, with, by);
    }

    @Override
    public void notifyTileCollided(CollisionResult result, CollisionCategory category)
    {
        currentTile.notifyTileCollided(result, category);
    }

    @Override
    public void recycle()
    {
        currentCollide = this::updateCollide;
        currentTile = this::updateTile;
        flickerCurrent = UpdatableVoid.getInstance();
        enabled = true;
        shading = false;
        if (fall)
        {
            body.setGravity(oldGravity);
            body.setGravityMax(oldGravityMax);
        }
        recover.restart();
        recover.set(HURT_RECOVER_BODY_DELAY_MS);
    }
}
