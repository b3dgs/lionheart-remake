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
package com.b3dgs.lionheart;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.game.Action;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.engine.Sequencer;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionengine.helper.DeviceControllerConfig;
import com.b3dgs.lionengine.helper.EntityChecker;
import com.b3dgs.lionengine.io.DeviceController;
import com.b3dgs.lionengine.io.DevicePointer;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.extro.Extro;
import com.b3dgs.lionheart.menu.Menu;
import com.b3dgs.lionheart.object.feature.Hurtable;
import com.b3dgs.lionheart.object.feature.Stats;
import com.b3dgs.lionheart.object.state.StateCrouch;
import com.b3dgs.lionheart.object.state.StateWin;

/**
 * Cheats implementation with menu.
 */
public class Cheats implements Updatable, Renderable
{
    private static final int CHEATS_WIDTH = 110;
    private static final int CHEATS_STAGE_WIDTH = 40;

    private static final int CURSOR_OX = -2;
    private static final int CURSOR_OY = -2;

    private static final int MOUSE_HIDE_DELAY_MS = 1000;
    private static final int EXTRO_DELAY_MS = 3000;

    private final List<CheatMenu> menus = new ArrayList<>();
    private final Tick tickMouse = new Tick();
    private final Tick tick;
    private final Services services;
    private final SourceResolutionProvider source;
    private final Sequencer sequencer;
    private final Camera camera;
    private final DeviceController device;
    private final DeviceController deviceCursor;
    private final DevicePointer pointer;
    private final Cursor cursor;
    private final Hud hud;
    private final ScreenShaker shaker;
    private final MusicPlayer music;
    private final GameConfig config;

    private Difficulty difficulty;
    private StateHandler player;
    private boolean paused;
    private boolean pressed;
    private boolean enabled;
    private boolean cheatsMenu;
    private boolean fly;
    private boolean invincibility;

    /**
     * Create cheats.
     * 
     * @param services The services reference.
     * @param tick The delayed action reference.
     */
    public Cheats(Services services, Tick tick)
    {
        this.tick = tick;
        this.services = services;
        source = services.get(SourceResolutionProvider.class);
        sequencer = services.get(Sequencer.class);
        camera = services.get(Camera.class);
        shaker = services.create(ScreenShaker.class);
        device = services.get(DeviceController.class);
        hud = services.get(Hud.class);
        music = services.get(MusicPlayer.class);
        config = services.get(GameConfig.class);

        final Media mediaCursor = Medias.create(Constant.INPUT_FILE_CURSOR);
        deviceCursor = DeviceControllerConfig.create(services, mediaCursor);

        final Context context = services.get(Context.class);
        pointer = (DevicePointer) context.getInputDevice(DeviceControllerConfig.imports(services, mediaCursor)
                                                                               .iterator()
                                                                               .next()
                                                                               .getDevice());

        cursor = services.create(Cursor.class);
        cursor.setArea(0, 0, camera.getWidth(), camera.getHeight());
        cursor.setViewer(camera);
        cursor.setVisible(false);
        cursor.setSync(pointer);
        cursor.setLock(pointer);
        cursor.addImage(0, Medias.create(Folder.SPRITE, "cursor.png"));
        cursor.setRenderingOffset(CURSOR_OX, CURSOR_OY);
        cursor.load();

        if (config.getType().is(GameType.STORY, GameType.TRAINING))
        {
            createMenu();
        }

        services.add(new CheatsProvider()
        {
            @Override
            public boolean isCheats()
            {
                return enabled;
            }

            @Override
            public boolean isFly()
            {
                return fly;
            }
        });
    }

    /**
     * Init cheats.
     * 
     * @param player The associated player.
     * @param difficulty The associated difficulty.
     * @param cheats The flag init.
     */
    public void init(StateHandler player, Difficulty difficulty, boolean cheats)
    {
        this.player = player;
        this.difficulty = difficulty;
        enabled = cheats;
        tickMouse.stop();
    }

    /**
     * Check if paused.
     * 
     * @return <code>true</code> if paused, <code>false</code> else.
     */
    public boolean isPaused()
    {
        return paused;
    }

    /**
     * Check if cursor is pressed.
     * 
     * @return <code>true</code> if pressed, <code>false</code> else.
     */
    private boolean isPressed()
    {
        return pressed;
    }

    /**
     * Get cheats enabled flag.
     * 
     * @return <code>true</code> if enabled, <code>false</code> else.
     */
    public boolean isEnabled()
    {
        return enabled;
    }

    /**
     * Make cheat label.
     * 
     * @param title The title label.
     * @param action The associated action.
     * @return The created label.
     */
    private CheatMenu make(String title, Action action)
    {
        return new CheatMenu(services, menus, this::isPressed, CHEATS_WIDTH, title, action);
    }

