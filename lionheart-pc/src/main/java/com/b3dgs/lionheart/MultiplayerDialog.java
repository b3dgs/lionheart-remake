/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.NumberFormatter;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionheart.constant.Folder;

/**
 * Multiplayer dialog parameters.
 */
public class MultiplayerDialog extends JDialog
{
    private static final int PLAYERS_MIN = 1;
    private static final int PLAYERS_MAX = 8;

    private static final Font FONT = new Font(Font.MONOSPACED, Font.PLAIN, 20);
    private static final Border BORDER = BorderFactory.createEmptyBorder(3, 10, 3, 10);

    private Media stage;
    private final AtomicReference<GameType> type = new AtomicReference<>(GameType.SPEEDRUN);
    private final AtomicInteger players = new AtomicInteger(2);
    private final AtomicInteger life = new AtomicInteger(0);
    private final AtomicInteger health = new AtomicInteger(1);
    private final AtomicBoolean online = new AtomicBoolean(false);

    /**
     * Create dialog.
     * 
     * @param parent The parent frame.
     */
    public MultiplayerDialog(Window parent)
    {
        super(parent);

        final BorderLayout layout = new BorderLayout();
        setLayout(layout);

        final Box box = Box.createVerticalBox();
        add(box, BorderLayout.CENTER);

        createType(box);
        final JSlider players = createPlayers(box);
        createStart(box, players);

        pack();
        setModalityType(ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(getParent());

        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                MultiplayerDialog.this.players.set(0);
            }
        });
    }

    /**
     * Get the game type.
     * 
     * @return The game type.
     */
    public GameType getGameType()
    {
        return type.get();
    }

    /**
     * Get selected stage.
     * 
     * @return The selected stage.
     */
    public Media getStage()
    {
        return stage;
    }

    /**
     * Get life.
     * 
     * @return The life.
     */
    public int getLife()
    {
        return life.get();
    }

    /**
     * Get health.
     * 
     * @return The health.
     */
    public int getHealth()
    {
        return health.get();
    }

    /**
     * Get players number.
     * 
     * @return The players number.
     */
    public int getPlayers()
    {
        return players.get();
    }

    /**
     * Check if is online or local.
     * 
     * @return <code>true</code> if online, <code>false</code> if local.
     */
    public boolean isOnline()
    {
        return online.get();
    }

    private void createType(JComponent parent)
    {
        final JLabel label = new JLabel("Type: ");
        label.setFont(FONT);
        label.setHorizontalAlignment(SwingConstants.RIGHT);

        final Box panel = Box.createVerticalBox();

        final JComboBox<GameType> combo = new JComboBox<>(GameType.values());
        combo.setFont(FONT);
        combo.addActionListener(e ->
        {
            panel.removeAll();

            final GameType type = combo.getItemAt(combo.getSelectedIndex());
            this.type.set(type);
            switch (type)
            {
                case SPEEDRUN:
                    createSpeedrun(panel);
                    break;
                case BATTLE:
                    createBattle(panel);
                    break;
                case COOP:
                    createCoop(panel);
                    break;
                default:
                    throw new LionEngineException(type);
            }
            panel.validate();
        });
        combo.setSelectedItem(type.get());

        final Box box = Box.createHorizontalBox();
        box.setBorder(BORDER);
        box.add(label);
        box.add(combo);

        final Box boxVertical = Box.createVerticalBox();
        boxVertical.add(box);
        boxVertical.add(panel);

        parent.add(boxVertical);
    }

    private void createSpeedrun(JComponent parent)
    {
        createStage(parent, Medias.create(Folder.STAGE, Folder.SPEEDRUN).getMedias());
    }

    private void createBattle(JComponent parent)
    {
        createStats(parent);
        createStage(parent, Medias.create(Folder.STAGE, Folder.BATTLE).getMedias());
    }

    private void createCoop(JComponent parent)
    {
        createStage(parent, Medias.create(Folder.STAGE, Folder.COOP).getMedias());
    }

    private void createStats(JComponent parent)
    {
        final NumberFormat format = NumberFormat.getIntegerInstance(Locale.ENGLISH);
        format.setGroupingUsed(false);
        final NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true);

        final JLabel labelLife = new JLabel("Life: ");
        labelLife.setFont(FONT);

        final JFormattedTextField life = new JFormattedTextField(formatter);
        life.setFont(FONT);
        life.setHorizontalAlignment(SwingConstants.RIGHT);
        life.setText(String.valueOf(this.life));
        life.setColumns(2);
        life.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void insertUpdate(DocumentEvent event)
            {
                Launcher.updateField(MultiplayerDialog.this.life, life);
            }

            @Override
            public void removeUpdate(DocumentEvent event)
            {
                Launcher.updateField(MultiplayerDialog.this.life, life);
            }

            @Override
            public void changedUpdate(DocumentEvent event)
            {
                // Nothing to do
            }
        });

        final Box boxLife = Box.createHorizontalBox();
        boxLife.setBorder(BORDER);
        boxLife.add(labelLife);
        boxLife.add(life);

        parent.add(boxLife);

        final JLabel labelHealth = new JLabel("Health: ");
        labelHealth.setFont(FONT);

        final JFormattedTextField health = new JFormattedTextField(formatter);
        health.setFont(FONT);
        health.setHorizontalAlignment(SwingConstants.RIGHT);
        health.setText(String.valueOf(this.health));
        health.setColumns(1);
        health.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void insertUpdate(DocumentEvent event)
            {
                Launcher.updateField(MultiplayerDialog.this.health, health);
            }

            @Override
            public void removeUpdate(DocumentEvent event)
            {
                Launcher.updateField(MultiplayerDialog.this.health, health);
            }

            @Override
            public void changedUpdate(DocumentEvent event)
            {
                // Nothing to do
            }
        });

        final Box boxHealth = Box.createHorizontalBox();
        boxHealth.setBorder(BORDER);
        boxHealth.add(labelHealth);
        boxHealth.add(health);

        parent.add(boxHealth);
    }

    private void createStage(JComponent parent, Collection<Media> stages)
    {
        final JLabel label = new JLabel("Stage: ");
        label.setFont(FONT);
        label.setHorizontalAlignment(SwingConstants.RIGHT);

        final JComboBox<Media> combo = new JComboBox<>(wrap(stages).toArray(new Media[stages.size()]));
        combo.setFont(FONT);
        combo.addActionListener(e ->
        {
            stage = combo.getItemAt(combo.getSelectedIndex());
        });
        if (!stages.isEmpty())
        {
            stage = stages.iterator().next();
            combo.setSelectedItem(stage);
        }

        final Box box = Box.createHorizontalBox();
        box.setBorder(BORDER);
        box.add(label);
        box.add(combo);

        parent.add(box);
    }

    private JSlider createPlayers(JComponent parent)
    {
        final JLabel label = new JLabel("Players: ");
        label.setFont(FONT);
        label.setHorizontalAlignment(SwingConstants.RIGHT);

        final JSlider slider = new JSlider(PLAYERS_MIN, PLAYERS_MAX, players.get());

        final JLabel result = new JLabel(String.valueOf(slider.getValue()));
        result.setFont(FONT);
        result.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        slider.addChangeListener(e ->
        {
            players.set(slider.getValue());
            result.setText(String.valueOf(slider.getValue()));
        });

        final Box box = Box.createHorizontalBox();
        box.setBorder(BORDER);
        box.add(label);
        box.add(slider);
        box.add(result);

        parent.add(box);

        return slider;
    }

    private void createStart(JComponent parent, JSlider players)
    {
        final JButton local = new JButton("Local");
        local.setFont(FONT);
        local.addActionListener(event ->
        {
            online.set(false);
            dispose();
        });

        final JButton online = new JButton("Online");
        online.setFont(FONT);
        online.addActionListener(event ->
        {
            this.online.set(true);
            dispose();
        });

        final JButton back = new JButton("Back");
        back.setFont(FONT);
        back.addActionListener(event ->
        {
            this.players.set(0);
            dispose();
        });

        players.addChangeListener(e -> local.setEnabled(players.getValue() < 5));

        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.5;

        final JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BORDER);
        panel.add(local, constraints);
        panel.add(online, constraints);
        panel.add(back, constraints);

        parent.add(panel);
    }

    private static Collection<Media> wrap(Collection<Media> media)
    {
        return media.stream().map(MultiplayerDialog::wrap).sorted().collect(Collectors.toList());
    }

    private static Media wrap(Media media)
    {
        return new Media()
        {

            @Override
            public boolean isJar()
            {
                return media.isJar();
            }

            @Override
            public URL getUrl()
            {
                return media.getUrl();
            }

            @Override
            public String getPath()
            {
                return media.getPath();
            }

            @Override
            public String getParentPath()
            {
                return media.getParentPath();
            }

            @Override
            public OutputStream getOutputStream()
            {
                return media.getOutputStream();
            }

            @Override
            public String getName()
            {
                return media.getName();
            }

            @Override
            public Collection<Media> getMedias()
            {
                return media.getMedias();
            }

            @Override
            public InputStream getInputStream()
            {
                return media.getInputStream();
            }

            @Override
            public File getFile()
            {
                return media.getFile();
            }

            @Override
            public boolean exists()
            {
                return media.exists();
            }

            @Override
            public String toString()
            {
                return media.getName();
            }
        };
    }
}
