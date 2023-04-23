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
package com.b3dgs.lionheart.object.feature;

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Animator;
import com.b3dgs.lionengine.AnimatorModel;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.UtilRandom;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.Factory;
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
import com.b3dgs.lionengine.game.feature.launchable.Launcher;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionengine.graphic.engine.Sequencer;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionheart.GameConfig;
import com.b3dgs.lionheart.Music;
import com.b3dgs.lionheart.MusicPlayer;
import com.b3dgs.lionheart.ScreenShaker;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.extro.Extro;

/**
 * Boss Norka feature implementation.
 * <ol>
 * <li>Raise.</li>
 * <li>Move right.</li>
 * <li>Jump.</li>
 * <li>Fall.</li>
 * <li>Raise.</li>
 * <li>Move right.</li>
 * <li>Jump.</li>
 * <li>Throws.</li>
 * <li>Fall.</li>
 * </ol>
 */
@FeatureInterface
public final class BossNorka extends FeatureModel implements Routine, Recyclable
{
    private static final int END_DELAY_MS = 8000;
    private static final double CURVE_SPEED = 9.5;
    private static final double CURVE_AMPLITUDE = 3.0;
    private static final int Y = 60;
    private static final int WALK_OFFSET = 47;
    private static final int JUMP_DELAY_MS = 5000;
    private static final int AWAIT_DELAY_MS = 1000;
    private static final int FIRE_DELAY_MS = 5000;
    // @formatter:off
    private static final int[] PATROLS = {WALK_OFFSET * 2, 0, -WALK_OFFSET * 2, 0};
    // @formatter:on
    private static final String[] LIMBS =
    {
        "NorkaHead", "NorkaBowl", "NorkaBowl", "NorkaBowl", "NorkaLeg", "NorkaBowl", "NorkaBowl", "NorkaBowl",
        "NorkaLeg"
    };

    // @formatter:on

    /**
     * Get random horizontal direction.
     * 
     * @return The random horizontal direction.
     */
    private static double getRandomDirectionX()
    {
        final int vx = UtilRandom.getRandomInteger(3);
        if (UtilRandom.getRandomBoolean())
        {
            return vx - 4.2;
        }
        return vx + 0.6;
    }

