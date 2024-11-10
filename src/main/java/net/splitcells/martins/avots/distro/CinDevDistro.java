/*
 * Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
 * which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.martins.avots.distro;

import net.splitcells.cin.CinFileSystem;
import net.splitcells.dem.Dem;
import net.splitcells.dem.DemApiFileSystem;
import net.splitcells.dem.DemFileSystem;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.environment.Environment;
import net.splitcells.dem.resource.ContentType;
import net.splitcells.gel.GelCoreFileSystem;
import net.splitcells.gel.data.table.Tables;
import net.splitcells.gel.doc.GelDocFileSystem;
import net.splitcells.gel.ext.GelExtFileSystem;
import net.splitcells.gel.solution.Solutions;
import net.splitcells.gel.ui.GelUiFileSystem;
import net.splitcells.network.NetworkFileSystem;
import net.splitcells.network.community.NetworkCommunityFileSystem;
import net.splitcells.network.distro.NetworkDistroFileSystem;
import net.splitcells.network.log.NetworkLogFileSystem;
import net.splitcells.network.media.NetworkMediaFileSystem;
import net.splitcells.network.worker.via.java.NetworkWorkerFileSystem;
import net.splitcells.os.state.interfaces.OsiFileSystem;
import net.splitcells.os.state.interfaces.lib.OsiLibFileSystem;
import net.splitcells.project.ProjectFileSystem;
import net.splitcells.system.SystemsFileSystem;
import net.splitcells.website.WebsiteServerFileSystem;
import net.splitcells.website.binaries.BinaryFileSystem;
import net.splitcells.website.content.defaults.WebsiteContentDefaultsFileSystem;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.processor.BinaryMessage;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.project.renderer.DiscoverableMediaRenderer;
import net.splitcells.website.server.project.renderer.DiscoverableRenderer;
import net.splitcells.website.server.project.renderer.ObjectsMediaRenderer;
import net.splitcells.website.server.project.renderer.ObjectsRenderer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static net.splitcells.cin.deprecated.World.WORLD_HISTORY;
import static net.splitcells.cin.deprecated.World.addNextTime;
import static net.splitcells.cin.deprecated.World.allocateBlinker;
import static net.splitcells.cin.deprecated.World.allocateRestAsDead;
import static net.splitcells.cin.deprecated.World.initWorldHistory;
import static net.splitcells.cin.deprecated.World.worldHistory;
import static net.splitcells.cin.deprecated.World.worldOptimizer;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.resource.FileSystemUnion.fileSystemsUnion;
import static net.splitcells.dem.resource.FileSystems.fileSystemOnLocalHost;
import static net.splitcells.dem.resource.communication.log.LogLevel.INFO;
import static net.splitcells.dem.utils.Time.reportRuntime;
import static net.splitcells.gel.solution.optimization.primitive.OnlineLinearInitialization.onlineLinearInitialization;
import static net.splitcells.sep.Network.network;
import static net.splitcells.website.server.processor.BinaryMessage.binaryMessage;

public class CinDevDistro {

    private static final Path PUBLIC_REPOS = Paths.get(System.getProperty("user.home")).resolve(
            "Documents/projects/net.splitcells.martins.avots.support.system/public/");
    private static final Path PUBLIC_ROOT_PROJECT_REPO = PUBLIC_REPOS.resolve("net.splitcells.network");
    private static final Path PUBLIC_ROOT_SUB_PROJECTS = PUBLIC_ROOT_PROJECT_REPO.resolve("projects");

    public static void main(String... args) {
        System.setProperty(net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY_KEY, "false");
        Dem.process(() -> {
            if (true) {
                // TODO Make this a declarative service.
                final var network = network();
                final var currentWorldHistory = worldHistory(WORLD_HISTORY, list(), list());
                reportRuntime(() -> {
                    network.withNode(WORLD_HISTORY, currentWorldHistory);
                    initWorldHistory(currentWorldHistory);
                    allocateBlinker(currentWorldHistory);
                    currentWorldHistory.init();
                    allocateRestAsDead(currentWorldHistory);
                }, "Initialize world history.", INFO);
                reportRuntime(() -> {
                    network.withOptimization(WORLD_HISTORY, onlineLinearInitialization());
                    network.withOptimization(WORLD_HISTORY, worldOptimizer(network.node(WORLD_HISTORY)));
                }, "Initial world history optimization", INFO);
                reportRuntime(() -> {
                    network.withExecution(WORLD_HISTORY, wh -> {
                        addNextTime(wh);
                        wh.init();
                    });
                    network.withOptimization(WORLD_HISTORY, onlineLinearInitialization());
                    network.withOptimization(WORLD_HISTORY, worldOptimizer(network.node(WORLD_HISTORY)));
                }, "World history optimization", INFO);
            }
            try (final var liveService = Distro.liveService()) {
                liveService.start();
                Dem.waitIndefinitely();
            }
        }, env -> {
            Distro.envConfig(env);
            useLocalFileSystem(env);
            env.config().configValue(Tables.class)
                    .withConnector(database -> ObjectsRenderer.registerObject(new DiscoverableRenderer() {
                        @Override
                        public String render() {
                            return database.toHtmlTable().toHtmlString();
                        }

                        @Override
                        public Optional<String> title() {
                            return Optional.of(database.path().toString());
                        }

                        @Override
                        public List<String> path() {
                            return database.path();
                        }
                    }));
            env.config().configValue(Solutions.class)
                    .withConnector(solution -> ObjectsRenderer.registerObject(new DiscoverableRenderer() {
                        @Override
                        public String render() {
                            return solution.toHtmlTable().toHtmlString();
                        }

                        @Override
                        public Optional<String> title() {
                            return Optional.of(solution.path().toString());
                        }

                        @Override
                        public List<String> path() {
                            return solution.path();
                        }
                    }));
            env.config().configValue(Solutions.class)
                    .withConnector(solution -> ObjectsRenderer.registerObject(new DiscoverableRenderer() {
                        @Override
                        public String render() {
                            return solution.constraint().renderToHtml().toHtmlString();
                        }

                        @Override
                        public Optional<String> title() {
                            return Optional.of(solution.path().toString());
                        }

                        @Override
                        public List<String> path() {
                            return solution.path().withAppended("constraint");
                        }
                    }));
            env.config().configValue(Solutions.class)
                    .withConnector(solution -> ObjectsMediaRenderer.registerMediaObject(new DiscoverableMediaRenderer() {
                        @Override
                        public Optional<BinaryMessage> render(ProjectRenderer projectRenderer, Config config) {
                            return Optional.of(binaryMessage(solution.toCSV().getBytes(), ContentType.CSV.codeName()));
                        }

                        @Override
                        public List<String> path() {
                            final var path = solution.path().shallowCopy();
                            return path.withAppended(path.removeAt(path.size() - 1) + ".csv");
                        }
                    }));
        });
    }

    public static void useLocalFileSystem(Environment env) {
        env.config().withConfigValue(NetworkMediaFileSystem.class
                        , fileSystemOnLocalHost(PUBLIC_REPOS.resolve("net.splitcells.network.media")))
                .withConfigValue(BinaryFileSystem.class
                        , fileSystemOnLocalHost(PUBLIC_REPOS.resolve("net.splitcells.website.content.binaries")))
                .withConfigValue(NetworkLogFileSystem.class
                        , fileSystemOnLocalHost(PUBLIC_REPOS.resolve("net.splitcells.network.log")))
                .withConfigValue(CinFileSystem.class
                        , fileSystemOnLocalHost(PUBLIC_ROOT_SUB_PROJECTS.resolve("net.splitcells.cin")))
                .withConfigValue(DemFileSystem.class
                        , fileSystemOnLocalHost(PUBLIC_ROOT_SUB_PROJECTS.resolve("net.splitcells.dem")))
                .withConfigValue(DemApiFileSystem.class
                        , fileSystemOnLocalHost(PUBLIC_ROOT_SUB_PROJECTS.resolve("net.splitcells.dem.api")))
                .withConfigValue(GelCoreFileSystem.class
                        , fileSystemOnLocalHost(PUBLIC_ROOT_SUB_PROJECTS.resolve("net.splitcells.gel.core")))
                .withConfigValue(GelDocFileSystem.class
                        , fileSystemOnLocalHost(PUBLIC_ROOT_SUB_PROJECTS.resolve("net.splitcells.gel.doc")))
                .withConfigValue(GelUiFileSystem.class
                        , fileSystemOnLocalHost(PUBLIC_ROOT_SUB_PROJECTS.resolve("net.splitcells.gel.ui")))
                .withConfigValue(GelExtFileSystem.class
                        , fileSystemOnLocalHost(PUBLIC_ROOT_SUB_PROJECTS.resolve("net.splitcells.gel.ext")))
                .withConfigValue(NetworkFileSystem.class
                        , fileSystemsUnion(
                                fileSystemOnLocalHost(PUBLIC_ROOT_PROJECT_REPO)
                                , fileSystemOnLocalHost(PUBLIC_ROOT_SUB_PROJECTS.resolve("net.splitcells.network"))))
                .withConfigValue(NetworkWorkerFileSystem.class
                        , fileSystemOnLocalHost(PUBLIC_ROOT_SUB_PROJECTS.resolve("net.splitcells.network.worker.via.java")))
                .withConfigValue(OsiFileSystem.class
                        , fileSystemOnLocalHost(PUBLIC_ROOT_SUB_PROJECTS.resolve("net.splitcells.os.state.interface")))
                .withConfigValue(OsiLibFileSystem.class
                        , fileSystemOnLocalHost(PUBLIC_ROOT_SUB_PROJECTS.resolve("net.splitcells.os.state.interface.lib")))
                .withConfigValue(SystemsFileSystem.class
                        , fileSystemOnLocalHost(PUBLIC_ROOT_SUB_PROJECTS.resolve("net.splitcells.system")))
                .withConfigValue(WebsiteServerFileSystem.class
                        , fileSystemOnLocalHost(PUBLIC_ROOT_SUB_PROJECTS.resolve("net.splitcells.website.server")))
                .withConfigValue(WebsiteContentDefaultsFileSystem.class
                        , fileSystemOnLocalHost(PUBLIC_ROOT_SUB_PROJECTS.resolve("net.splitcells.website.content.default")))
                .withConfigValue(NetworkCommunityFileSystem.class
                        , fileSystemOnLocalHost(PUBLIC_REPOS.resolve("net.splitcells.network.community")))
                .withConfigValue(DistroFileSystem.class
                        , fileSystemOnLocalHost(PUBLIC_REPOS.resolve("net.splitcells.martins.avots.distro")))
                .withConfigValue(NetworkDistroFileSystem.class
                        , fileSystemOnLocalHost(PUBLIC_REPOS.resolve("net.splitcells.network.distro/projects/net.splitcells.network.distro")))
                .withConfigValue(ProjectFileSystem.class
                        , fileSystemOnLocalHost(PUBLIC_ROOT_SUB_PROJECTS.resolve("net.splitcells.project.files.standard")))
        ;
    }
}