    /**
     * Create cheats menu.
     */
    private void createMenu()
    {
        menus.add(make("Heart More", this::onAddHeart));
        menus.add(make("Heart Less", this::onRemoveHeart));
        menus.add(make("Heart Fill", this::onFillHeart));
        menus.add(make("Sword More", this::onMoreSword));
        menus.add(make("Sword Less", this::onLessSword));
        menus.add(make("Fly", this::onFly));
        menus.add(make("Invincible", this::onInvincibility));

        final List<String> stagesSet = Util.readLines(Medias.create(Folder.STAGE, Folder.STORY, "stages.txt"));
        for (final String stages : stagesSet)
        {
            int stagesCount = 0;
            while (true)
            {
                if (!Util.getStage(stages, null, stagesCount + 1).exists())
                {
                    break;
                }
                stagesCount++;
            }
            final CheatMenu[] menu = new CheatMenu[stagesCount];
            final int l = CHEATS_STAGE_WIDTH;
            for (int i = 0; i < menu.length; i++)
            {
                final int index = i;
                menu[i] = new CheatMenu(services,
                                        menus,
                                        this::isPressed,
                                        l,
                                        String.valueOf(i + 1),
                                        () -> onStage(stages, index));
            }
            menus.add(new CheatMenu(services, menus, this::isPressed, CHEATS_WIDTH, "Stage " + stages, null, menu));
        }
    }

    /**
     * Update pause checking.
     */
    private void updatePause()
    {
        if (config.getType().is(GameType.STORY, GameType.TRAINING) && device.isFiredOnce(DeviceMapping.PAUSE))
        {
            paused = !paused;
            hud.setPaused(paused);
        }
    }

    /**
     * Update quit checking.
     */
    private void updateQuit()
    {
        if (device.isFiredOnce(DeviceMapping.QUIT))
        {
            if (config.getType().is(GameType.STORY))
            {
                if (paused)
                {
                    sequencer.end(Menu.class, config);
                }
                paused = !paused;
                hud.setExit(paused);
            }
            else
            {
                sequencer.end(Menu.class, config.with((InitConfig) null));
            }
        }
    }

    private void onAddHeart()
    {
        player.getFeature(Stats.class).increaseMaxHealth();
    }

    private void onRemoveHeart()
    {
        player.getFeature(Stats.class).decreaseMaxHealth();
    }

    private void onFillHeart()
    {
        player.getFeature(Stats.class).fillHealth();
    }

    private void onMoreSword()
    {
        player.getFeature(Stats.class).applySword(player.getFeature(Stats.class).getSword() + 1, false);
    }

    private void onLessSword()
    {
        player.getFeature(Stats.class).applySword(player.getFeature(Stats.class).getSword() - 1, true);
    }

    private void onFly()
    {
        enabled = true;
        fly = !fly;
        unlockPlayer(fly);
        cursor.setInputDevice(deviceCursor);
        cursor.setSync(null);
        sequencer.setSystemCursorVisible(false);
    }

    private void onInvincibility()
    {
        invincibility = !invincibility;
        player.getFeature(Hurtable.class).setInvincibility(invincibility);
    }

    private void onStage(String stages, int index)
    {
        sequencer.end(SceneBlack.class,
                      config.with(Util.getInitConfig(Util.getStage(stages, difficulty, index + 1),
                                                     player,
                                                     difficulty,
                                                     enabled,
                                                     Optional.empty())));
    }

    /**
     * Close cheats menu.
     */
    private void closeMenu()
    {
        cheatsMenu = false;
        for (int i = 0; i < menus.size(); i++)
        {
            menus.get(i).hide();
        }
        pressed = false;
    }

    /**
     * Update original cheats activation.
     */
    private void updateOriginal()
    {
        if (paused && device.isFired(DeviceMapping.CHEAT))
        {
            if (!player.isState(StateCrouch.class))
            {
                paused = false;
                hud.setPaused(false);
            }
            else if (device.isFiredOnce(DeviceMapping.PAGE_DOWN))
            {
                device.isFiredOnce(DeviceMapping.CHEAT);
                enabled = !enabled;
                shaker.start();
                paused = false;
                hud.setPaused(false);
            }
        }
        else if (enabled && !player.isState(StateWin.class))
        {
            updateCheatsFly();
            updateStages();
        }
    }

