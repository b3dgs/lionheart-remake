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
import com.b3dgs.lionengine.Animator;
import com.b3dgs.lionengine.AnimatorModel;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.game.AnimationConfig;
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
import com.b3dgs.lionheart.LoadNextStage;
import com.b3dgs.lionheart.Music;
import com.b3dgs.lionheart.MusicPlayer;
import com.b3dgs.lionheart.Sfx;
import com.b3dgs.lionheart.constant.Anim;
import com.b3dgs.lionheart.landscape.Landscape;
import com.b3dgs.lionheart.object.EntityModel;

/**
 * Boss Lava feature implementation.
 * <ol>
 * <li>Raise from ground.</li>
 * <li>Stay idle.</li>
 * <li>Move right.</li>
 * <li>Jump left.</li>
 * </ol>
 */
@FeatureInterface
public final class BossLava extends FeatureModel implements Routine, Recyclable
{
    private static final int END_TICK = 500;
    private static final int Y = 60;
    private static final int WALK_OFFSET = 60;
    private static final int RANGE_X = 368;
    private static final int TICK_RISE = 200;
    private static final int TICK_IDLE = 100;
    private static final int TICK_JUMP = 14;

    // @formatter:off
    private static final int[][] DATA =
    {
        // Rise
        {0, -8, -8, -8, -8, -8, -8, 9, 9, 9, 9, 9, 9},
        {0, -5, -5, 1, 1, 1, 1, -5, -5, 1, 1, 1, 1},

        {0, -8, -17, -8, -8, -8, -8, 9, 19, 9, 9, 9, 9},
        {0, -5, -5, 1, 1, 1, 1, -5, -5, 1, 1, 1, 1},

        {0, -8, -17, -8, -8, -8, -8, 9, 19, 9, 9, 9, 9},
        {0, -5, -5, 1, 1, 1, 1, -5, -5, 1, 1, 1, 1},

        {0, -8, -21, -8, -8, -8, -8, 9, 22, 9, 9, 9, 9},
        {0, -5, -5, 1, 1, 1, 1, -5, -5, 1, 1, 1, 1},

        {0, -12, -23, -8, -8, -8, -8, 17, 25, 9, 9, 9, 9},
        {0, -9, -5, 1, 1, 1, 1, -9, -5, 1, 1, 1, 1},

        {0, -15, -26, -8, -8, -8, -8, 19, 26, 9, 9, 9, 9},
        {0, -11, -7, 1, 1, 1, 1, -11, -5, 1, 1, 1, 1},

        {0, -16, -28, -8, -8, -8, -8, 20, 26, 9, 9, 9, 9},
        {0, -12, -6, 1, 1, 1, 1, -11, -6, 1, 1, 1, 1},

        {0, -18, -30, -8, -8, -8, -8, 21, 31, 9, 9, 9, 9},
        {0, -13, -6, 1, 1, 1, 1, -12, -6, 1, 1, 1, 1},

        {0, -19, -32, -8, -8, -8, -8, 20, 33, 9, 9, 9, 9},
        {0, -14, -4, 1, 1, 1, 1, -13, -4, 1, 1, 1, 1},

        {0, -20, -34, -8, -8, -8, -8, 22, 34, 9, 9, 9, 9},
        {0, -14, -3, 1, 1, 1, 1, -14, -3, 1, 1, 1, 1},

        {0, -22, -35, -8, -8, -8, -8, 23, 37, 9, 9, 9, 9},
        {0, -13, -1, -5, -3, -1, 1, -14, -2, -5, -3, -1, 1},

        {0, -23, -35, -8, -8, -8, -8, 25, 38, 9, 9, 9, 9},
        {0, -14, 0, -6, -4, -2, 1, -15, -1, -6, -4, -2, 1},

        {0, -25, -36, -8, -8, -8, -8, 27, 39, 9, 9, 9, 9},
        {0, -13, 3, -6, -4, -2, 1, -15, 2, -6, -4, -2, 1},

        {0, -26, -37, -8, -8, -8, -8, 30, 40, 9, 9, 9, 9},
        {0, -12, 6, -6, -4, -2, 1, -14, 6, -6, -4, -2, 1},

        {0, -28, -37, -9, -8, -8, -37, 31, 41, 10, 9, 9, 41},
        {0, -10, 12, -6, -4, -2, 12, -12, 12, -6, -4, -2, 12},

        {0, -29, -37, -18, -12, -10, -38, 30, 41, 21, 14, 11, 41},
        {0, -9, 12, -10, -4, -2, 1, -12, 12, -10, -4, -2, 12},

        {0, -29, -37, -18, -12, -10, -38, 30, 41, 21, 14, 11, 41},
        {-1, -9, 12, -10, -4, -2, 1, -12, 12, -10, -4, -2, 12},

        {0, -29, -38, -24, -15, -13, -38, 30, 40, 21, 17, 14, 41},
        {-3, -10, 12, -12, -9, -5, -1, -12, 12, -10, -5, -3, 12},

        {0, -29, -38, -26, -15, -13, -38, 30, 40, 23, 19, 16, 41},
        {-4, -10, 12, -12, -9, -5, -1, -12, 12, -10, -5, -3, 12},

        {0, -32, -38, -26, -15, -13, -38, 30, 40, 23, 19, 16, 41},
        {-7, -12, 7, -12, -9, -5, -1, -12, 7, -10, -5, -3, -1},

        {0, -32, -38, -26, -15, -13, -10, 30, 40, 23, 19, 16, 41},
        {-12, -12, 3, -12, -9, -5, -1, -12, 3, -10, -5, -3, -1},

        {0, -37, -42, -15, -24, -13, -10, 33, 40, 11, 23, 33, 41},
        {-20, -13, -1, -14, -23, -5, -1, -13, -1, -13, -21, -13, -1},

        {0, -37, -45, -17, -27, -39, -40, 40, 46, 13, 29, 37, 42},
        {-24, -23, -8, -20, -27, -16, 0, -20, -8, -18, -25, -15, 1},

        {0, -33, -45, -20, -29, -39, -40, 33, 47, 17, 29, 37, 42},
        {-29, -35, -8, -23, -26, -16, 0, -35, -16, -22, -26, -15, 1},

        {0, -32, -49, -21, -30, -38, -40, 33, 49, 21, 31, 38, 42},
        {-35, -43, -25, -29, -26, -16, 0, -44, -26, -28, -27, -16, 1},

        {0, -34, -50, -24, -31, -39, -40, 33, 51, 24, 33, 40, 42},
        {-42, -48, -32, -36, -27, -15, 0, -51, -35, -36, -28, -15, 1},

        {0, -34, -50, -24, -33, -40, -40, 34, 52, 24, 34, 41, 42},
        {-46, -56, -40, -38, -29, -15, 0, -57, -41, -39, -29, -16, 1},

        // Idle
        {0, -31, -49, -18, -28, -38, -40, 30, 47, 23, 31, 38, 41},
        {0, -24, -11, 6, 20, 34, 53, -49, -40, 7, 25, 39, 56},

        {1, -31, -50, -19, -30, -38, -41, 29, 45, 22, 33, 37, 41},
        {-3, -27, -15, 2, 18, 31, 51, -46, -35, 4, 21, 39, 56},

        {1, -32, -50, -18, -30, -38, -40, 29, 43, 23, 34, 38, 40},
        {-6, -31, -20, 2, 16, 31, 50, -45, -31, 4, 20, 37, 55},

        {2, -30, -48, -16, -30, -38, -40, 29, 44, 23, 35, 39, 39},
        {-9, -34, -27, -1, 13, 28, 47, -42, -27, 2, 18, 35, 51},

        {4, -29, -50, -17, -30, -38, -40, 30, 43, 24, 36, 40, 39},
        {-11, -37, -30, -3, 10, 26, 44, -41, -22, 1, 17, 32, 48},

        {4, -29, -47, -14, -31, -38, -40, 30, 44, 23, 36, 41, 40},
        {-13, -43, -36, -7, 6, 23, 41, -38, -17, -1, 15, 30, 46},

        {6, -28, -46, -14, -30, -37, -40, 31, 44, 24, 36, 41, 39},
        {-16, -47, -38, -9, 3, 21, 37, -36, -12, -4, 11, 29, 44},

        {4, -27, -44, -15, -31, -38, -40, 33, 47, 24, 39, 41, 41},
        {-18, -51, -41, -10, -2, 17, 36, -33, -9, -6, 8, 26, 42},

        {3, -26, -40, -17, -32, -38, -39, 34, 50, 23, 38, 41, 40},
        {-20, -55, -41, -14, -4, 14, 32, -32, -9, -9, 4, 23, 40},

        {2, -26, -38, -19, -34, -39, -39, 37, 53, 24, 38, 41, 40},
        {-21, -56, -39, -14, -5, 15, 36, -33, -12, -12, 3, 22, 36},

        {1, -28, -35, -20, -34, -39, -40, 38, 56, 22, 38, 41, 40},
        {-21, -55, -33, -16, -3, 16, 43, -34, -14, -13, 1, 19, 33},

        {-0, -28, -33, -22, -35, -39, -39, 38, 56, 20, 36, 40, 42},
        {-18, -52, -30, -13, 1, 21, 47, -36, -15, -13, -3, 14, 29},

        {-1, -29, -32, -22, -35, -39, -38, 40, 59, 21, 34, 39, 42},
        {-16, -49, -25, -10, 5, 26, 54, -39, -20, -12, -1, 17, 34},

        {-3, -30, -32, -26, -37, -38, -38, 39, 57, 20, 34, 40, 42},
        {-12, -46, -19, -5, 8, 32, 57, -41, -24, -11, 2, 20, 38},

        {-4, -30, -36, -27, -37, -39, -38, 37, 58, 20, 33, 40, 41},
        {-10, -41, -15, -2, 13, 34, 57, -44, -26, -7, 5, 23, 42},

        {-5, -32, -41, -27, -36, -38, -38, 35, 60, 20, 32, 39, 42},
        {-7, -36, -8, 1, 16, 37, 56, -47, -30, -5, 7, 25, 48},

        {-6, -33, -44, -26, -32, -37, -38, 34, 57, 21, 33, 39, 42},
        {-2, -30, -4, 6, 22, 39, 57, -48, -35, -2, 11, 29, 53},

        {-5, -34, -49, -22, -30, -36, -38, 33, 55, 24, 33, 39, 40},
        {2, -25, -3, 11, 26, 42, 56, -49, -39, 2, 15, 35, 56},

        {-2, -35, -51, -21, -30, -37, -38, 31, 53, 26, 34, 39, 40},
        {5, -23, -5, 11, 24, 42, 57, -50, -42, 6, 20, 39, 57},

        {0, -31, -46, -18, -28, -37, -39, 30, 51, 22, 31, 37, 41},
        {3, -22, -7, 7, 21, 37, 55, -50, -42, 6, 23, 40, 56},

        // Walk
        {0, -34, -41, -20, -24, -20, -20, 30, 45, 21, 28, 22, 22},
        {0, -12, 21, 12, 31, 48, 64, -33, -27, 10, 25, 42, 60},

        {0, -33, -39, -15, -21, -20, -19, 33, 48, 25, 31, 25, 26},
        {1, -13, 19, 13, 31, 48, 64, -33, -24, 7, 22, 40, 58},

        {1, -30, -38, -14, -20, -19, -20, 35, 52, 28, 36, 29, 29},
        {1, -14, 16, 13, 31, 48, 64, -33, -21, 5, 20, 38, 56},

        {3, -28, -37, -12, -20, -19, -20, 39, 55, 30, 39, 31, 33},
        {0, -15, 15, 12, 31, 48, 64, -33, -17, 3, 19, 36, 54},

        {4, -26, -35, -11, -20, -20, -20, 40, 57, 31, 42, 35, 37},
        {-1, -16, 13, 14, 30, 48, 64, -33, -14, 2, 17, 34, 52},

        {6, -24, -34, -10, -20, -20, -20, 44, 62, 36, 46, 39, 41},
        {-1, -18, 9, 13, 32, 48, 64, -33, -12, 2, 15, 32, 49},

        {10, -22, -33, -10, -19, -20, -20, 47, 64, 38, 49, 46, 46},
        {-1, -20, 8, 13, 32, 48, 64, -32, -10, 2, 13, 30, 49},

        {14, -19, -33, -8, -17, -20, -20, 50, 67, 41, 51, 50, 51},
        {-1, -22, 6, 13, 31, 48, 64, -31, -7, 2, 12, 30, 49},

        {16, -18, -34, -9, -18, -19, -20, 54, 70, 44, 53, 55, 56},
        {0, -23, 3, 13, 31, 48, 64, -30, -5, 3, 14, 31, 50},

        {18, -16, -31, -8, -18, -20, -20, 57, 72, 46, 57, 60, 61},
        {0, -24, -1, 12, 30, 48, 64, -28, -2, 4, 15, 33, 52},

        {20, -13, -29, -6, -18, -20, -20, 61, 74, 49, 61, 65, 66},
        {0, -26, -3, 11, 30, 48, 64, -26, 3, 6, 18, 35, 54},

        {24, -10, -26, -4, -17, -20, -20, 64, 78, 52, 65, 70, 71},
        {0, -28, -10, 10, 29, 48, 64, -23, 7, 7, 21, 37, 56},

        {28, -5, -22, -1, -17, -20, -20, 67, 81, 56, 69, 73, 74},
        {1, -30, -17, 9, 28, 48, 64, -19, 10, 8, 24, 41, 58},

        {32, 0, -17, 0, -15, -19, -20, 69, 83, 58, 72, 76, 77},
        {2, -32, -22, 8, 27, 45, 64, -17, 13, 8, 25, 44, 61},

        {35, 5, -14, 2, -13, -18, -19, 73, 85, 61, 75, 79, 80},
        {3, -34, -26, 7, 24, 41, 32, -14, 15, 9, 27, 47, 64},

        {38, 8, -8, 4, -9, -16, -17, 73, 86, 63, 75, 78, 80},
        {4, -37, -31, 6, 22, 39, 60, -13, 18, 10, 29, 47, 64},

        {38, 12, -2, 8, -5, -14, -14, 76, 86, 66, 76, 78, 80},
        {5, -37, -33, 5, 20, 37, 57, -10, 17, 11, 29, 47, 64},

        {40, 11, -3, 9, -4, -12, -11, 75, 86, 67, 76, 78, 80},
        {8, -34, -27, 3, 17, 34, 54, -7, 17, 12, 30, 47, 64},

        {40, 11, -4, 11, -1, -9, -8, 74, 86, 67, 77, 79, 80},
        {8, -31, -24, 0, 14, 32, 51, -9, 16, 12, 30, 46, 64},

        {41, 12, -5, 14, 3, -6, -4, 76, 87, 68, 78, 79, 80},
        {8, -28, -22, -4, 12, 29, 48, -11, 14, 12, 30, 47, 64},

        {44, 13, -4, 15, 4, -2, 0, 77, 88, 70, 79, 79, 80},
        {7, -29, -19, -5, 10, 25, 44, -12, 11, 12, 29, 47, 64},

        {46, 15, -4, 18, 5, 3, 5, 80, 89, 72, 81, 79, 80},
        {5, -28, -18, -6, 7, 24, 43, -14, 10, 12, 31, 47, 64},

        {48, 17, -2, 21, 7, 8, 10, 81, 90, 73, 80, 79, 80},
        {4, -27, -12, -8, 4, 22, 43, -17, 8, 12, 31, 47, 64},

        {51, 18, 2, 22, 10, 15, 16, 83, 92, 73, 81, 80, 80},
        {0, -25, -7, -9, 5, 22, 44, -19, 6, 11, 31, 47, 64},

        {51, 20, 5, 25, 14, 21, 20, 85, 94, 74, 82, 80, 80},
        {0, -24, -2, -8, 7, 24, 45, -20, 4, 11, 30, 47, 64},

        {54, 20, 7, 26, 18, 25, 24, 85, 95, 74, 83, 81, 80},
        {-2, -22, 5, -6, 12, 28, 48, -21, 0, 9, 29, 47, 64},

        {54, 20, 10, 28, 22, 30, 29, 85, 96, 75, 84, 80, 80},
        {-2, -18, 11, -5, 15, 32, 51, -27, -5, 8, 29, 47, 64},

        {54, 22, 12, 30, 25, 34, 33, 85, 96, 77, 84, 79, 80},
        {-1, -16, 17, -1, 20, 37, 56, -29, -15, 9, 29, 47, 64},

        {56, 22, 16, 31, 30, 37, 37, 86, 98, 78, 86, 79, 80},
        {0, -13, 21, 2, 25, 42, 61, -31, -22, 9, 29, 47, 64},

        {57, 25, 20, 37, 35, 40, 40, 87, 100, 78, 85, 81, 81},
        {0, -11, 23, 9, 31, 48, 64, -32, -26, 11, 28, 45, 62},

        {60, 26, 18, 40, 36, 40, 40, 90, 104, 81, 88, 82, 83},
        {0, -12, 20, 12, 31, 48, 64, -33, -26, 10, 25, 42, 60},

        {60, 27, 20, 45, 39, 41, 40, 93, 108, 84, 91, 85, 86},
        {1, -13, 19, 13, 31, 48, 64, -33, -24, 7, 22, 40, 58},

        // Prepare Jump
        {0, -33, -44, -21, -34, -41, -46, 30, 46, 21, 34, 41, 43},
        {0, -12, 14, 12, 26, 46, 62, -33, -29, 10, 25, 43, 61},

        {0, -33, -44, -21, -34, -41, -46, 30, 46, 21, 34, 41, 43},
        {3, -12, 14, 12, 26, 46, 62, -33, -29, 10, 25, 43, 61},

        {0, -32, -45, -20, -34, -41, -46, 31, 48, 21, 34, 41, 43},
        {2, -13, 10, 12, 27, 46, 62, -33, -28, 10, 25, 43, 61},

        {0, -32, -46, -20, -34, -41, -46, 30, 48, 20, 34, 41, 43},
        {4, -14, 7, 14, 27, 46, 62, -33, -28, 10, 25, 43, 61},

        {-2, -32, -46, -20, -34, -41, -46, 30, 48, 20, 34, 41, 43},
        {9, -14, 7, 14, 27, 46, 62, -33, -28, 10, 25, 43, 61},

        {-2, -33, -48, -20, -34, -41, -46, 30, 48, 22, 35, 41, 43},
        {9, -15, 2, 14, 27, 46, 62, -33, -30, 11, 25, 43, 61},

        {-2, -33, -48, -20, -34, -41, -46, 30, 48, 22, 35, 41, 43},
        {12, -15, 2, 14, 27, 46, 62, -33, -30, 11, 25, 43, 61},

        {-2, -33, -48, -22, -35, -43, -46, 29, 47, 21, 35, 41, 43},
        {12, -16, -1, 15, 27, 45, 62, -33, -30, 12, 25, 43, 61},

        {-2, -33, -48, -22, -35, -43, -46, 29, 47, 21, 35, 41, 43},
        {16, -16, -1, 15, 27, 45, 62, -33, -30, 12, 25, 43, 61},

        {-2, -33, -50, -22, -37, -44, -46, 29, 48, 22, 36, 42, 43},
        {16, -17, -6, 17, 29, 45, 62, -33, -32, 14, 26, 44, 61},

        {-2, -33, -50, -22, -37, -44, -46, 29, 48, 22, 36, 42, 43},
        {18, -17, -6, 17, 29, 45, 62, -33, -32, 14, 26, 44, 61},

        {-2, -32, -51, -23, -38, -45, -46, 29, 48, 22, 37, 42, 43},
        {18, -18, -12, 20, 30, 45, 62, -32, -32, 18, 27, 44, 61},

        {-2, -32, -51, -23, -38, -45, -46, 29, 48, 22, 37, 42, 43},
        {22, -18, -12, 20, 30, 45, 62, -32, -32, 18, 27, 44, 61},

        {-2, -34, -54, -26, -40, -46, -46, 29, 47, 25, 38, 45, 43},
        {22, -19, -16, 25, 33, 47, 62, -30, -34, 21, 29, 45, 61},

        {-3, -34, -54, -26, -40, -46, -46, 29, 47, 25, 38, 45, 43},
        {27, -19, -16, 25, 33, 47, 62, -30, -34, 21, 29, 45, 61},

        {-3, -35, -56, -26, -38, -46, -46, 29, 49, 24, 38, 45, 43},
        {27, -18, -16, 24, 32, 47, 62, -30, -31, 22, 30, 46, 61},

        {-3, -35, -56, -26, -38, -46, -46, 29, 49, 24, 38, 45, 43},
        {25, -18, -16, 24, 32, 47, 62, -30, -31, 22, 30, 46, 61},

        {-3, -36, -58, -26, -38, -46, -46, 29, 51, 23, 37, 44, 43},
        {25, -17, -12, 23, 32, 45, 62, -29, -27, 21, 30, 46, 61},

        {-3, -36, -58, -26, -38, -46, -46, 29, 51, 23, 37, 44, 43},
        {23, -17, -12, 23, 32, 45, 62, -29, -27, 21, 30, 46, 61},

        {-3, -36, -59, -26, -37, -45, -46, 31, 52, 23, 36, 43, 43},
        {23, -17, -10, 22, 32, 46, 62, -29, -23, 21, 30, 47, 61},

        {-3, -36, -59, -26, -37, -45, -46, 31, 52, 23, 36, 43, 43},
        {21, -17, -10, 22, 32, 46, 62, -29, -23, 21, 30, 47, 61},

        {-3, -37, -60, -26, -36, -44, -46, 31, 54, 24, 36, 43, 43},
        {21, -16, -7, 22, 32, 46, 62, -29, -19, 21, 31, 47, 61},

        {-3, -37, -60, -26, -36, -44, -46, 31, 54, 24, 36, 43, 43},
        {19, -16, -7, 22, 32, 46, 62, -29, -19, 21, 31, 47, 61},

        {-3, -37, -58, -24, -35, -42, -46, 33, 53, 23, 34, 41, 43},
        {19, -15, -3, 21, 32, 46, 62, -29, -19, 20, 32, 47, 61},

        {-3, -37, -58, -24, -35, -42, -46, 33, 53, 23, 34, 41, 43},
        {17, -15, -3, 21, 32, 46, 62, -29, -16, 20, 32, 47, 61},

        {-3, -36, -58, -23, -34, -41, -46, 34, 54, 22, 35, 40, 43},
        {17, -14, -1, 20, 32, 47, 62, -28, -12, 19, 31, 46, 61},

        // Jump
        {0, -35, -56, -21, -31, -39, -41, 36, 56, 25, 37, 43, 43},
        {0, -25, -10, 6, 18, 33, 48, -40, -20, 5, 18, 32, 48},

        {0, -35, -55, -20, -32, -38, -39, 37, 55, 24, 37, 42, 41},
        {0, -24, 0, 6, 17, 32, 48, -38, -15, 4, 18, 31, 47},

        {0, -36, -54, -20, -32, -38, -37, 36, 52, 24, 35, 43, 39},
        {0, -21, 2, 5, 17, 31, 47, -36, -12, 3, 17, 30, 46},

        {0, -36, -54, -19, -31, -38, -34, 36, 51, 23, 34, 41, 37},
        {0, -19, 7, 5, 16, 30, 45, -34, -6, 2, 15, 29, 45},

        {0, -35, -50, -18, -30, -37, -31, 36, 49, 23, 34, 40, 35},
        {0, -14, 13, 7, 18, 31, 45, -29, 1, 5, 17, 30, 46},

        {0, -34, -48, -18, -29, -36, -30, 37, 47, 23, 33, 40, 34},
        {0, -11, 15, 7, 18, 30, 44, -25, 7, 5, 17, 29, 44},

        {0, -31, -43, -17, -27, -34, -29, 37, 45, 23, 34, 40, 34},
        {0, -7, 20, 9, 19, 30, 45, -20, 14, 7, 17, 29, 44},

        {0, -30, -40, -17, -27, -34, -29, 36, 41, 23, 34, 42, 34},
        {0, -4, 24, 8, 18, 28, 44, -15, 18, 7, 15, 27, 42},

        {0, -29, -38, -17, -25, -33, -29, 35, 39, 22, 34, 42, 35},
        {0, -4, 25, 7, 16, 25, 42, -11, 24, 6, 12, 24, 38},

        {0, -29, -34, -17, -25, -33, -30, 34, 34, 21, 32, 43, 36},
        {0, 0, 31, 9, 17, 24, 42, -5, 25, 8, 13, 23, 38},

        {0, -25, -25, -16, -24, -36, -32, 33, 27, 20, 33, 44, 39},
        {0, 2, 38, 7, 11, 17, 34, 29, 30, 6, 9, 16, 32},

        {0, -25, -21, -17, -28, -40, -34, 30, 24, 21, 34, 47, 43},
        {0, 7, 41, 6, 7, 6, 27, 4, 32, 5, 4, 4, 26},

        {0, -25, -21, -17, -28, -40, -34, 30, 24, 21, 34, 47, 43},
        {0, 5, 39, 4, 5, 4, 25, 2, 30, 3, 2, 2, 24},

        // Land
        {0, -34, -53, -26, -41, -45, -43, 34, 52, 29, 45, 47, 45},
        {0, -33, -29, -1, 15, 34, 54, -34, -29, 0, 15, 33, 54},

        {0, -33, -50, -26, -43, -44, -43, 34, 52, 29, 47, 47, 45},
        {0, -34, -31, 5, 18, 35, 54, -35, -34, 5, 18, 34, 54},

        {0, -33, -50, -26, -43, -44, -43, 34, 52, 29, 47, 47, 45},
        {4, -34, -31, 5, 18, 35, 54, -35, -34, 5, 18, 34, 54},

        {0, -31, -49, -26, -46, -45, -43, 33, 51, 29, 50, 48, 45},
        {4, -34, -34, 11, 21, 36, 54, -36, -40, 10, 20, 34, 54},

        {0, -31, -49, -26, -46, -45, -43, 33, 51, 29, 50, 48, 45},
        {10, -34, -34, 11, 21, 36, 54, -36, -40, 10, 20, 34, 54},

        {0, -28, -45, -25, -45, -44, -43, 31, 50, 29, 52, 49, 45},
        {10, -33, -35, 16, 21, 35, 54, -36, -44, 15, 22, 36, 54},

        {0, -28, -45, -25, -45, -44, -43, 31, 50, 29, 52, 49, 45},
        {18, -33, -35, 16, 21, 35, 54, -36, -44, 15, 22, 36, 54},

        {0, -28, -44, -25, -45, -44, -43, 29, 46, 29, 52, 49, 45},
        {18, -33, -37, 14, 21, 35, 54, -36, -45, 15, 22, 36, 54},

        {0, -28, -44, -25, -45, -44, -43, 29, 46, 29, 52, 49, 45},
        {16, -33, -37, 14, 21, 35, 54, -36, -45, 15, 22, 36, 54},

        {0, -28, -46, -24, -43, -42, -43, 31, 48, 28, 47, 47, 45},
        {16, -33, -34, 11, 20, 35, 54, -37, -42, 11, 21, 35, 54},

        {0, -28, -46, -24, -43, -42, -43, 31, 48, 28, 47, 47, 45},
        {14, -33, -34, 11, 20, 35, 54, -37, -42, 11, 21, 35, 54},

        {0, -29, -47, -23, -42, -43, -43, 31, 49, 28, 44, 46, 45},
        {14, -32, -31, 9, 18, 34, 54, -36, -39, 9, 20, 34, 54},

        {0, -29, -47, -23, -42, -43, -43, 31, 49, 28, 44, 46, 45},
        {12, -32, -31, 9, 18, 34, 54, -36, -39, 9, 20, 34, 54},

        {0, -30, -48, -22, -39, -43, -43, 32, 50, 27, 40, 44, 45},
        {12, -32, -26, 7, 16, 33, 54, -36, -35, 7, 19, 34, 54},

        {0, -30, -48, -22, -39, -43, -43, 32, 50, 27, 40, 44, 45},
        {8, -32, -26, 7, 16, 33, 54, -36, -35, 7, 19, 34, 54},

        {0, -31, -49, -21, -37, -42, -43, 33, 51, 26, 37, 44, 45},
        {8, -31, -21, 5, 14, 32, 54, -35, -29, 3, 17, 33, 54},

        {0, -31, -49, -21, -37, -42, -43, 33, 51, 26, 37, 44, 45},
        {4, -31, -21, 5, 14, 32, 54, -35, -29, 3, 17, 33, 54}
    };
    // @formatter:on

