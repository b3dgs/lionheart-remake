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
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.audio.AudioVoidFormat;
import com.b3dgs.lionengine.editor.ObjectRepresentation;
import com.b3dgs.lionengine.editor.dialog.project.ProjectImportHandler;
import com.b3dgs.lionengine.editor.object.world.updater.ObjectSelectionListener;
import com.b3dgs.lionengine.editor.object.world.updater.WorldInteractionObject;
import com.b3dgs.lionengine.editor.project.Project;
import com.b3dgs.lionengine.editor.project.ProjectFactory;
import com.b3dgs.lionengine.editor.properties.PropertiesPart;
import com.b3dgs.lionengine.editor.utility.UtilPart;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.feature.CameraTracker;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeaturableConfig;
import com.b3dgs.lionengine.game.feature.MirrorableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Spawner;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.body.BodyModel;
import com.b3dgs.lionengine.game.feature.collidable.CollidableModel;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionengine.game.feature.tile.map.persister.MapTilePersister;
import com.b3dgs.lionengine.game.feature.tile.map.persister.MapTilePersisterListener;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionDelegate;
import com.b3dgs.lionengine.helper.EntityChecker;
import com.b3dgs.lionengine.helper.MapTileHelper;
import com.b3dgs.lionheart.CheckpointHandler;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.LoadNextStage;
import com.b3dgs.lionheart.MapTilePersisterOptimized;
import com.b3dgs.lionheart.MapTileWater;
import com.b3dgs.lionheart.MusicPlayer;
import com.b3dgs.lionheart.constant.Folder;
import com.b3dgs.lionheart.object.XmlSaver;
import com.b3dgs.lionheart.object.feature.Trackable;

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
        private final Services services = WorldModel.INSTANCE.getServices();

        /**
         * Constructor.
         */
        AppStartupCompleteEventHandler()
        {
            super();
        }

        private void handleExtensions()
        {
            services.get(WorldInteractionObject.class).addListener(new ObjectSelectionListener()
            {
                @Override
                public void notifyObjectSelected(Transformable object)
                {
                    final PropertiesPart part = UtilPart.getPart(PropertiesPart.ID, PropertiesPart.class);
                    part.setInput(part.getTree(), (Configurer) null);
                    // TODO handle configurer with stage
                }

                @Override
                public void notifyObjectsSelected(Collection<Transformable> objects)
                {
                    // Nothing to do
                }
            });
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

                AudioFactory.addFormat(new AudioVoidFormat(Arrays.asList("wav", "sc68")));
                services.create(CheckpointHandler.class);
                services.add(new MusicPlayer()
                {
                    @Override
                    public void stopMusic()
                    {
                    }

                    @Override
                    public void playMusic(Media media)
                    {
                    }
                });
                services.add((LoadNextStage) (next, tickDelay, spawn) ->
                {
                });
                services.add(new SourceResolutionDelegate(Constant.RESOLUTION));
                services.remove(services.get(Spawner.class));
                services.add((Spawner) (media, x, y) ->
                {
                    final Featurable featurable = WorldModel.INSTANCE.getFactory()
                                                                     .create(media, ObjectRepresentation.class);
                    final Setup setup = WorldModel.INSTANCE.getFactory().getSetup(media);
                    featurable.addFeature(new MirrorableModel(services, setup));
                    featurable.addFeature(new BodyModel(services, setup));
                    featurable.addFeature(new CollidableModel(services, setup));
                    featurable.addFeature(new StateHandler(services, setup));
                    featurable.addFeature(new EntityChecker());
                    FeaturableConfig.getFeatures(project.getLoader().getClassLoader(), services, setup)
                                    .stream()
                                    .filter(f -> f instanceof XmlSaver)
                                    .forEach(featurable::addFeature);
                    featurable.getFeature(Transformable.class).teleport(x, y);
                    WorldModel.INSTANCE.getHandler().add(featurable);
                    return featurable;
                });

                services.create(CameraTracker.class);
                services.create(MapTileWater.class);
                services.add(WorldModel.INSTANCE.getFactory()
                                                .create(Medias.create(Folder.HERO, "valdyn", "Valdyn.xml"))
                                                .getFeature(Trackable.class));

                final MapTileHelper map = WorldModel.INSTANCE.getMap();
                map.addFeature(new MapTilePersisterOptimized(), true);
                map.getFeature(MapTilePersister.class).addListener(new MapTilePersisterListener()
                {
                    @Override
                    public void notifyMapLoadStart()
                    {
                        map.loadBefore(map.getMedia());
                    }

                    @Override
                    public void notifyMapLoaded()
                    {
                        map.loadAfter(map.getMedia());
                    }
                });
            }
            catch (final IOException exception)
            {
                Verbose.exception(exception);
            }
        }

        @Override
        public void handleEvent(Event event)
        {
            handleExtensions();
            checkProjectImport();
        }
    }
}
