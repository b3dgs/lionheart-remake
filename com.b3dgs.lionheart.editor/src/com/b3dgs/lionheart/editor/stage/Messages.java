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
package com.b3dgs.lionheart.editor.stage;

import java.util.Locale;

import org.eclipse.osgi.util.NLS;

/**
 * Messages internationalization.
 */
public final class Messages extends NLS
{
    /** Error save title. */
    public static String ErrorSaveTitle;
    /** Error save message. */
    public static String ErrorSaveMessage;
    /** Error load title. */
    public static String ErrorLoadTitle;
    /** Error load message. */
    public static String ErrorLoadMessage;
    /** Picture message. */
    public static String Picture;
    /** Text message. */
    public static String Text;
    /** Music message. */
    public static String Music;
    /** Map message. */
    public static String Map;
    /** Raster message. */
    public static String Raster;
    /** Background message. */
    public static String Background;
    /** Foreground message. */
    public static String Foreground;
    /** Depth message. */
    public static String Depth;
    /** Offset message. */
    public static String Offset;
    /** Speed message. */
    public static String Speed;
    /** Effect message. */
    public static String Effect;
    /** Raise message. */
    public static String Raise;

    /**
     * Initialize.
     */
    static
    {
        NLS.initializeMessages(Messages.class.getName().toLowerCase(Locale.ENGLISH), Messages.class);
    }
}