    // @formatter:off
    private final int[][] data =
    {
        // Rise
        {0, -22, -16, -16, -16, 22, 28, 28, 32},
        {0, 3, 6, 6, 6, 3, 6, 6, 6},

        {-3, -25, -17, -17, -18, 20, 27, 29, 32},
        {-3, -1, 7, 7, 7, 2, 6, 6, 6},

        {-5, -26, -18, -18, -18, 19, 27, 30, 34},
        {-4, -1, 7, 7, 7, 1, 5, 6, 6},

        {-8, -27, -19, -19, -19, 18, 26, 30, 35},
        {-6, -2, 7, 7, 7, 1, 5, 6, 6},

        {-9, -29, -22, -22, -22, 13, 22, 31, 35},
        {-10, -4, 5, 6, 6, -4, 4, 6, 6},

        {-10, -30, -26, -22, -22, 12, 22, 30, 35},
        {-11, -5, 1, 6, 6, -4, 3, 5, 6},

        {-8, -31, -28, -25, -25, 11, 19, 29, 35},
        {-15, -7, 0, 6, 6, -6, 2, 6, 6},

        {-6, -30, -30, -26, -25, 9, 20, 29, 35},
        {-17, -10, -2, 5, 6, -8, 1, 5, 6},

        {0, -25, -29, -28, -24, 12, 20, 29, 36},
        {-21, -11, -5, 4, 6, -10, 1, 6, 6},

        {3, -24, -28, -28, -24, 15, 18, 28, 36},
        {-24, -12, -5, 1, 6, -13, 1, 5, 6},

        {10, -19, -26, -27, -25, 23, 18, 29, 35},
        {-27, -16, -9, 0, 6, -15, -3, 5, 6},

        {12, -18, -24, -27, -25, 27, 20, 28, 35},
        {-30, -18, -8, 0, 6, -18, -4, 5, 6},

        {15, -13, -22, -26, -25, 33, 22, 28, 35},
        {-33, -22, -12, -3, 6, -21, -8, 4, 6},

        {17, -11, -22, -26, -25, 35, 25, 29, 35},
        {-36, -25, -14, -3, 6, -25, -10, 4, 6},

        {21, -7, -18, -25, -25, 38, 27, 30, 35},
        {-39, -29, -17, -5, 6, -27, -14, 1, 6},

        {21, -6, -18, -25, -25, 39, 29, 30, 34},
        {-41, -30, -18, -6, 6, -28, -15, 0, 6},

        {22, -2, -15, -23, -25, 42, 32, 34, 34},
        {-44, -33, -20, -7, 6, -31, -17, -3, 6},

        {22, -1, -13, -22, -25, 43, 34, 35, 34},
        {-45, -33, -22, -9, 6, -33, -18, -4, 6},

        {20, 0, -12, -21, -24, 44, 39, 37, 35},
        {-48, -34, -22, -10, 6, -35, -19, -5, 6},

        {18, -2, -10, -20, -23, 43, 41, 37, 37},
        {-49, -36, -23, -9, 6, -37, -20, -5, 6},

        {14, -3, -8, -18, -21, 40, 42, 39, 39},
        {-50, -38, -23, -10, 6, -39, -23, -6, 6},

        {12, -4, -7, -16, -20, 38, 43, 40, 39},
        {-51, -38, -24, -9, 6, -39, -23, -7, 6},

        {9, -6, -6, -14, -18, 35, 42, 42, 40},
        {-53, -41, -26, -10, 6, -42, -26, -9, 6},

        {10, -7, -6, -15, -16, 34, 42, 43, 42},
        {-54, -42, -26, -11, 6, -43, -27, -10, 6},

        {9, -10, -6, -14, -15, 32, 41, 43, 43},
        {-56, -44, -27, -11, 6, -44, -29, -11, 6},

        {8, -12, -7, -14, -16, 29, 40, 44, 43},
        {-57, -44, -28, -10, 6, -45, -29, -10, 6},

        {9, -16, -9, -15, -18, 28, 38, 44, 44},
        {-60, -48, -28, -11, 6, -47, -30, -11, 6},

        {10, -16, -11, -15, -19, 28, 37, 44, 45},
        {-60, -47, -28, -11, 6, -48, -31, -12, 6},

        {9, -14, -13, -19, -11, 28, 34, 43, 43},
        {-61, -50, -29, -11, 6, -50, -32, -13, 6},

        {10, -15, -15, -20, -22, 29, 35, 42, 42},
        {-63, -50, -30, -11, 6, -51, -33, -14, 6},

        {9, -15, -23, -24, -24, 32, 39, 42, 42},
        {-64, -51, -33, -13, 6, -52, -34, -14, 6},

        // Walk
        {0, -26, -33, -37, -36, 26, 37, 35, 35},
        {0, 10, 29, 49, 68, 0, 0, 20, 40},

        {1, -25, -32, -37, -36, 26, 38, 38, 38},
        {0, 10, 29, 49, 68, 1, 2, 22, 42},

        {2, -24, -30, -37, -36, 27, 39, 40, 41},
        {1, 10, 29, 49, 68, 1, 4, 24, 45},

        {3, -22, -29, -37, -36, 29, 40, 45, 43},
        {2, 10, 29, 49, 68, 2, 6, 26, 48},

        {4, -21, -29, -37, -36, 30, 42, 47, 45},
        {2, 10, 29, 49, 68, 3, 10, 29, 51},

        {6, -21, -29, -37, -36, 31, 43, 49, 47},
        {2, 10, 29, 49, 68, 4, 14, 32, 54},

        {5, -19, -28, -37, -36, 34, 46, 50, 47},
        {2, 10, 30, 49, 68, 4, 18, 36, 57},

        {7, -18, -28, -37, -36, 36, 48, 52, 47},
        {2, 10, 29, 49, 68, 7, 22, 42, 65},

        {8, -18, -29, -37, -36, 36, 49, 52, 47},
        {2, 10, 28, 47, 66, 8, 25, 44, 68},

        {9, -17, -30, -36, -36, 38, 50, 50, 47},
        {1, 10, 26, 44, 63, 10, 27, 47, 68},

        {10, -16, -30, -36, -36, 38, 50, 50, 47},
        {0, 10, 24, 41, 60, 10, 28, 48, 68},

        {11, -17, -30, -36, -35, 38, 50, 48, 48},
        {-1, 9, 20, 39, 58, 11, 28, 48, 68},

        {12, -16, -31, -35, -34, 38, 50, 48, 48},
        {-1, 8, 17, 36, 55, 11, 28, 48, 68},

        {12, -16, -31, -35, -34, 39, 50, 48, 48},
        {-3, 7, 14, 34, 53, 10, 28, 48, 68},

        {14, -15, -30, -33, -32, 40, 49, 48, 48},
        {-3, 5, 12, 32, 50, 9, 27, 48, 68},

        {15, -14, -30, -33, -32, 40, 48, 48, 48},
        {-5, 3, 9, 29, 47, 8, 27, 47, 68},

        {16, -12, -29, -32, -32, 41, 47, 48, 48},
        {-5, 1, 6, 25, 45, 8, 26, 47, 68},

        {17, -12, -27, -30, -31, 41, 48, 48, 48},
        {-7, -1, 4, 22, 41, 7, 26, 47, 68},

        {18, -9, -26, -29, -30, 42, 47, 48, 48},
        {-7, -2, 1, 19, 38, 7, 26, 47, 68},

        {19, -9, -25, -28, -28, 42, 47, 48, 48},
        {-7, -2, -2, 15, 36, 6, 26, 48, 68},

        {22, -7, -23, -28, -27, 43, 47, 48, 48},
        {-8, -2, -7, 13, 34, 6, 26, 48, 68},

        {23, -7, -23, -27, -25, 43, 47, 48, 48},
        {-8, -2, -7, 12, 32, 6, 26, 48, 68},

        {26, -5, -20, -25, -24, 43, 46, 48, 48},
        {-10, -2, -7, 10, 30, 5, 26, 48, 68},

        {28, -4, -18, -22, -19, 44, 46, 48, 48},
        {-10, -2, -7, 10, 29, 5, 26, 48, 68},

        {29, -3, -17, -17, -16, 45, 46, 48, 48},
        {-11, -2, -7, 10, 29, 5, 26, 48, 68},

        {30, -2, -15, -14, -12, 46, 46, 48, 48},
        {-11, -2, -7, 10, 30, 5, 27, 48, 68},

        {31, -1, -15, -12, -8, 46, 47, 46, 48},
        {-10, -1, -4, 13, 32, 7, 28, 48, 68},

        {32, 0, -13, -9, -4, 47, 48, 46, 48},
        {-10, -1, -3, 16, 35, 8, 28, 48, 68},

        {32, 2, -13, -6, -1, 48, 47, 46, 48},
        {-8, 1, 1, 21, 41, 9, 28, 48, 68},

        {32, 2, -10, -2, 3, 50, 48, 46, 48},
        {-7, 4, 6, 26, 46, 10, 28, 48, 68},

        {32, 4, -7, 3, 6, 52, 50, 46, 48},
        {-5, 4, 13, 32, 52, 10, 28, 48, 68},

        {32, 5, -1, 8, 9, 53, 49, 46, 48},
        {-4, 7, 19, 39, 60, 10, 28, 48, 68},

        {32, 6, 3, 11, 12, 54, 53, 46, 47},
        {-3, 8, 26, 47, 68, 10, 27, 45, 65},

        {32, 8, 6, 12, 12, 54, 57, 48, 48},
        {-2, 9, 28, 50, 68, 10, 25, 41, 62},

        {32, 8, 8, 12, 12, 56, 60, 50, 49},
        {-2, 8, 28, 49, 68, 10, 23, 39, 60},

        {31, 8, 9, 12, 12, 56, 64, 53, 52},
        {-3, 8, 28, 49, 68, 8, 20, 36, 59},

        {32, 8, 10, 13, 12, 57, 67, 55, 53},
        {-4, 7, 28, 48, 68, 6, 19, 35, 56},

        {32, 10, 11, 12, 12, 58, 70, 58, 55},
        {-5, 6, 28, 48, 68, 5, 17, 33, 54},

        {32, 11, 12, 12, 12, 60, 73, 60, 56},
        {-6, 5, 28, 48, 68, 3, 16, 31, 52},

        {32, 13, 12, 12, 12, 61, 75, 63, 58},
        {-7, 5, 28, 48, 68, 3, 14, 29, 50},

        {34, 15, 12, 11, 12, 64, 76, 64, 60},
        {-7, 5, 28, 48, 68, 2, 12, 28, 48},

        {37, 15, 12, 11, 12, 65, 78, 66, 62},
        {-7, 5, 28, 48, 68, 2, 10, 26, 46},

        {39, 17, 12, 11, 12, 68, 81, 70, 64},
        {-6, 6, 28, 48, 68, 1, 7, 24, 44},

        {41, 17, 12, 11, 12, 69, 83, 73, 66},
        {-6, 7, 28, 48, 68, 1, 6, 22, 42},

        {42, 19, 12, 11, 12, 70, 83, 75, 68},
        {-5, 9, 29, 48, 68, 1, 3, 21, 40},

        {45, 19, 13, 11, 12, 72, 85, 78, 71},
        {-4, 10, 29, 48, 68, 1, -1, 19, 38},

        {47, 20, 13, 12, 12, 73, 86, 80, 75},
        {-3, 10, 29, 48, 68, 0, -2, 19, 38},

        {47, 22, 15, 12, 12, 73, 84, 81, 79},
        {-2, 10, 29, 48, 68, 0, -1, 18, 38},

        // Prepare Jump
        {0, -25, -34, -34, -34, 22, 30, 32, 32},
        {0, 11, 29, 49, 68, 11, 28, 47, 68},

        {0, -25, -34, -34, -34, 22, 30, 31, 32},
        {2, 12, 30, 48, 68, 12, 29, 47, 68},

        {0, -26, -35, -34, -34, 23, 32, 32, 32},
        {5, 13, 30, 48, 68, 13, 29, 47, 68},

        {0, -26, -36, -35, -34, 24, 33, 33, 32},
        {7, 13, 30, 48, 68, 13, 29, 48, 68},

        {0, -27, -37, -36, -34, 25, 34, 34, 32},
        {9, 15, 30, 48, 68, 14, 29, 47, 68},

        {0, -27, -38, -37, -34, 26, 35, 34, 32},
        {11, 17, 31, 48, 68, 16, 30, 47, 68},

        {0, -27, -39, -37, -34, 26, 36, 34, 32},
        {12, 20, 32, 49, 68, 18, 31, 48, 68},

        {0, -28, -40, -37, -34, 27, 36, 34, 32},
        {14, 21, 32, 49, 68, 20, 31, 48, 68},

        {0, -28, -41, -37, -34, 28, 38, 34, 32},
        {15, 23, 33, 49, 68, 22, 32, 48, 68},

        {0, -29, -42, -38, -34, 28, 39, 35, 32},
        {18, 24, 33, 50, 68, 23, 33, 48, 68},

        {0, -29, -43, -39, -34, 29, 40, 34, 32},
        {19, 27, 34, 50, 68, 25, 33, 49, 68},

        {0, -29, -44, -38, -34, 29, 41, 36, 32},
        {20, 28, 35, 51, 68, 26, 34, 51, 68},

        {0, -30, -45, -38, -34, 28, 42, 36, 32},
        {20, 28, 35, 51, 68, 27, 34, 50, 68},

        {0, -30, -45, -38, -34, 29, 42, 36, 32},
        {22, 30, 36, 52, 68, 29, 35, 51, 68},

        {0, -31, -45, -38, -34, 29, 42, 36, 32},
        {24, 30, 36, 52, 68, 30, 35, 51, 68},

        {0, -31, -46, -40, -34, 30, 43, 37, 32},
        {25, 32, 37, 52, 68, 32, 36, 51, 68},

        {0, -32, -47, -39, -34, 31, 44, 37, 32},
        {26, 32, 38, 52, 68, 33, 37, 52, 68},

        {0, -32, -47, -40, -34, 31, 44, 37, 32},
        {28, 34, 39, 54, 68, 34, 38, 53, 68},

        {0, -32, -47, -40, -34, 30, 44, 37, 32},
        {29, 35, 39, 54, 68, 35, 38, 53, 68},

        // Jump
        {0, -30, -45, -39, -34, 26, 40, 36, 32},
        {0, 6, 11, 25, 40, 5, 11, 24, 40},

        {0, -29, -44, -39, -34, 26, 39, 35, 32},
        {-4, 4, 10, 26, 40, 4, 10, 25, 40},

        {0, -27, -42, -38, -34, 25, 38, 35, 32},
        {-7, 2, 10, 25, 40, 2, 10, 24, 40},

        {0, -25, -40, -37, -34, 17, 36, 35, 32},
        {-10, 0, 9, 24, 40, 0, 11, 24, 40},

        {0, -24, -38, -37, -34, 22, 34, 34, 32},
        {-13, -3, 8, 23, 40, -3, 9, 24, 40},

        {0, -24, -37, -35, -34, 22, 32, 34, 32},
        {-16, -6, 7, 23, 40, -5, 8, 24, 40},

        {0, -23, -34, -35, -34, 22, 30, 33, 32},
        {-19, -10, 5, 22, 39, -9, 7, 23, 39},

        {0, -23, -32, -34, -34, 21, 28, 32, 32},
        {-26, -17, 0, 18, 37, -16, 1, 19, 37},

        {0, -23, -31, -34, -34, 22, 28, 32, 32},
        {-32, -24, -5, 15, 34, -24, -5, 15, 34},

        {0, -24, -31, -34, -35, 21, 28, 32, 32},
        {-40, -29, -9, 10, 31, -29, -10, 10, 31},

        {0, -21, -31, -34, -35, 21, 28, 32, 32},
        {-47, -35, -14, 7, 28, -35, -14, 7, 28},

        {0, -24, -30, -33, -35, 21, 27, 32, 32},
        {-52, -39, -18, 4, 26, -39, -19, 4, 26},

        {0, -24, -29, -33, -35, 21, 27, 31, 32},
        {-57, -44, -23, 0, 23, -44, -23, -1, 23},

        {0, -24, -30, -32, -35, 21, 26, 30, 32},
        {-61, -47, -25, -4, 17, -47, -26, -6, 17},

        {0, -24, -28, -31, -33, 21, 25, 29, 32},
        {-64, -48, -30, -10, 11, -48, -30, -11, 11},

        {0, -24, -26, -31, -32, 21, 25, 28, 32},
        {-65, -54, -35, -17, 4, -51, -34, -16, 5},

        {0, -25, -26, -29, -32, 21, 24, 27, 31},
        {-66, -54, -39, -22, -3, -54, -38, -22, -3},

        {0, -24, -26, -28, -31, 20, 23, 26, 30},
        {-67, -57, -44, -27, -9, -56, -43, -27, -10},

        {0, -23, -25, -28, -29, 19, 23, 26, 29},
        {-67, -60, -47, -31, -15, -60, -47, -31, -15},

        {0, -23, -23, -27, -28, 20, 23, 25, 27},
        {-67, -62, -52, -37, -23, -62, -52, -38, -23},

        {0, -23, -24, -26, -28, 19, 22, 25, 27},
        {-66, -63, -55, -43, -31, -63, -55, -43, -31},

        {0, -22, -23, -25, -26, 20, 20, 24, 25},
        {-67, -65, -58, -48, -39, -65, -59, -49, -40},

        {0, -22, -20, -23, -24, 19, 20, 20, 23},
        {-66, -64, -63, -55, -48, -64, -56, -56, -49},

        {0, -23, -23, -23, -23, 20, 20, 20, 20},
        {-65, -64, -63, -62, -56, -60, -60, -60, -59},
        
        // Await
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {-65, -500, -500, -500, -500, -500, -500, -500, -500}
    };
    // @formatter:on

