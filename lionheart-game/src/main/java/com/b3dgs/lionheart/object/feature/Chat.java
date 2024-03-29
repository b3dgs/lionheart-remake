/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilConversion;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.networkable.Networkable;
import com.b3dgs.lionengine.game.feature.networkable.Syncable;
import com.b3dgs.lionengine.network.Packet;
import com.b3dgs.lionheart.ChatHandler;
import com.b3dgs.lionheart.object.EntityModel;

/**
 * Chat representation allowing to exchange messages.
 */
@FeatureInterface
public class Chat extends FeatureModel implements Syncable
{
    private final Map<Integer, String> clients = services.get(ConcurrentHashMap.class);
    private final ChatHandler chat = services.getOptional(ChatHandler.class).orElse(null);

    private final Networkable networkable;
    private final EntityModel model;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @param networkable The networkable feature.
     * @param model The model feature.
     * @throws LionEngineException If invalid arguments.
     */
    public Chat(Services services, Setup setup, Networkable networkable, EntityModel model)
    {
        super(services, setup);

        this.networkable = networkable;
        this.model = model;
    }

    private void sendMessage(String message)
    {
        final int length = message.length();
        final ByteBuffer nameBuffer = StandardCharsets.UTF_8.encode(message);
        final ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES + 1 + length);

        buffer.putInt(getSyncId());
        buffer.put(UtilConversion.fromUnsignedByte(length));
        buffer.put(nameBuffer);
        networkable.send(buffer);
    }

    @Override
    public void onConnected()
    {
        if (networkable.isOwner())
        {
            chat.setConsumer(this::sendMessage);
        }
        if (networkable.isClient())
        {
            services.get(PlayerNetwork.class).setModel(model);
        }
    }

    @Override
    public void onDisconnected()
    {
        if (networkable.isOwner())
        {
            chat.setConsumer(null);
        }
    }

    @Override
    public void onReceived(Packet packet)
    {
        chat.add(clients.get(packet.getClientSourceId()) + Constant.DOUBLE_DOT + packet.readString());
    }
}
