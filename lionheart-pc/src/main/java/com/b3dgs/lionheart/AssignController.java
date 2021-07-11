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
package com.b3dgs.lionheart;

import com.b3dgs.lionengine.Nameable;

/**
 * Controller.
 */
public abstract class AssignController implements AssignListener, CodeTranslator, Nameable
{
    private final AssignListener listener;
    private final CodeTranslator translator;

    /**
     * Create controller.
     * 
     * @param listener The listener.
     * @param translator The translator.
     */
    public AssignController(AssignListener listener, CodeTranslator translator)
    {
        this.listener = listener;
        this.translator = translator;
    }

    /**
     * Stop controller. Does nothing by default.
     */
    public void stop()
    {
        // Nothing by default
    }

    @Override
    public void awaitAssign(ActionGetter assigner)
    {
        listener.awaitAssign(assigner);
    }

    @Override
    public String getText(int code)
    {
        return translator.getText(code);
    }
}