    private final Transformable[] limbs = new Transformable[LIMBS.length];
    private final Animatable[] limbsAnim = new Animatable[LIMBS.length];
    private final Animator animator = new AnimatorModel();
    private final Tick tick = new Tick();

    private final SourceResolutionProvider source = services.get(SourceResolutionProvider.class);
    private final Spawner spawner = services.get(Spawner.class);
    private final MusicPlayer music = services.get(MusicPlayer.class);
    private final Sequencer sequencer = services.get(Sequencer.class);
    private final ScreenShaker shaker = services.get(ScreenShaker.class);
    private final StateHandler target = services.get(Trackable.class).getFeature(StateHandler.class);

    private final Animation idle;
    private final Animation rise;
    private final Animation walk;
    private final Animation preparejump;
    private final Animation jump;

    private Stats stats;
    private double startX;
    private double startY;
    private int patrol;
    private int walkedCount;
    private Updatable phase;
    private double angle;

    @FeatureGet private Identifiable identifiable;
    @FeatureGet private Transformable transformable;
    @FeatureGet private Body body;
    @FeatureGet private Launcher launcher;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public BossNorka(Services services, Setup setup)
    {
        super(services, setup);

        final AnimationConfig config = AnimationConfig.imports(setup);
        idle = config.getAnimation(Anim.IDLE);
        rise = config.getAnimation("rise");
        walk = config.getAnimation(Anim.WALK);
        preparejump = config.getAnimation("preparejump");
        jump = config.getAnimation(Anim.JUMP);
    }

