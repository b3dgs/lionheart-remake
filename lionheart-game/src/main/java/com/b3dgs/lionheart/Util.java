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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.UtilConversion;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionFormulaConfig;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionGroupConfig;
import com.b3dgs.lionengine.game.feature.tile.map.collision.MapTileCollision;
import com.b3dgs.lionengine.game.feature.tile.map.persister.MapTilePersister;
import com.b3dgs.lionengine.geom.Coord;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.lionengine.graphic.TextStyle;
import com.b3dgs.lionengine.graphic.engine.FilterNone;
import com.b3dgs.lionengine.graphic.engine.Loop;
import com.b3dgs.lionengine.graphic.engine.LoopHybrid;
import com.b3dgs.lionengine.graphic.engine.LoopUnlocked;
import com.b3dgs.lionengine.graphic.engine.ScanlineNone;
import com.b3dgs.lionengine.graphic.engine.Sequence;
import com.b3dgs.lionengine.graphic.filter.FilterBlur;
import com.b3dgs.lionengine.graphic.filter.FilterCrt;
import com.b3dgs.lionengine.graphic.filter.FilterHq2x;
import com.b3dgs.lionengine.graphic.filter.FilterHq3x;
import com.b3dgs.lionengine.graphic.scanline.ScanlineHorizontal;
import com.b3dgs.lionengine.io.FileReading;
import com.b3dgs.lionengine.io.FileWriting;
import com.b3dgs.lionheart.constant.Extension;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.landscape.BackgroundType;
import com.b3dgs.lionheart.object.XmlLoader;
import com.b3dgs.lionheart.object.feature.Stats;