    private static final String[] LIMBS =
    {
        "BossHead", "BossBowl", "BossHandLeft", "BossBowl", "BossBowl", "BossBowl", "BossLegLeft", "BossBowl",
        "BossHandRight", "BossBowl", "BossBowl", "BossBowl", "BossLegRight"
    };
    private final Transformable[] limbs = new Transformable[LIMBS.length];
    private final Animatable[] limbsAnim = new Animatable[LIMBS.length];
    private final Animator animator = new AnimatorModel();
    private final Tick tick = new Tick();

    private final Spawner spawner = services.get(Spawner.class);
    private final MusicPlayer music = services.get(MusicPlayer.class);
    private final LoadNextStage stage = services.get(LoadNextStage.class);
    private final Landscape landscape = services.get(Landscape.class);

    private final Animation rise;
    private final Animation idle;
    private final Animation walk;
    private final Animation preparejump;
    private final Animation jump;
    private final Animation land;

    private Stats stats;
    private double startX;
    private double startY;
    private double minY;
    private Updatable phase;

    @FeatureGet private EntityModel model;
    @FeatureGet private Transformable transformable;
    @FeatureGet private Identifiable identifiable;
    @FeatureGet private Body body;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public BossLava(Services services, Setup setup)
    {
        super(services, setup);

        final AnimationConfig config = AnimationConfig.imports(setup);
        rise = config.getAnimation("rise");
        idle = config.getAnimation(Anim.IDLE);
        walk = config.getAnimation(Anim.WALK);
        preparejump = config.getAnimation("preparejump");
        jump = config.getAnimation(Anim.JUMP);
        land = config.getAnimation(Anim.LAND);
    }