    /**
     * Create limb.
     * 
     * @param limb The limb name.
     * @return The created limb.
     */
    private Featurable create(String limb)
    {
        return spawner.spawn(Medias.create(Folder.BOSS, "norka", limb + Factory.FILE_DATA_DOT_EXTENSION),
                             transformable);
    }

    /**
     * Start phase and prepare for rise.
     * 
     * @param extrp The extrapolation value.
     */
    private void start(double extrp)
    {
        startX = transformable.getX();
        startY = transformable.getY();
        animator.play(rise);
        phase = this::updateRise;
    }

    /**
     * Update rise phase. Play until animation ends and start idle.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateRise(double extrp)
    {
        animator.update(extrp);
        transformable.teleportY(startY);
        body.resetGravity();

        if (animator.getAnimState() == AnimState.FINISHED)
        {
            phase = this::updateWalk;
            transformable.teleportY(startY + Y);
            animator.play(walk);
            walkedCount = 0;

            if (startX + PATROLS[patrol] < transformable.getX())
            {
                animator.setFrame(walk.getLast());
                animator.setAnimSpeed(-walk.getSpeed());
                transformable.moveLocationX(1.0, -WALK_OFFSET);
            }
            tick.restart();
        }
    }

    /**
     * Update walk phase on right until border. Play walk and move right until range reached.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateWalk(double extrp)
    {
        final int old = animator.getFrame();
        animator.update(extrp);
        transformable.teleportY(startY + Y);
        body.resetGravity();

        final int side = getSide();
        if (side <= 0 && old == walk.getFirst() && animator.getFrame() == walk.getLast()
            || side > 0 && old == walk.getLast() && animator.getFrame() == walk.getFirst())
        {
            transformable.moveLocationX(1.0, WALK_OFFSET * side);
            walkedCount++;

            if (walkedCount > 1)
            {
                phase = this::updatePrepareJump;
                animator.play(preparejump);
                tick.restart();
            }
        }
    }

    /**
     * Get direction side.
     * 
     * @return The direction side.
     */
    private int getSide()
    {
        final int side;
        if (startX + PATROLS[patrol] < transformable.getX())
        {
            side = -1;
        }
        else if (startX + PATROLS[patrol] > transformable.getX())
        {
            side = 1;
        }
        else
        {
            side = 0;
        }
        return side;
    }

