/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionheart.extro;

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.AnimatorStateListener;
import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UpdatableVoid;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.ComponentDisplayable;
import com.b3dgs.lionengine.game.feature.ComponentRefreshable;
import com.b3dgs.lionengine.game.feature.DisplayableModel;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.RefreshableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Spawner;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionengine.game.feature.rasterable.RasterableModel;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.RenderableVoid;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;
import com.b3dgs.lionengine.graphic.engine.Sequence;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionDelegate;
import com.b3dgs.lionengine.helper.DeviceControllerConfig;
import com.b3dgs.lionengine.io.DeviceController;
import com.b3dgs.lionheart.AppInfo;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.DeviceMapping;
import com.b3dgs.lionheart.GameConfig;
import com.b3dgs.lionheart.Time;
import com.b3dgs.lionheart.Util;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.constant.Folder;

/**
 * Extro part 5 implementation.
 */
// CHECKSTYLE IGNORE LINE: FanOutComplexity|DataAbstractionCoupling
public class Part5 extends Sequence
{
    private static final Animation OPEN = new Animation("open", 1, 8, 0.18, false, false);
    private static final int FADE_SPEED = 5;

    private static final String PART5_FOLDER = "part5";
    private static final String FILE_TRANSFORM1 = "Transform1.xml";
    private static final String FILE_TRANSFORM2 = "Transform2.xml";
    private static final String FILE_TRANSFORM3 = "Transform3.xml";
    private static final String FILE_TRANSFORM4 = "Transform4.xml";

    private static final int TRANSFORM_Y = 24;
    private static final int TRANSFORM_FLICK_COUNT = 7;
    private static final int TRANSFORM_FLICK_DELAY_MS = 150;
    private static final int EYE_Y = 57;

    private static final int TIME_TRANSFORM1_MS = 141_600;
    private static final int TIME_TRANSFORM_ALPHA_IN_MS = 142_500;
    private static final int TIME_TRANSFORM2_MS = 143_400;
    private static final int TIME_TRANSFORM3_MS = 144_300;
    private static final int TIME_TRANSFORM4_MS = 145_900;
    private static final int TIME_TRANSFORM5_MS = 148_700;
    private static final int TIME_TRANSFORM6_MS = 149_700;
    private static final int TIME_TRANSFORM7_MS = 150_700;
    private static final int TIME_TRANSFORM_FLICKER_MS = 151_900;
    private static final int TIME_TRANSFORM8_MS = 153_600;
    private static final int TIME_TRANSFORM_ALPHA_OUT_MS = 155_000;
    private static final int TIME_TRANSFORM_EYES_MS = 156_000;

    private static final int TIME_FADE_OUT_MS = 160_300;

    private final Sprite transform0a = get("transform0a.png");
    private final Sprite transform0b = get("transform0b.png");
    private final Sprite transform0c = get("transform0c.png");
    private final SpriteAnimated eyes = get("eyes.png", 2, 4);

    private static Sprite get(String file)
    {
        return Drawable.loadSprite(Medias.create(Folder.EXTRO, PART5_FOLDER, file));
    }

    private static SpriteAnimated get(String file, int horizontalFrames, int verticalFrames)
    {
        return Drawable.loadSpriteAnimated(Medias.create(Folder.EXTRO, PART5_FOLDER, file),
                                           horizontalFrames,
                                           verticalFrames);
    }

    /** Device controller reference. */
    final DeviceController device;
    /** Alpha speed. */
    int alphaSpeed = FADE_SPEED;

    private final Services services = new Services();
    private final Factory factory = services.create(Factory.class);
    private final Handler handler = services.create(Handler.class);
    private final Camera camera = services.create(Camera.class);
    private final Spawner spawner = services.add((Spawner) (media, x, y) ->
    {
        final Featurable featurable = factory.create(media);
        featurable.getFeature(Transformable.class).teleport(x, y);
        final Animatable animatable = featurable.getFeature(Animatable.class);
        animatable.play(AnimationConfig.imports(new Configurer(featurable.getMedia())).getAnimation(Anim.IDLE));
        animatable.addListener((AnimatorStateListener) state ->
        {
            if (AnimState.FINISHED == state)
            {
                featurable.getFeature(Identifiable.class).destroy();
            }
        });
        final Rasterable rasterable = featurable.getFeature(RasterableModel.class);
        featurable.addFeature(new RefreshableModel(extrp ->
        {
            rasterable.update(extrp);
            animatable.update(extrp);
        }));
        featurable.addFeature(new DisplayableModel(rasterable));
        return featurable;
    });
    private final int x = getWidth() / 2 - 2;
    private final int y = getHeight() / 2 - 54;
    private final Featurable[] effects = new Featurable[]
    {
        spawner.spawn(Medias.create(Folder.EXTRO, PART5_FOLDER, FILE_TRANSFORM1), x, y),
        spawner.spawn(Medias.create(Folder.EXTRO, PART5_FOLDER, FILE_TRANSFORM2), x + 4, y),
        spawner.spawn(Medias.create(Folder.EXTRO, PART5_FOLDER, FILE_TRANSFORM3), x - 1, y),
        spawner.spawn(Medias.create(Folder.EXTRO, PART5_FOLDER, FILE_TRANSFORM4), x - 2, y + 116),
        spawner.spawn(Medias.create(Folder.EXTRO, PART5_FOLDER, FILE_TRANSFORM1), x - 40, y + 32),
        spawner.spawn(Medias.create(Folder.EXTRO, PART5_FOLDER, FILE_TRANSFORM1), x, y + 32),
        spawner.spawn(Medias.create(Folder.EXTRO, PART5_FOLDER, FILE_TRANSFORM1), x + 40, y + 32),
        spawner.spawn(Medias.create(Folder.EXTRO, PART5_FOLDER, FILE_TRANSFORM1), x, y),
    };
    private final AppInfo info;
    private final Time time;
    private final Audio audio;
    private final Tick tick = new Tick();