    /**
     * Create limb.
     * 
     * @param limb The limb name.
     * @return The created limb.
     */
    private Featurable create(String limb)
    {
        return spawner.spawn(Medias.create(setup.getMedia().getParentPath(), limb + Factory.FILE_DATA_DOT_EXTENSION),
                             transformable);
    }

    /**
     * Start phase and prepare for rise.
     * 
     * @param extrp The extrapolation value.
     */
    private void start(double extrp)
    {
        if (!tick.isStarted())
        {
            startY = transformable.getY();
            startX = transformable.getX();
            landscape.setEnabled(false);
            tick.start();
        }
        tick.update(extrp);
        body.resetGravity();
        transformable.teleportY(startY);

        if (tick.elapsed(TICK_RISE))
        {
            for (int i = 0; i < LIMBS.length; i++)
            {
                final Featurable featurable = create(LIMBS[i]);
                limbs[i] = featurable.getFeature(Transformable.class);
                limbsAnim[i] = featurable.getFeature(Animatable.class);
            }
            stats = limbs[0].getFeature(Stats.class);
            minY = startY;
            animator.play(rise);
            phase = this::updateRise;
        }
    }

    /**
     * Update rise phase. Play until animation ends and start idle.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateRise(double extrp)
    {
        animator.update(extrp);

        if (animator.getAnimState() == AnimState.FINISHED)
        {
            phase = this::updateIdleBeforeWalk;
            transformable.setLocationY(startY + Y);
            minY = startY + Y;
            animator.play(idle);
            tick.restart();
        }
    }

    /**
     * Update idle phase before walk phase. Play idle for a moment and start walk.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateIdleBeforeWalk(double extrp)
    {
        tick.update(extrp);
        animator.update(extrp);

        if (tick.elapsed(TICK_IDLE))
        {
            phase = this::updateWalk;
            transformable.setLocationY(startY + Y + 8);
            minY = startY + Y + 8;
            animator.play(walk);
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

        if (old == walk.getLast() && animator.getFrame() == walk.getFirst())
        {
            transformable.moveLocationX(1.0, WALK_OFFSET);
            if (transformable.getX() > startX + RANGE_X)
            {
                phase = this::updateIdleBeforeJump;
                transformable.setLocationY(startY + Y);
                minY = startY + Y;
                animator.play(idle);
                tick.restart();
            }
        }
    }

    /**
     * Update idle phase before preparing jump.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateIdleBeforeJump(double extrp)
    {
        tick.update(extrp);
        animator.update(extrp);

        if (tick.elapsed(TICK_IDLE))
        {
            phase = this::updatePrepareJump;
            transformable.setLocationY(startY + Y + 4);
            minY = startY + Y + 4;
            animator.play(preparejump);
        }
    }

    /**
     * Update prepare jump phase.
     * 
     * @param extrp The extrapolation value.
     */
    private void updatePrepareJump(double extrp)
    {
        animator.update(extrp);

        if (animator.getAnimState() == AnimState.FINISHED)
        {
            phase = this::updateJump;
            animator.play(jump);
            model.getJump().setDirection(0.0, 8.0);
            Sfx.BOSS3_JUMP.play();
            tick.stop();
        }
    }