/**
 * Static utility functions.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public final class Util
{
    private static final int MARGIN_X = 1;
    private static final int MARGIN_Y = 5;
    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Util.class);

    private static AtomicReference<Consumer<BackgroundType>> init = new AtomicReference<>();

    /**
     * Init task.
     * 
     * @param init The init task.
     */
    public static void init(Consumer<BackgroundType> init)
    {
        Util.init.set(init);
    }

    /**
     * Run init task.
     * 
     * @param type The background type.
     */
    public static void run(BackgroundType type)
    {
        final Consumer<BackgroundType> t = init.get();
        if (t != null)
        {
            t.accept(type);
        }
    }

    /**
     * Get loop instance.
     * 
     * @param desktop The desktop resolution.
     * @return The loop instance.
     */
    public static Loop getLoop(Resolution desktop)
    {
        final Settings settings = Settings.getInstance();
        final LoopFactory factory;
        if (settings.isFlagVsync() && !settings.isResolutionWindowed())
        {
            factory = LoopUnlocked::new;
        }
        else
        {
            factory = LoopHybrid::new;
        }
        return factory.create(Constant.RESOLUTION, settings.getResolution(desktop));
    }

    /**
     * Set scene filter.
     * 
     * @param sequence The sequence reference.
     * @param context The context reference.
     * @param source The source resolution.
     * @param scale The filter scale.
     */
    public static void setFilter(Sequence sequence, Context context, Resolution source, int scale)
    {
        final Settings settings = Settings.getInstance();
        final FilterType filter = settings.getFilter();
        if (FilterType.BLUR == filter)
        {
            final FilterBlur blur = new FilterBlur();
            blur.setRadius(1.3F);
            sequence.setFilter(blur);
            sequence.setScanline(ScanlineNone.INSTANCE);
        }
        else if (FilterType.HQ2X == filter)
        {
            sequence.setFilter(new FilterHq2x());
            sequence.setScanline(ScanlineNone.INSTANCE);
        }
        else if (FilterType.HQ3X == filter)
        {
            sequence.setFilter(new FilterHq3x());
            sequence.setScanline(ScanlineNone.INSTANCE);
        }
        else if (FilterType.SCANLINE == filter)
        {
            sequence.setScanline(new ScanlineHorizontal(Util.getResolution(source, context), 2));
            sequence.setFilter(FilterNone.INSTANCE);
        }
        else if (FilterType.CRT == filter)
        {
            final FilterCrt crt = new FilterCrt(scale);
            sequence.setFilter(crt);
            sequence.setScanline(ScanlineNone.INSTANCE);
        }
    }

    /**
     * Get resolution adapted to output from source.
     * 
     * @param source The source resolution.
     * @param context The context reference.
     * @return The adjusted source resolution based on output wide.
     */
    public static Resolution getResolution(Resolution source, Context context)
    {
        final Resolution output = context.getConfig().getOutput();
        final double factor = source.getHeight() / (double) output.getHeight();
        return new Resolution((int) Math.round(output.getWidth() * factor),
                              (int) Math.round(output.getHeight() * factor),
                              source.getRate());
    }

    /**
     * Get resolution adapted to output from source.
     * 
     * @param context The context reference.
     * @param minHeight The minimum height.
     * @param maxWidth The maximum width.
     * @param marginWidth The width margin.
     * @return The adjusted source resolution based on output wide.
     */
    public static Resolution getResolution(Context context, int minHeight, int maxWidth, int marginWidth)
    {
        final Resolution output = context.getConfig().getOutput();
        final Resolution adjusted = getResolution(Constant.RESOLUTION, context);
        final double ratio = (double) output.getWidth() / (double) output.getHeight();
        final int width = adjusted.getWidth() - (adjusted.getWidth() - maxWidth + marginWidth);
        final int height = (int) Math.round(width / ratio);

        final Resolution resolution;
        if (height < minHeight)
        {
            resolution = new Resolution((int) Math.ceil(minHeight * ratio), minHeight, adjusted.getRate());
        }
        else
        {
            resolution = new Resolution(width, height, adjusted.getRate());
        }

        return resolution;
    }

    /**
     * Read media lines.
     * 
     * @param media The media reference.
     * @return The ordered lines found.
     */
    public static List<String> readLines(Media media)
    {
        final List<String> lines = new ArrayList<>();
        try (BufferedReader data = new BufferedReader(new InputStreamReader(media.getInputStream(),
                                                                            StandardCharsets.UTF_8)))
        {
            String line;
            while ((line = data.readLine()) != null)
            {
                lines.add(line);
            }
        }
        catch (final IOException exception)
        {
            LOGGER.error("readLines error", exception);
        }
        return lines;
    }

    /**
     * Convert multi line text to single line text with separator for font.
     * 
     * @param media The media reference.
     * @return The text single line with separator.
     */
    public static String toFontText(Media media)
    {
        final StringBuilder builder = new StringBuilder();
        final List<String> lines = Util.readLines(media);
        final int n = lines.size();
        for (int i = 0; i < n; i++)
        {
            builder.append(lines.get(i));
            if (i < n - 1)
            {
                builder.append('%');
            }
        }
        return builder.toString();
    }

    /**
     * Load map tiles data.
     * 
     * @param map The map reference.
     * @param media The map tiles data.
     */
    public static void loadMapTiles(MapTile map, Media media)
    {
        try (FileReading reading = new FileReading(media))
        {
            final MapTilePersister mapPersister = map.getFeature(MapTilePersister.class);
            mapPersister.load(reading);
            final MapTileCollision mapCollision = map.getFeature(MapTileCollision.class);
            mapCollision.loadCollisions(Medias.create(Folder.LEVEL, CollisionFormulaConfig.FILENAME),
                                        Medias.create(Folder.LEVEL, CollisionGroupConfig.FILENAME));
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception);
        }
    }

    /**
     * Load entity data.
     * 
     * @param entity The entity to load.
     * @param config The config data.
     */
    public static void loadEntityFeature(Featurable entity, EntityConfig config)
    {
        entity.getFeatures().forEach(feature ->
        {
            if (feature instanceof XmlLoader)
            {
                ((XmlLoader) feature).load(config.getRoot());
            }
        });
    }

    /**
     * Get stage by difficulty.
     * 
     * @param stages The stages set.
     * @param difficulty The difficulty (<code>null</code> to ignore).
     * @param index The stage index.
     * @return The stage media.
     */
    public static Media getStage(String stages, Difficulty difficulty, int index)
    {
        final String folder = stages.toLowerCase(Locale.ENGLISH);
        final Media stage = Medias.create(Folder.STAGE,
                                          Folder.STORY,
                                          folder,
                                          Constant.STAGE_PREFIX + index + Constant.STAGE_HARD_SUFFIX + Extension.STAGE);
        if (difficulty != null && difficulty.is(Difficulty.HARD, Difficulty.LIONHARD) && stage.exists())
        {
            return stage;
        }
        return Medias.create(Folder.STAGE, Folder.STORY, folder, Constant.STAGE_PREFIX + index + Extension.STAGE);
    }

    /**
     * Show menu at cursor location.
     * 
     * @param viewer The viewer reference.
     * @param cursor The cursor reference.
     * @param menus The menus to show.
     * @param ox The horizontal cursor offset.
     * @param oy The vertical cursor offset.
     */
    public static void showMenu(Viewer viewer, Cursor cursor, List<CheatMenu> menus, double ox, double oy)
    {
        int w = 0;
        int h = 0;
        for (int i = 0; i < menus.size(); i++)
        {
            final CheatMenu menu = menus.get(i);
            w = menu.getWidth();
            h += menu.getHeight();
        }
        h++;

        final int mx = viewer.getWidth() - (w + 1);
        final int my = viewer.getHeight() - h;
        double cx = cursor.getScreenX() + ox;
        double cy = cursor.getScreenY() + oy;

        if (cx > mx)
        {
            cx = mx;
        }
        if (cy > my)
        {
            cy = my;
        }
        double x = cx;

        for (int i = 0; i < menus.size(); i++)
        {
            double y = cy + i * menus.get(i).getHeight();
            if (y < 0)
            {
                cy -= y;
                y = 0;
            }
            if (y > viewer.getHeight() - 1)
            {
                final double offsetX = menus.get(i).getWidth() * Math.floor(y / viewer.getHeight());
                if (cx + offsetX > viewer.getWidth() - 2)
                {
                    x = cx - offsetX;
                }
                else
                {
                    x = cx + offsetX;
                }
                y = y - viewer.getHeight();
            }
            menus.get(i).spawn(x, y - 1);
        }
    }

    /**
     * Generate sprint font from text.
     * 
     * @param font The font name.
     * @param textSize The text size.
     * @param style The text style.
     * @param color The text color.
     */
    public static void generateFont(String font, int textSize, TextStyle style, ColorRgba color)
    {
        final Text text = Graphics.createText(font, textSize, style);
        final int size = text.getSize() - 1;
        text.setColor(color);

        final char[] letters = getLetters();
        final int[] mult = UtilMath.getClosestSquareMult(letters.length, letters.length);

        final ImageBuffer buffer = Graphics.createImageBuffer((size + MARGIN_X) * mult[0], (size + MARGIN_Y) * mult[1]);
        buffer.prepare();

        final Graphic g = buffer.createGraphic();
        drawAll(letters, size, mult[0], text, g);
        g.dispose();

        Graphics.saveImage(buffer, Medias.create("font.png"));
    }

    /**
     * Get init configuration.
     * 
     * @param stage The associated stage.
     * @param player The player reference.
     * @param difficulty The current difficulty
     * @param cheats The current cheats.
     * @param spawn The next spawn.
     * @return The init configuration.
     */
    public static InitConfig getInitConfig(Media stage,
                                           FeatureProvider player,
                                           Difficulty difficulty,
                                           boolean cheats,
                                           Optional<Coord> spawn)
    {
        final Stats stats = player.getFeature(Stats.class);
        return new InitConfig(stage,
                              stats.getHealthMax(),
                              stats.getTalisment(),
                              stats.getLife(),
                              stats.getSword(),
                              stats.hasAmulet().booleanValue(),
                              stats.getCredits(),
                              difficulty,
                              cheats,
                              spawn);
    }

    /**
     * Save progress.
     * 
     * @param config The game configuration.
     */
    public static void saveProgress(GameConfig config)
    {
        if (config.getStages().isPresent() && new File(Medias.getResourcesDirectory()).isDirectory())
        {
            final Media media = Medias.create(Folder.STAGE,
                                              Folder.STORY,
                                              config.getStages().get(),
                                              Constant.FILE_PROGRESS);
            try
            {
                final File file = new File(Medias.getResourcesDirectory(), media.getPath());
                if (!file.getParentFile().isDirectory())
                {
                    file.getParentFile().mkdirs();
                }
                if (!file.isFile() && !file.createNewFile())
                {
                    LOGGER.warn("Unable to create progress: {}", file);
                }

                final InitConfig init = config.getInit();
                try (FileWriting writing = new FileWriting(media))
                {
                    writing.writeString(init.getStage().getPath());
                    writing.writeByte(UtilConversion.fromUnsignedByte(init.getHealthMax()));
                    writing.writeByte(UtilConversion.fromUnsignedByte(init.getTalisment()));
                    writing.writeByte(UtilConversion.fromUnsignedByte(init.getLife()));
                    writing.writeByte(UtilConversion.fromUnsignedByte(init.getSword()));
                    writing.writeBoolean(init.isAmulet());
                    writing.writeByte(UtilConversion.fromUnsignedByte(init.getCredits()));
                    writing.writeString(init.getDifficulty().name());
                    writing.writeBoolean(init.isCheats());
                }
            }
            catch (final IOException | SecurityException exception)
            {
                LOGGER.error("saveProgress error", exception);
            }
        }
    }

    /**
     * Load progress from file.
     * 
     * @param config The game configuration.
     * @return The progress loaded.
     * @throws IOException If error.
     */
    public static InitConfig loadProgress(GameConfig config) throws IOException
    {
        if (config.getStages().isPresent())
        {
            try (FileReading reading = new FileReading(Medias.create(Folder.STAGE,
                                                                     Folder.STORY,
                                                                     config.getStages().get(),
                                                                     Constant.FILE_PROGRESS)))
            {
                return new InitConfig(Medias.create(reading.readString()),
                                      UtilConversion.toUnsignedByte(reading.readByte()),
                                      UtilConversion.toUnsignedByte(reading.readByte()),
                                      UtilConversion.toUnsignedByte(reading.readByte()),
                                      UtilConversion.toUnsignedByte(reading.readByte()),
                                      reading.readBoolean(),
                                      UtilConversion.toUnsignedByte(reading.readByte()),
                                      Difficulty.valueOf(reading.readString()),
                                      reading.readBoolean(),
                                      Optional.empty());
            }
        }
        return null;
    }

    private static char[] getLetters()
    {
        final char[] letters = new char[102];

        int i = 0;
        i = addRange(letters, i, 'a', 'z');
        i = addRange(letters, i, 'A', 'Z');
        i = addRange(letters, i, '0', '9');
        i = addList(letters, i, 'à', 'á', 'ç', 'è', 'é', 'ì', 'í', 'ñ', 'ó', 'ô', 'ö', 'ú', 'û', 'ü');
        i = addList(letters, i, 'À', 'Á', 'Ç', 'È', 'É', 'Ì', 'Í', 'Ñ', 'Ó', 'Ô', 'Ö', 'Ú', 'Û', 'Ü');
        i = addList(letters, i, ',', '?', '!', '.', '-', ':', '*', '/', '&', '(', ')', ' ');

        return letters;
    }

    private static int addRange(char[] letters, int start, char first, char last)
    {
        int i = 0;
        for (char c = first; c <= last; c++)
        {
            letters[start + i] = c;
            i++;
        }
        return start + i;
    }

    private static int addList(char[] chars, int start, char... c)
    {
        System.arraycopy(c, 0, chars, start, c.length);
        return start + c.length;
    }

    private static void drawAll(char[] letters, int size, int horizontals, Text text, Graphic g)
    {
        int x = 0;
        int y = 0;
        final Xml root = new Xml("lionengine:letters");
        for (int i = 0; i < letters.length; i++)
        {
            final String s = String.valueOf(letters[i]);
            text.draw(g, x * (size + MARGIN_X) + MARGIN_X, y * (size + MARGIN_Y) + MARGIN_Y, Align.LEFT, s);

            final Xml node = root.createChild("lionengine:letter");
            node.writeString("char", s);
            node.writeInteger("width", text.getStringWidth(g, s) - 1);
            node.writeInteger("height", 0);

            x++;
            if (x >= horizontals)
            {
                x = 0;
                y++;
            }
        }
        root.save(Medias.create("font.xml"));
    }

    /**
     * Private constructor.
     */
    private Util()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }

    /**
     * Loop factory.
     */
    @FunctionalInterface
    private interface LoopFactory
    {
        /**
         * Create loop.
         * 
         * @param rateOriginal The original rate;
         * @param rateDesired The desired rate.
         * @return The created loop.
         */
        Loop create(int rateOriginal, int rateDesired);

        /**
         * Create loop.
         * 
         * @param original The original resolution;
         * @param desired The desired resolution.
         * @return The created loop.
         */
        default Loop create(Resolution original, Resolution desired)
        {
            return create(original.getRate(), desired.getRate());
        }
    }
}