    /**
     * Update prepare jump phase.
     * 
     * @param extrp The extrapolation value.
     */
    private void updatePrepareJump(double extrp)
    {
        animator.update(extrp);
        transformable.teleportY(startY + Y);
        body.resetGravity();

        if (animator.getAnimState() == AnimState.FINISHED)
        {
            phase = this::updateJump;
            transformable.teleportY(startY + Y - 24);
            animator.play(jump);
            tick.stop();
        }
    }

    /**
     * Update jump phase.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateJump(double extrp)
    {
        animator.update(extrp);
        transformable.teleportY(startY + Y - 24);
        body.resetGravity();

        if (animator.getAnimState() == AnimState.FINISHED)
        {
            tick.start();
            animator.play(idle);

            if (PATROLS[patrol] == 0)
            {
                launcher.setLevel(0);
                phase = this::updateAttack;
                animator.play(idle);
                tick.restart();
            }
            else
            {
                launcher.setLevel(1);
                launcher.fire();
                launcher.setLevel(2);
                launcher.fire();
                phase = this::updatePlatform;
                tick.restart();
            }
        }

    }

    /**
     * Update attack phase.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateAttack(double extrp)
    {
        launcher.fire(new Force(getRandomDirectionX(), -UtilRandom.getRandomDouble() / 5));
        updateCurve(extrp);
        transformable.teleportY(startY + Y - 24 + UtilMath.cos(angle) * CURVE_AMPLITUDE);
        body.resetGravity();

        tick.update(extrp);
        if (tick.elapsedTime(source.getRate(), FIRE_DELAY_MS))
        {
            phase = this::updateFall;
        }
    }

    /**
     * Update platform phase.
     * 
     * @param extrp The extrapolation value.
     */
    private void updatePlatform(double extrp)
    {
        updateCurve(extrp);
        transformable.teleportY(startY + Y - 24 + UtilMath.cos(angle) * CURVE_AMPLITUDE);
        body.resetGravity();

        tick.update(extrp);
        if (tick.elapsedTime(source.getRate(), JUMP_DELAY_MS))
        {
            phase = this::updateFall;
        }
    }

