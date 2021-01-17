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
package com.b3dgs.lionheart.editor;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.audio.AudioVoidFormat;
import com.b3dgs.lionengine.editor.dialog.project.ProjectImportHandler;
import com.b3dgs.lionengine.editor.project.Project;
import com.b3dgs.lionengine.editor.project.ProjectFactory;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.game.feature.CameraTracker;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionheart.Constant;

/**
 * Configure the editor with the right name.
 */
public class ApplicationConfiguration
{
    /** Import project argument. */
    private static final String ARG_IMPORT = "-import";

    /** Application reference. */
    @Inject private MApplication application;

    /**
     * Constructor.
     */
    public ApplicationConfiguration()
    {
        super();
    }

    /**
     * Execute the injection.
     * 
     * @param eventBroker The event broker service.
     */
    @PostConstruct
    public void execute(IEventBroker eventBroker)
    {
        final MWindow existingWindow = application.getChildren().get(0);
        existingWindow.setLabel(Activator.PLUGIN_NAME);
        existingWindow.setIconURI("platform:/plugin/" + Activator.PLUGIN_ID + "/icons/icon_256_8.bmp");
        eventBroker.subscribe(UIEvents.UILifeCycle.APP_STARTUP_COMPLETE, new AppStartupCompleteEventHandler());
    }

    /**
     * Handler called on startup complete.
     */
    private class AppStartupCompleteEventHandler implements EventHandler
    {
        /**
         * Constructor.
         */
        AppStartupCompleteEventHandler()
        {
            super();
        }

        /**
         * Check if there is a project to import.
         */
        private void checkProjectImport()
        {
            final String[] args = Platform.getApplicationArgs();
            for (int i = 0; i < args.length; i++)
            {
                if (ApplicationConfiguration.ARG_IMPORT.equals(args[i]))
                {
                    i++;
                    if (i < args.length)
                    {
                        importProject(args[i]);

                        WorldModel.INSTANCE.getServices().create(CameraTracker.class);
                        WorldModel.INSTANCE.getServices().add(new SourceResolutionProvider()
                        {
                            @Override
                            public int getWidth()
                            {
                                return Constant.NATIVE_RESOLUTION.getWidth();
                            }

                            @Override
                            public int getHeight()
                            {
                                return Constant.NATIVE_RESOLUTION.getHeight();
                            }

                            @Override
                            public int getRate()
                            {
                                return 60;
                            }
                        });
                        AudioFactory.addFormat(new AudioVoidFormat(Arrays.asList("wav", "sc68")));

                        // final MapTileHelper map = WorldModel.INSTANCE.getMap();
                        // map.create(Medias.create("levels", "swamp", "level1-1.png"));
                        // map.getFeature(MapTileGroup.class).loadGroups(Medias.create("levels", "swamp",
                        // "groups.xml"));
                        // map.getFeature(MapTileCollision.class)
                        // .loadCollisions(Medias.create("levels", "swamp", "formulas.xml"),
                        // Medias.create("levels", "swamp", "collisions.xml"));
                        // UtilPart.getPart(WorldPart.ID, WorldPart.class).update();
                    }
                }
            }
        }

        /**
         * Import a project from a path.
         * 
         * @param projectPath The project path.
         */
        private void importProject(String projectPath)
        {
            final File path = new File(projectPath);
            try
            {
                final Project project = ProjectFactory.create(path.getCanonicalFile());
                ProjectImportHandler.importProject(project);
            }
            catch (final IOException exception)
            {
                Verbose.exception(exception);
            }
        }

        @Override
        public void handleEvent(Event event)
        {
            checkProjectImport();
        }
    }
}
