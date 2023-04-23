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

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Window;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

/**
 * Action getter dialog.
 */
public class ActionGetter extends JDialog
{
    private static final Font FONT = new Font("Monospaced", Font.PLAIN, 20);

    private static final int BORDER_SIZE = 10;
    private static final String LABEL_PRESS = " action";
    private static final String LABEL_AWAIT = "Await action...";

    private final AtomicInteger code = new AtomicInteger(-1);

    /**
     * Create dialog.
     * 
     * @param owner The owner.
     * @param name The name.
     */
    public ActionGetter(Window owner, String name)
    {
        super(owner, name + LABEL_PRESS, Dialog.ModalityType.DOCUMENT_MODAL);

        setLayout(new BorderLayout(BORDER_SIZE, BORDER_SIZE));

        final JLabel label = new JLabel(LABEL_AWAIT);
        label.setFont(FONT);
        label.setBorder(new EmptyBorder(BORDER_SIZE, BORDER_SIZE, BORDER_SIZE, BORDER_SIZE));
        add(label);

        setResizable(false);
    }

    /**
     * Assign code.
     * 
     * @param code The code value.
     */
    public void assign(int code)
    {
        this.code.set(code);
        dispose();
    }

    /**
     * Get assigned key.
     * 
     * @return The assigned key.
     */
    public int getKey()
    {
        return code.get();
    }
}
