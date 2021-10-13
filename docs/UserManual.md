***
| Table |
|:---:|
| [Launcher](#launcher) |
| [Controls](#controls) |
| [Menu](#menu) |
| [Options](#options) |
| [Scene](#scene) |
| [Credits](#credits) |
| [Cheats](#cheats) |
***

# Launcher
[[/manual/launcher.png]]
* Lang: List of game languages
* Scale & Ratio: Based on original one, provide a perfect scaled resolution with custom ratio
* Available: List of supported screen resolution
* Resolution: Current resolution (can be manually edited)
* Hz: List of supported refresh rate
* Windowed: selected = windowed, unselected = true fullscreen
    * Note: Windowed mode with max screen resolution will provide full windowed mode
* Music & Sfx: Volume level
* Stages: Selection of stages set
* Strategy: Advanced flag to balance between loading speed and memory usage
* Parallel: Allow parallel loading
* VSync: selected = GPU sync (no screen tearing, low cpu usage, some input latency), unselected = CPU sync (low latency, adapted to VRR)
* Raster: selected = enable raster bar effect (more colors), unselected = disable raster bar
* Hud: Visible = control global hud visibility, Sword = display sword level (not visible on original game)
* Zoom: Game scene clip scaled ratio (original game scene is clipped at 280x208 instead of 320x240)
* Gamepad: List of detected gamepad by index
* Setup Keyboard & Gamepad: Customize input keys
* Play: Save settings and start game (exit launcher on started)
* Exit: Save settings and exit launcher

# Controls
* ### Computer
  * Arrowkeys
    * Menu interaction
    * Player movement
  * CTRL_RIGHT
    * Intro skip / Menu validation
    * Player attack
  * P: Game pause
  * ESC: Ask for exit, ESC again will exit, CTRL_RIGHT will cancel
  * Mouse: menu click

* ### Android
  * Menu / Intro
    * Validation / Skip: Double touch screen
    * Go up: Touch screen upper part
    * Go down: Touch screen lower part
    * Go left: Touch screen left part
    * Go right: Touch screen right part
  * Player
    * J: Jump
    * F: Attack
    * <: Move left
    * \>: Move right
    * \\/: Crouch

# Menu
## Main
[[/manual/main.png]]
* Change with vertical direction
* Press fire to validate

| Label | Description |
| :---: | --- |
| **Start game** | Will start a new game |
| **Options** | Go to options menu |
| **Introduction** | Launch introduction scene |
| **Quit** | Terminate application |

## Options
[[/manual/options.png]]
* Change with vertical direction
* Choose option with horizontal direction
* Press fire to validate on **Done**

| Label | Description |
| :---: | --- |
| **Difficulty** | Change game difficulty (**Normal**: default, **Hard**: Alternative stages, **Lionhard**: Enemies with twice health) |
| **Joystick** | Joystick configuration (**1 button**: jump with direction, **2 buttons**: jump with button) |
| **Soundtest** | Press fire to play music |
| **Done** | Go back main menu |

# Scene
[[/manual/scene.png]]
1. Health
   * Bright red: remaining
   * Dark red: lost
   * White: to be unlocked with 99 **Talisment**
2. Talisment
   * Current talisment count.
   * Reset to 0 after 99.
3. Life
   * Remaining life before game credits
   * If no more credits available, it goes to main menu
4. Player
5. Monster

# Credits
[[/manual/credits.png]]
* Choose with horizontal direction
* Press fire to validate

| Label | Description |
| :---: | --- |
| **Yes** | Continue to last level lost |
| **No** | Go to main menu |

# Cheats
* Move mouse (cursor will be visible), and right click somewhere to popup cheats menu.
* Same as original : move down, press "pause", CTRL + PAGE_DOWN
    * Screen will shake
    * CTRL_RIGHT: free fly with mouse (pc) / directional keys
    * F1-F10 & 1-5: Stage jump