package com.b3dgs.lionheart.editor;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionheart.Constant;

public class Util
{
    /**
     * List of supported level formats.
     * 
     * @return Supported level formats.
     */
    public static String[] getLevelFilter()
    {
        return new String[]
        {
            Constant.EXTENSION_LEVEL.substring(1)
        };
    }

    /**
     * Private constructor.
     */
    private Util()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
