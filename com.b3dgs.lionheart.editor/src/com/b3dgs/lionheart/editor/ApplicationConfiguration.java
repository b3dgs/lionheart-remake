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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
import com.b3dgs.lionengine.editor.ObjectRepresentation;
import com.b3dgs.lionengine.editor.dialog.project.ProjectImportHandler;
import com.b3dgs.lionengine.editor.object.world.updater.WorldInteractionObject;
import com.b3dgs.lionengine.editor.project.Project;
import com.b3dgs.lionengine.editor.project.ProjectFactory;
import com.b3dgs.lionengine.editor.utility.UtilPart;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.editor.world.view.WorldPart;
import com.b3dgs.lionengine.game.Feature;
import com.b3dgs.lionengine.game.feature.AnimatableModel;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeaturableConfig;
import com.b3dgs.lionengine.game.feature.MirrorableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Spawner;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.body.BodyModel;
import com.b3dgs.lionengine.game.feature.collidable.CollidableModel;
import com.b3dgs.lionengine.game.feature.launchable.LaunchableModel;
import com.b3dgs.lionengine.game.feature.launchable.LauncherModel;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableModel;
import com.b3dgs.lionengine.game.feature.tile.map.persister.MapTilePersister;
import com.b3dgs.lionengine.game.feature.tile.map.persister.MapTilePersisterListener;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionDelegate;
import com.b3dgs.lionengine.helper.EntityChecker;
import com.b3dgs.lionengine.helper.MapTileHelper;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.MapTilePersisterOptimized;
import com.b3dgs.lionheart.Settings;
import com.b3dgs.lionheart.editor.checkpoint.Checkpoints;
import com.b3dgs.lionheart.editor.object.properties.PropertiesFeature;
import com.b3dgs.lionheart.editor.object.properties.dragon1.Dragon1Part;
import com.b3dgs.lionheart.editor.object.properties.geyzer.GeyzerPart;
import com.b3dgs.lionheart.editor.object.properties.hotfireball.HotFireBallPart;
import com.b3dgs.lionheart.editor.object.properties.jumper.JumperPart;
import com.b3dgs.lionheart.editor.object.properties.laserairship.LaserAirshipPart;
import com.b3dgs.lionheart.editor.object.properties.model.ModelPart;
import com.b3dgs.lionheart.editor.object.properties.patrol.PatrolPart;
import com.b3dgs.lionheart.editor.object.properties.rotating.RotatingPart;
import com.b3dgs.lionheart.editor.object.properties.sheet.SheetPart;
import com.b3dgs.lionheart.editor.object.properties.shooter.ShooterPart;
import com.b3dgs.lionheart.editor.object.properties.spider.SpiderPart;
import com.b3dgs.lionheart.editor.object.properties.spike.SpikePart;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.XmlSaver;
import com.b3dgs.lionheart.object.feature.Dragon1;
import com.b3dgs.lionheart.object.feature.Geyzer;
import com.b3dgs.lionheart.object.feature.HotFireBall;
import com.b3dgs.lionheart.object.feature.Jumper;
import com.b3dgs.lionheart.object.feature.LaserAirship;
import com.b3dgs.lionheart.object.feature.Patrols;
import com.b3dgs.lionheart.object.feature.Rotating;
import com.b3dgs.lionheart.object.feature.Sheet;
import com.b3dgs.lionheart.object.feature.Shooter;
import com.b3dgs.lionheart.object.feature.Spider;
import com.b3dgs.lionheart.object.feature.Spike;
import com.b3dgs.lionheart.object.feature.Stats;

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
        private final Map<Class<? extends Feature>, PropertiesFeature> properties = new HashMap<>();

        /**
         * Constructor.
         */
        AppStartupCompleteEventHandler()
        {
            super();

            Settings.setEditor(true);

            AudioFactory.addFormat(new AudioVoidFormat(Arrays.asList("wav", "sc68")));
            services.add(new Checkpoints());
            services.add(new SourceResolutionDelegate(Constant.RESOLUTION));

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

        private void handleExtensions()
        {
            properties.put(EntityModel.class, UtilPart.getPart(ModelPart.ID, ModelPart.class));
            properties.put(Patrols.class, UtilPart.getPart(PatrolPart.ID, PatrolPart.class));
            properties.put(Geyzer.class, UtilPart.getPart(GeyzerPart.ID, GeyzerPart.class));
            properties.put(HotFireBall.class, UtilPart.getPart(HotFireBallPart.ID, HotFireBallPart.class));
            properties.put(Spike.class, UtilPart.getPart(SpikePart.ID, SpikePart.class));
            properties.put(Spider.class, UtilPart.getPart(SpiderPart.ID, SpiderPart.class));
            properties.put(Shooter.class, UtilPart.getPart(ShooterPart.ID, ShooterPart.class));
            properties.put(LaserAirship.class, UtilPart.getPart(LaserAirshipPart.ID, LaserAirshipPart.class));
            properties.put(Sheet.class, UtilPart.getPart(SheetPart.ID, SheetPart.class));
            properties.put(Rotating.class, UtilPart.getPart(RotatingPart.ID, RotatingPart.class));
            properties.put(Dragon1.class, UtilPart.getPart(Dragon1Part.ID, Dragon1Part.class));
            properties.put(Jumper.class, UtilPart.getPart(JumperPart.ID, JumperPart.class));

            services.get(WorldInteractionObject.class).addListener(this::loadProperties);
        }

        private void loadProperties(Transformable featurable)
        {
            UtilPart.getMPart(ModelPart.ID).setVisible(false);
            UtilPart.getMPart(PatrolPart.ID).setVisible(false);
            UtilPart.getMPart(GeyzerPart.ID).setVisible(false);
            UtilPart.getMPart(HotFireBallPart.ID).setVisible(false);
            UtilPart.getMPart(SpikePart.ID).setVisible(false);
            UtilPart.getMPart(SpiderPart.ID).setVisible(false);
            UtilPart.getMPart(ShooterPart.ID).setVisible(false);
            UtilPart.getMPart(LaserAirshipPart.ID).setVisible(false);
            UtilPart.getMPart(SheetPart.ID).setVisible(false);
            UtilPart.getMPart(RotatingPart.ID).setVisible(false);
            UtilPart.getMPart(Dragon1Part.ID).setVisible(false);
            UtilPart.getMPart(JumperPart.ID).setVisible(false);

            featurable.getFeatures().forEach(AppStartupCompleteEventHandler.this::loadProperty);
            UtilPart.getPart(WorldPart.ID, WorldPart.class).focus();
        }

        private void loadProperty(Feature feature)
        {
            if (feature instanceof XmlSaver)
            {
                Optional.ofNullable(properties.get(feature.getClass())).ifPresent(p -> p.load(feature));
            }
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

                services.remove(services.get(Spawner.class));
                services.add((Spawner) (media, x, y) ->
                {
                    final Featurable featurable = WorldModel.INSTANCE.getFactory()
                                                                     .create(media, ObjectRepresentation.class);
                    final Setup setup = WorldModel.INSTANCE.getFactory().getSetup(media);
                    featurable.addFeature(new MirrorableModel(services, setup));
                    featurable.addFeature(new BodyModel(services, setup));
                    featurable.addFeature(new CollidableModel(services, setup));
                    featurable.addFeature(new LauncherModel(services, setup));
                    featurable.addFeature(new LaunchableModel(services, setup));
                    featurable.addFeature(new TileCollidableModel(services, setup));
                    featurable.addFeature(new StateHandler(services, setup));
                    featurable.addFeature(new AnimatableModel(services, setup));
                    featurable.addFeature(new Stats(services, setup));
                    featurable.addFeature(new EntityChecker());
                    FeaturableConfig.getFeatures(project.getLoader().getClassLoader(), services, setup, XmlSaver.class)
                                    .forEach(featurable::addFeature);
                    featurable.getFeature(Transformable.class).teleport(x, y);
                    WorldModel.INSTANCE.getHandler().add(featurable);
                    return featurable;
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
