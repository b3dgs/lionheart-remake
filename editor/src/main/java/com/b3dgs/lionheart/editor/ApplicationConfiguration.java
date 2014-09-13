/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionheart.editor;

import java.io.File;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.b3dgs.lionengine.editor.project.ImportProjectHandler;
import com.b3dgs.lionengine.editor.project.Project;

/**
 * Configure the editor with the right name.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class ApplicationConfiguration
{
    /** Application reference. */
    @Inject
    MApplication application;
    /** Part service reference. */
    @Inject
    EPartService partService;

    /**
     * Execute the injection.
     * 
     * @param eventBroker The event broker service.
     */
    @PostConstruct
    public void execute(IEventBroker eventBroker)
    {
        final MWindow existingWindow = application.getChildren().get(0);
        existingWindow.setLabel("Lionheart Remake Editor");

        eventBroker.subscribe(UIEvents.UILifeCycle.APP_STARTUP_COMPLETE, new AppStartupCompleteEventHandler());
    }

    /**
     * Handler called on startup complete.
     */
    private class AppStartupCompleteEventHandler
            implements EventHandler
    {
        /**
         * Constructor.
         */
        public AppStartupCompleteEventHandler()
        {
            // Nothing to do
        }

        @Override
        public void handleEvent(Event event)
        {
            final String[] args = Platform.getApplicationArgs();
            for (int i = 0; i < args.length; i++)
            {
                if (args[i].equals("-import"))
                {
                    i++;
                    if (i < args.length)
                    {
                        final File path = new File(args[i]);
                        if (path.isDirectory())
                        {
                            final Project project = Project.create(path);
                            ImportProjectHandler.importProject(project, partService);
                        }
                    }
                }
            }
        }
    }
}