    /**
     * Update jump phase moving left.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateJump(double extrp)
    {
        animator.update(extrp);
        tick.update(extrp);
        transformable.moveLocationX(extrp, -1.0);

        if (animator.getAnimState() == AnimState.FINISHED && transformable.getY() < transformable.getOldY())
        {
            tick.start();
            if (tick.elapsed(TICK_JUMP))
            {
                phase = this::updateFall;
                model.getJump().setDirection(0.0, 0.0);
                animator.play(jump);
                animator.setAnimSpeed(-animator.getAnimSpeed());
                animator.setFrame(jump.getLast());
            }
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
        transformable.moveLocationX(extrp, -1.0);

        if (Double.compare(transformable.getY(), minY) <= 0)
        {
            phase = this::updateLand;
            transformable.setLocationY(startY + Y);
            minY = startY + Y;
            animator.play(land);
        }
    }

    /**
     * Update land phase and check for next phase depending of location.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateLand(double extrp)
    {
        animator.update(extrp);

        if (animator.getAnimState() == AnimState.FINISHED)
        {
            if (transformable.getX() < startX + WALK_OFFSET)
            {
                phase = this::updateIdleBeforeWalk;
            }
            else
            {
                phase = this::updateIdleBeforeJump;
            }
            transformable.setLocationY(startY + Y);
            minY = startY + Y;
            animator.play(idle);
            tick.restart();
        }
    }

    /**
     * Check ground collision.
     */
    private void checkGroundCollision()
    {
        if (transformable.getY() < minY)
        {
            transformable.teleportY(minY);
            body.resetGravity();
        }
    }

    /**
     * Update limbs position.
     */
    private void updateLimbs()
    {
        final int id = animator.getFrame() - 1;
        final boolean hurting = limbsAnim[0].getFeature(Hurtable.class).isHurting();
        final int frame = limbsAnim[0].getFrame();

        for (int i = 0; i < LIMBS.length; i++)
        {
            limbs[i].setLocation(transformable.getX() + DATA[id * 2][i], transformable.getY() - DATA[id * 2 + 1][i]);
            if (i > 0)
            {
                limbsAnim[i].setFrame(hurting ? frame + 5 : frame);
            }
        }
    }

    @Override
    public void update(double extrp)
    {
        phase.update(extrp);

        if (stats != null)
        {
            checkGroundCollision();
            updateLimbs();

            if (stats.getHealth() == 0)
            {
                for (int i = 1; i < limbs.length; i++)
                {
                    limbs[i].getFeature(Hurtable.class).kill(true);
                }
                identifiable.destroy();
                music.playMusic(Music.BOSS_WIN);
                model.getNext().ifPresent(next -> stage.loadNextStage(next, END_TICK));
                stats = null;
            }
        }
    }

    @Override
    public void recycle()
    {
        stats = null;
        phase = this::start;
        tick.stop();
    }
}
