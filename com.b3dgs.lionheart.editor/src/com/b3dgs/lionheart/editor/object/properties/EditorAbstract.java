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
package com.b3dgs.lionheart.editor.object.properties;

import java.util.Optional;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * Editor dialog.
 * 
 * @param <C> The configuration type.
 */
public abstract class EditorAbstract<C> extends com.b3dgs.lionengine.editor.dialog.EditorAbstract
{
    /** Filled configuration. */
    protected Optional<C> output = Optional.empty();

    /**
     * Create editor.
     * 
     * @param parent The parent reference.
     * @param title The editor title.
     * @param icon The editor icon.
     */
    public EditorAbstract(Composite parent, String title, Image icon)
    {
        super(parent, title, icon);
    }

    @Override
    protected void createContent(Composite parent)
    {
        final Composite content = new Composite(parent, SWT.NONE);
        content.setLayout(new GridLayout(1, false));
        content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createFields(parent);
    }

    /**
     * Create editable fields.
     * 
     * @param parent The parent reference.
     */
    protected abstract void createFields(Composite parent);

    /**
     * Get output.
     * 
     * @return The output.
     */
    public Optional<C> getOutput()
    {
        return output;
    }
}