    private Updatable updaterFade = this::updateFadeIn;
    private Updatable updaterTransform = this::updateTransform1;

    private Renderable rendererFade = this::renderFade;

    private double alpha = 255.0;
    private double alphaTransform;
    private int flicked0c;
    private int effect;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     * @param config The game config reference.
     * @param time The time reference.
     * @param audio The audio reference.
     * @param alternative The alternative end.
     */
    public Part5(Context context, GameConfig config, Time time, Audio audio, Boolean alternative)
    {
        super(context, Util.getResolution(Constant.RESOLUTION, context), Util.getLoop(context.getConfig().getOutput()));

        this.time = time;
        this.audio = audio;

        services.add(new SourceResolutionDelegate(this::getWidth, this::getHeight, this::getRate));
        camera.setView(0, 0, getWidth(), getHeight(), getHeight());

        services.add(context);
        device = services.add(DeviceControllerConfig.create(services, Medias.create(Constant.INPUT_FILE_DEFAULT)));
        info = new AppInfo(this::getFps, services);

        handler.addComponent(new ComponentRefreshable());
        handler.addComponent(new ComponentDisplayable());
        handler.addListener(factory);

        load(Credits.class, config, time, audio, alternative);

        setSystemCursorVisible(false);
        Util.setFilter(this, context, Util.getResolution(Constant.RESOLUTION, context), 2);
    }

    /**
     * Update fade in effect.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFadeIn(double extrp)
    {
        alpha -= alphaSpeed * extrp;

        if (getAlpha() < 0)
        {
            alpha = 0.0;
            updaterFade = this::updateFadeOutInit;
            rendererFade = RenderableVoid.getInstance();
        }
    }

    /**
     * Update fade out time.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFadeOutInit(double extrp)
    {
        if (time.isAfter(TIME_FADE_OUT_MS))
        {
            updaterFade = this::updateFadeOut;
            rendererFade = this::renderFade;
        }
    }

    /**
     * Update fade out effect.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFadeOut(double extrp)
    {
        alpha += alphaSpeed * extrp;

        if (getAlpha() > 255)
        {
            alpha = 255.0;
            end();
            updaterFade = UpdatableVoid.getInstance();
        }
    }

    /**
     * Update transform 1.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateTransform1(double extrp)
    {
        if (time.isAfter(TIME_TRANSFORM1_MS))
        {
            handler.add(effects[effect++]);
            updaterTransform = this::updateTransformAlphaInInit;
        }
    }

    /**
     * Update transform alpha in time.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateTransformAlphaInInit(double extrp)
    {
        if (time.isAfter(TIME_TRANSFORM_ALPHA_IN_MS))
        {
            updaterTransform = this::updateTransformAlphaIn;
        }
    }

    /**
     * Update transform alpha in effect.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateTransformAlphaIn(double extrp)
    {
        final int old = (int) Math.floor(alphaTransform);
        alphaTransform += alphaSpeed * extrp;

        if (alphaTransform > 255.0)
        {
            alphaTransform = 255.0;
            updaterTransform = this::updateTransform2;
        }
        if ((int) Math.floor(alphaTransform) != old)
        {
            transform0b.setAlpha((int) Math.floor(alphaTransform));
        }
    }

    /**
     * Update transform 2.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateTransform2(double extrp)
    {
        if (time.isAfter(TIME_TRANSFORM2_MS))
        {
            handler.add(effects[effect++]);
            updaterTransform = this::updateTransform3;
        }
    }

    /**
     * Update transform 3.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateTransform3(double extrp)
    {
        if (time.isAfter(TIME_TRANSFORM3_MS))
        {
            handler.add(effects[effect++]);
            updaterTransform = this::updateTransform4;
        }
    }

    /**
     * Update transform 4.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateTransform4(double extrp)
    {
        if (time.isAfter(TIME_TRANSFORM4_MS))
        {
            handler.add(effects[effect++]);
            updaterTransform = this::updateTransform5;
        }
    }

    /**
     * Update transform 5.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateTransform5(double extrp)
    {
        if (time.isAfter(TIME_TRANSFORM5_MS))
        {
            handler.add(effects[effect++]);
            updaterTransform = this::updateTransform6;
        }
    }

    /**
     * Update transform 6.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateTransform6(double extrp)
    {
        if (time.isAfter(TIME_TRANSFORM6_MS))
        {
            handler.add(effects[effect++]);
            updaterTransform = this::updateTransform7;
        }
    }

    /**
     * Update transform 7.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateTransform7(double extrp)
    {
        if (time.isAfter(TIME_TRANSFORM7_MS))
        {
            handler.add(effects[effect++]);
            updaterTransform = this::updateTransformFlickerInit;
        }
    }

    /**
     * Update transform flicker time.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateTransformFlickerInit(double extrp)
    {
        if (time.isAfter(TIME_TRANSFORM_FLICKER_MS))
        {
            updaterTransform = this::updateTransformFlicker;
            tick.restart();
        }
    }

    /**
     * Update transform flicker effect.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateTransformFlicker(double extrp)
    {
        tick.update(extrp);
        if (tick.elapsedTime(getRate(), TRANSFORM_FLICK_DELAY_MS))
        {
            flicked0c++;
            tick.restart();
        }
        if (flicked0c > TRANSFORM_FLICK_COUNT)
        {
            updaterTransform = this::updateTransform8;
        }
    }

    /**
     * Update transform 8.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateTransform8(double extrp)
    {
        if (time.isAfter(TIME_TRANSFORM8_MS))
        {
            handler.add(effects[effect++]);
            updaterTransform = this::updateTransformAlphaOutInit;
        }
    }

    /**
     * Update transform alpha out time.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateTransformAlphaOutInit(double extrp)
    {
        if (time.isAfter(TIME_TRANSFORM_ALPHA_OUT_MS))
        {
            updaterTransform = this::updateTransformAlphaOut;
        }
    }

    /**
     * Update transform alpha out effect.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateTransformAlphaOut(double extrp)
    {
        final int old = (int) Math.floor(alphaTransform);
        alphaTransform -= alphaSpeed * extrp;

        if (getAlphaTransform() < 0)
        {
            alphaTransform = 0.0;
            updaterTransform = this::updateTransformOpenEyesInit;
        }
        if ((int) Math.floor(alphaTransform) != old)
        {
            transform0b.setAlpha(getAlphaTransform());
        }
    }

    /**
     * Update transform open eyes.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateTransformOpenEyesInit(double extrp)
    {
        if (time.isAfter(TIME_TRANSFORM_EYES_MS))
        {
            updaterTransform = this::updateTransformOpenEyes;
        }
    }

    /**
     * Update transform open eyes.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateTransformOpenEyes(double extrp)
    {
        eyes.play(OPEN);
        updaterTransform = UpdatableVoid.getInstance();
    }

    /**
     * Get alpha value.
     * 
     * @return The alpha value.
     */
    private int getAlpha()
    {
        return (int) Math.floor(alpha);
    }