    /**
     * Update cheats activation with menu.
     */
    private void updateMenu()
    {
        if (cheatsMenu)
        {
            pressed = deviceCursor.isFiredOnce(DeviceMapping.LEFT);
        }
        else if (!fly)
        {
            if (tickMouse.elapsedTime(source.getRate(), MOUSE_HIDE_DELAY_MS))
            {
                tickMouse.stop();
                sequencer.setSystemCursorVisible(false);
            }
            else if (Double.compare(cursor.getMoveX(), 0.0) != 0 || Double.compare(cursor.getMoveY(), 0.0) != 0)
            {
                tickMouse.restart();
                sequencer.setSystemCursorVisible(true);
            }
            if (deviceCursor.isFiredOnce(DeviceMapping.RIGHT))
            {
                cheatsMenu = true;
                cursor.setInputDevice(null);
                sequencer.setSystemCursorVisible(true);

                Util.showMenu(camera, cursor, menus, 0, 0);
            }
        }
        else if (deviceCursor.isFiredOnce(DeviceMapping.RIGHT))
        {
            fly = false;
            unlockPlayer(fly);
            cursor.setInputDevice(null);
            cursor.setSync(pointer);
            sequencer.setSystemCursorVisible(true);
        }
    }

    /**
     * Update fly mode cheat.
     */
    private void updateCheatsFly()
    {
        if (device.isFiredOnce(DeviceMapping.CHEAT))
        {
            fly = !fly;
            unlockPlayer(fly);
            if (fly)
            {
                cursor.setInputDevice(deviceCursor);
                cursor.setSync(null);
                sequencer.setSystemCursorVisible(false);
            }
            else
            {
                cursor.setInputDevice(null);
                cursor.setSync(pointer);
                sequencer.setSystemCursorVisible(true);
            }
        }
        if (fly && !cheatsMenu)
        {
            player.getFeature(Transformable.class)
                  .moveLocation(1.0, deviceCursor.getHorizontalDirection(), deviceCursor.getVerticalDirection());
        }
    }

    /**
     * Unlock player for cheats.
     * 
     * @param unlock <code>true</code> to unlock, <code>false</code> else.
     */
    private void unlockPlayer(boolean unlock)
    {
        player.getFeature(TileCollidable.class).setEnabled(!unlock);
        player.getFeature(Collidable.class).setEnabled(!unlock);
        player.getFeature(EntityChecker.class).setCheckerUpdate(() -> !unlock);
    }

    /**
     * Update stage jumping cheat.
     */
    private void updateStages()
    {
        if (config.getStages().isPresent())
        {
            final String s = config.getStages().get();
            for (int i = 0; i < Stage.values().length; i++)
            {
                if (device.isFiredOnce(Integer.valueOf(i + DeviceMapping.F1.getIndex().intValue())))
                {
                    final Media stage = Util.getStage(s, difficulty, i + 1);
                    jumpToStage(stage);
                }
            }
        }
        if (device.isFiredOnce(DeviceMapping.K5))
        {
            player.getFeature(Stats.class).win();
            tick.addAction(() ->
            {
                music.stopMusic();
                sequencer.end(Extro.class, config, player.getFeature(Stats.class).hasAmulet());
            }, source.getRate(), EXTRO_DELAY_MS);
        }
    }

    /**
     * Jump to stage if exists.
     * 
     * @param stage The stage reference.
     */
    private void jumpToStage(Media stage)
    {
        if (stage.exists())
        {
            sequencer.end(SceneBlack.class,
                          config.with(Util.getInitConfig(stage, player, difficulty, enabled, Optional.empty())));
        }
    }

    /**
     * Called when the resolution changed. Does nothing by default.
     * 
     * @param width The new screen width.
     * @param height The new screen height.
     */
    public void onResolutionChanged(int width, int height)
    {
        cursor.setArea(0, 0, camera.getWidth(), camera.getHeight());
    }

    @Override
    public void update(double extrp)
    {
        tickMouse.update(extrp);
        deviceCursor.update(extrp);
        cursor.update(extrp);
        shaker.update(extrp);

        updatePause();
        updateQuit();

        if (config.getType().is(GameType.STORY, GameType.TRAINING) && player != null)
        {
            updateOriginal();
            updateMenu();
        }

        for (int i = 0; i < menus.size(); i++)
        {
            menus.get(i).updateSub(extrp);
            if (menus.get(i).isHoverSub())
            {
                for (int j = 0; j < menus.size(); j++)
                {
                    menus.get(j).setInactive();
                }
                break;
            }
        }

        for (int i = 0; i < menus.size(); i++)
        {
            menus.get(i).update(extrp);
        }
        if (pressed)
        {
            closeMenu();
        }
    }

    @Override
    public void render(Graphic g)
    {
        for (int i = 0; i < menus.size(); i++)
        {
            menus.get(i).render(g);
        }
        cursor.render(g);
    }
}
