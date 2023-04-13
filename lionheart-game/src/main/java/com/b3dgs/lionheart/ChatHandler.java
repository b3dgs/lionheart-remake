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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.IdentifiableListener;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionengine.io.DeviceController;
import com.b3dgs.lionengine.io.DeviceControllerListener;

/**
 * Chat handler dedicated to messages control and rendering.
 */
@FeatureInterface
public class ChatHandler extends FeatureModel implements Routine, IdentifiableListener
{
    private static final int MESSAGE_DELAY_MS = 5000;
    private static final int MESSAGES_POS_X = 2;
    private static final int MESSAGES_MAX = 6;
    private static final int MESSAGE_VALIDATE_KEY = 10;
    private static final int MESSAGE_CORRECT_KEY = 8;
    private static final String MESSAGE_START = ">";

    private final Text text = Graphics.createText(9);
    private final Deque<String> messagesShort = new ArrayDeque<>(MESSAGES_MAX);
    private final AtomicReference<StringBuilder> builder = new AtomicReference<>(new StringBuilder());
    private final Tick tick = new Tick();
    private final DeviceControllerListener listener = this::onDeviceChanged;

    private final SourceResolutionProvider source = services.get(SourceResolutionProvider.class);
    private final DeviceController device = services.get(DeviceController.class);
    private final Camera camera = services.get(Camera.class);

    private Consumer<String> consumer;
    private boolean typing;
    private String current;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public ChatHandler(Services services, Setup setup)
    {
        super(services, setup);

        device.addListener(listener);
        tick.start();
    }

    /**
     * Called on device changed.
     * 
     * @param name The device name.
     * @param push The push value.
     * @param c The char representation.
     * @param flag <code>true</code> if enabled, <code>false</code> if disabled.
     */
    private void onDeviceChanged(String name, Integer push, char c, boolean flag)
    {
        if (flag)
        {
            if (typing)
            {
                if (push.intValue() == MESSAGE_VALIDATE_KEY)
                {
                    validateMessage();
                }
                else if (push.intValue() == MESSAGE_CORRECT_KEY)
                {
                    correctMessage();
                }
                else
                {
                    typeMessage(c);
                }
            }
            else if (push.intValue() == MESSAGE_VALIDATE_KEY)
            {
                startMessage();
            }
        }
    }

    /**
     * Set message consumer called on message built.
     * 
     * @param consumer The consumer reference.
     */
    public void setConsumer(Consumer<String> consumer)
    {
        this.consumer = consumer;
    }

    /**
     * Add message to chat.
     * 
     * @param message The message to add.
     */
    public void add(String message)
    {
        if (messagesShort.size() > MESSAGES_MAX)
        {
            messagesShort.removeLast();
        }
        messagesShort.addFirst(message);
        tick.addAction(() ->
        {
            if (!messagesShort.isEmpty() && messagesShort.contains(message))
            {
                messagesShort.removeLast();
            }
        }, source.getRate(), MESSAGE_DELAY_MS);
    }

    /**
     * Check if currently in typing mode.
     * 
     * @return <code>true</code> if typing, <code>false</code> else.
     */
    public boolean isTyping()
    {
        return typing;
    }

    private void validateMessage()
    {
        final String message = builder.get().toString();
        add(message);
        if (consumer != null)
        {
            consumer.accept(message);
        }
        typing = false;
    }

    private void correctMessage()
    {
        final int n = builder.get().length() - 1;
        if (n > -1)
        {
            builder.get().deleteCharAt(n);
            current = MESSAGE_START + builder.get().toString();
        }
    }

    private void typeMessage(char c)
    {
        builder.get().append(c);
        current = MESSAGE_START + builder.get().toString();
    }

    private void startMessage()
    {
        builder.set(new StringBuilder());
        current = MESSAGE_START;
        typing = true;
    }

    @Override
    public void update(double extrp)
    {
        tick.update(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        g.setColor(ColorRgba.WHITE);
        if (typing)
        {
            text.draw(g, MESSAGES_POS_X, camera.getHeight() - (text.getSize() + 1) * MESSAGES_MAX - 2, current);
        }
        int y = 0;
        for (final String message : messagesShort)
        {
            text.draw(g, MESSAGES_POS_X, y + camera.getHeight() - text.getSize() * MESSAGES_MAX, message);
            y += text.getSize();
        }
    }

    @Override
    public void notifyDestroyed(Integer id)
    {
        device.removeListener(listener);
    }
}