    /**
     * Update fall phase moving left.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateFall(double extrp)
    {
        animator.update(extrp);
        if (Double.compare(transformable.getY(), startY - 68) <= 0)
        {
            phase = this::updateShakeScreen;
            body.resetGravity();
            transformable.teleportY(startY - 68);
            shaker.start();
        }
    }

    /**
     * Update shake screen phase.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateShakeScreen(double extrp)
    {
        transformable.teleportY(startY - 68);
        if (shaker.hasShaken())
        {
            tick.restart();
            phase = this::updateAwait;
        }
    }

    /**
     * Update await phase before rise again.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateAwait(double extrp)
    {
        animator.update(extrp);
        transformable.teleportY(startY - 68);
        body.resetGravity();

        tick.update(extrp);
        if (tick.elapsedTime(source.getRate(), AWAIT_DELAY_MS))
        {
            phase = this::updateRise;
            transformable.teleportY(startY);
            patrol = UtilMath.wrap(patrol + 1, 0, PATROLS.length);
            animator.play(rise);
        }
    }

    /**
     * Update end win.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateEnd(double extrp)
    {
        body.resetGravity();

        tick.update(extrp);
        if (tick.elapsedTime(source.getRate(), END_DELAY_MS))
        {
            identifiable.destroy();
            music.stopMusic();
            sequencer.end(Extro.class, services.get(GameConfig.class), target.getFeature(Stats.class).hasAmulet());
        }
    }

    /**
     * Update limbs position.
     */
    private void updateLimbs()
    {
        final int id = animator.getFrame() - 1;
        for (int i = 0; i < LIMBS.length; i++)
        {
            limbs[i].setLocation(transformable.getX() + data[id * 2][i], transformable.getY() - data[id * 2 + 1][i]);
            if (i > 0 && i < 4 || i > 4 && i < 8)
            {
                limbs[i].getFeature(Rasterable.class)
                        .setAnimOffset(UtilMath.clamp(stats.getHealthMax() - stats.getHealth(), 0, 3));
            }
            else
            {
                limbs[i].getFeature(Rasterable.class)
                        .setAnimOffset(UtilMath.clamp(stats.getHealthMax() - stats.getHealth(), 0, 3) * 5);
            }
            limbs[i].check(false);
        }
    }