    /**
     * Get alpha transform value.
     * 
     * @return The alpha transform value.
     */
    private int getAlphaTransform()
    {
        return (int) Math.floor(alphaTransform);
    }

    /**
     * Render fade effect.
     * 
     * @param g The graphic output.
     */
    private void renderFade(Graphic g)
    {
        final int a = getAlpha();
        if (a > 0)
        {
            g.setColor(Constant.ALPHAS_BLACK[a]);
            g.drawRect(0, 0, getWidth(), getHeight(), true);
            g.setColor(ColorRgba.BLACK);
        }
    }

    @Override
    public void load()
    {
        transform0a.load();
        transform0a.prepare();
        transform0a.setOrigin(Origin.CENTER_TOP);

        transform0b.load();
        transform0b.prepare();
        transform0b.setOrigin(Origin.CENTER_TOP);
        transform0b.setAlpha(0);

        transform0c.load();
        transform0c.prepare();
        transform0c.setOrigin(Origin.CENTER_TOP);

        eyes.load();
        eyes.prepare();
        eyes.setOrigin(Origin.CENTER_TOP);
    }

    @Override
    public void update(double extrp)
    {
        time.update(extrp);
        handler.update(extrp);
        eyes.update(extrp);
        updaterTransform.update(extrp);
        updaterFade.update(extrp);
        info.update(extrp);

        if (device.isFiredOnce(DeviceMapping.FORCE_EXIT))
        {
            end(null);
        }
    }

    @Override
    public void render(Graphic g)
    {
        g.clear(0, 0, getWidth(), getHeight());

        transform0a.setLocation(getWidth() / 2, TRANSFORM_Y);
        transform0a.render(g);

        if (flicked0c % 2 == 1 || flicked0c > TRANSFORM_FLICK_COUNT)
        {
            transform0c.setLocation(getWidth() / 2, TRANSFORM_Y);
            transform0c.render(g);

            eyes.setLocation(getWidth() / 2, EYE_Y);
            eyes.render(g);
        }

        transform0b.setLocation(getWidth() / 2, TRANSFORM_Y);
        transform0b.render(g);

        handler.render(g);

        rendererFade.render(g);

        info.render(g);
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        super.onTerminated(hasNextSequence);

        if (!hasNextSequence)
        {
            audio.stop();
            Engine.terminate();
        }
    }
}
