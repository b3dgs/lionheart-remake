/*
 * Copyright (C) 2013-2026 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionheart.narration.extro;

import java.io.Closeable;
import java.util.function.Consumer;

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.AnimatorStateListener;
import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.game.AnimationsConfig;
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
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionDelegate;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.FadeSide;
import com.b3dgs.lionheart.Util;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.narration.NarrationFactory;

/**
 * Extro part 5 implementation.
 */
// CHECKSTYLE IGNORE LINE: FanOutComplexity|DataAbstractionCoupling
public final class Part5 implements Closeable
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

    private static final int T_FADE_IN = 140_000;
    private static final int T_TRANSFORM1 = 141_600;
    private static final int T_TRANSFORM_ALPHA_IN = 142_500;
    private static final int T_TRANSFORM2 = 143_400;
    private static final int T_TRANSFORM3 = 144_300;
    private static final int T_TRANSFORM4 = 145_900;
    private static final int T_TRANSFORM5 = 148_700;
    private static final int T_TRANSFORM6 = 149_700;
    private static final int T_TRANSFORM7 = 150_700;
    private static final int T_TRANSFORM_FLICKER = 151_900;
    private static final int T_TRANSFORM8 = 153_600;
    private static final int T_TRANSFORM_ALPHA_OUT = 155_000;
    private static final int T_TRANSFORM_EYES = 156_000;

    private static final int T_FADE_OUT = 160_300;

    private final Resolution resolution;

    private final Sprite transform0a = Util.get(Folder.EXTRO, PART5_FOLDER, "transform0a.png");
    private final Sprite transform0b = Util.get(Folder.EXTRO, PART5_FOLDER, "transform0b.png");
    private final Sprite transform0c = Util.get(Folder.EXTRO, PART5_FOLDER, "transform0c.png");
    private final SpriteAnimated eyes = Util.get(2, 4, Folder.EXTRO, PART5_FOLDER, "eyes.png");

    private final Services services = new Services();
    private final Factory factory = services.create(Factory.class);
    private final Handler handler = services.create(Handler.class);
    private final Camera camera = services.create(Camera.class);
    private final Spawner spawner = services.add((Spawner) (media, x, y) ->
    {
        final Featurable featurable = factory.create(media);
        featurable.getFeature(Transformable.class).teleport(x, y);
        final Animatable animatable = featurable.getFeature(Animatable.class);
        animatable.play(AnimationsConfig.imports(new Configurer(featurable.getMedia())).get(Anim.IDLE));
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
    private final Featurable[] effects;
    private final Tick tick = new Tick();
    private final int width;
    private final int height;
    private final int rate;

    private double alphaTransform;
    private int flicked0c;
    private int effect;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public Part5(Context context)
    {
        super();

        resolution = Util.getResolution(Constant.RESOLUTION, context);
        width = resolution.width();
        height = resolution.height();
        rate = resolution.rate();

        final double x = resolution.width() / 2.0 - 2;
        final double y = height / 2.0 - 54;

        effects = new Featurable[]
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

        services.add(new SourceResolutionDelegate(resolution::width, resolution::height, resolution::rate));
        camera.setView(0, 0, width, height, height);

        services.add(context);

        handler.addComponent(new ComponentRefreshable());
        handler.addComponent(new ComponentDisplayable());
        handler.addListener(factory);
    }

    /**
     * Init resolution.
     * 
     * @param source The source resolution.
     */
    public void initResolution(Consumer<Resolution> source)
    {
        source.accept(resolution);
    }

    /**
     * Load part.
     * 
     * @param n The narration factory.
     * @param source The resolution source.
     */
    public void load(NarrationFactory n, Consumer<Resolution> source)
    {
        transform0a.setOrigin(Origin.CENTER_TOP);
        transform0a.setLocation(width / 2, TRANSFORM_Y);

        transform0b.setOrigin(Origin.CENTER_TOP);
        transform0b.setLocation(width / 2, TRANSFORM_Y);
        transform0b.setAlpha(0);

        transform0c.setOrigin(Origin.CENTER_TOP);
        transform0c.setLocation(width / 2, TRANSFORM_Y);

        eyes.setOrigin(Origin.CENTER_TOP);
        eyes.setLocation(width / 2, EYE_Y);

        init(n, source);
    }

    private void init(NarrationFactory n, Consumer<Resolution> source)
    {
        n.add(T_FADE_IN, () -> initResolution(source));
        n.add(T_FADE_IN, T_FADE_OUT + 3000, this::update, this::render);
        n.add(T_FADE_IN, FadeSide.IN, FADE_SPEED);
        n.add(T_TRANSFORM1, () -> handler.add(effects[effect++]));
        n.add(T_TRANSFORM_ALPHA_IN, T_TRANSFORM2, this::updateTransformAlphaIn);
        n.add(T_TRANSFORM2, () -> handler.add(effects[effect++]));
        n.add(T_TRANSFORM3, () -> handler.add(effects[effect++]));
        n.add(T_TRANSFORM4, () -> handler.add(effects[effect++]));
        n.add(T_TRANSFORM5, () -> handler.add(effects[effect++]));
        n.add(T_TRANSFORM6, () -> handler.add(effects[effect++]));
        n.add(T_TRANSFORM7, () -> handler.add(effects[effect++]));
        n.add(T_TRANSFORM_FLICKER, tick::restart);
        n.add(T_TRANSFORM_FLICKER, T_TRANSFORM8, this::updateTransformFlicker);
        n.add(T_TRANSFORM8, () -> handler.add(effects[effect++]));
        n.add(T_TRANSFORM_ALPHA_OUT, T_TRANSFORM_EYES, this::updateTransformAlphaOut);
        n.add(T_TRANSFORM_EYES, () -> eyes.play(OPEN));
        n.add(T_FADE_OUT, FadeSide.OUT, FADE_SPEED);
    }

    @Override
    public void close()
    {
        transform0a.dispose();
        transform0b.dispose();
        transform0c.dispose();
        eyes.dispose();
    }

    /**
     * Update transform alpha in effect.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateTransformAlphaIn(double extrp)
    {
        final int old = (int) Math.floor(alphaTransform);
        alphaTransform += FADE_SPEED * extrp;

        if (alphaTransform > 255.0)
        {
            alphaTransform = 255.0;
        }
        if ((int) Math.floor(alphaTransform) != old)
        {
            transform0b.setAlpha((int) Math.floor(alphaTransform));
        }
    }

    /**
     * Update transform flicker effect.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateTransformFlicker(double extrp)
    {
        if (flicked0c <= TRANSFORM_FLICK_COUNT)
        {
            tick.update(extrp);
            if (tick.elapsedTime(rate, TRANSFORM_FLICK_DELAY_MS))
            {
                flicked0c++;
                tick.restart();
            }
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
        alphaTransform -= FADE_SPEED * extrp;

        if (getAlphaTransform() < 0)
        {
            alphaTransform = 0.0;
        }
        if ((int) Math.floor(alphaTransform) != old)
        {
            transform0b.setAlpha(getAlphaTransform());
        }
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

    private void update(double extrp)
    {
        handler.update(extrp);
        eyes.update(extrp);
    }

    private void render(Graphic g)
    {
        g.clear(0, 0, width, height);

        transform0a.render(g);

        if (flicked0c % 2 == 1 || flicked0c > TRANSFORM_FLICK_COUNT)
        {
            transform0c.render(g);
            eyes.render(g);
        }

        transform0b.render(g);
        handler.render(g);
    }
}