    /**
     * Update curve effect.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateCurve(double extrp)
    {
        angle = UtilMath.wrapAngleDouble(angle + CURVE_SPEED * extrp);
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        for (int i = 0; i < LIMBS.length; i++)
        {
            final Featurable featurable = create(LIMBS[i]);
            limbs[i] = featurable.getFeature(Transformable.class);
            limbsAnim[i] = featurable.getFeature(Animatable.class);
        }
        stats = limbs[0].getFeature(Stats.class);

        launcher.addListener(l ->
        {
            if (stats != null && !l.hasFeature(Fly.class))
            {
                if (l.hasFeature(NorkaPlatform.class))
                {
                    l.getFeature(Rasterable.class)
                     .setAnimOffset(UtilMath.clamp(stats.getHealthMax() - stats.getHealth(), 0, 3) * 3);
                }
                else
                {
                    l.getFeature(Rasterable.class)
                     .setAnimOffset(UtilMath.clamp(stats.getHealthMax() - stats.getHealth(), 0, 3));
                }
            }
        });
        identifiable.addListener(id ->
        {
            for (int i = 0; i < limbs.length; i++)
            {
                limbs[i].getFeature(Identifiable.class).destroy();
            }
        });
    }

    @Override
    public void update(double extrp)
    {
        phase.update(extrp);
        if (stats != null)
        {
            updateLimbs();
            if (stats.getHealth() == 0)
            {
                for (int i = 1; i < limbs.length; i++)
                {
                    limbs[i].getFeature(Hurtable.class).kill(true);
                }
                music.playMusic(Music.NORKA_WIN);
                target.getFeature(Stats.class).win();
                phase = this::updateEnd;
                stats = null;
                tick.restart();
            }
        }
    }

    @Override
    public void recycle()
    {
        phase = this::start;
        patrol = 0;
    }
}
