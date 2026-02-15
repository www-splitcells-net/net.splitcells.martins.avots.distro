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
import net.splitcells.cin.CinServiceInitTest;
import net.splitcells.cin.text.CinTextFileSystem;
import net.splitcells.dem.Dem;
import net.splitcells.dem.DemApiFileSystem;
import net.splitcells.dem.DemFileSystem;
import net.splitcells.dem.environment.Cell;
import net.splitcells.dem.environment.Environment;
import net.splitcells.dem.environment.config.framework.Configuration;
import net.splitcells.dem.resource.FileSystem;
import net.splitcells.dem.resource.FileSystemView;
import net.splitcells.gel.GelCoreFileSystem;
import net.splitcells.gel.data.table.TableModificationCounter;
import net.splitcells.gel.data.table.Tables;
import net.splitcells.gel.doc.GelDocFileSystem;
import net.splitcells.gel.editor.GelEditorFileSystem;
import net.splitcells.gel.ext.GelExtFileSystem;
import net.splitcells.gel.ui.GelUiFileSystem;
import net.splitcells.network.NetworkFileSystem;
import net.splitcells.network.community.NetworkCommunityFileSystem;
import net.splitcells.network.distro.NetworkDistroFileSystem;
import net.splitcells.network.hub.NetworkHubFileSystem;
import net.splitcells.network.log.NetworkLogFileSystem;
import net.splitcells.network.media.NetworkMediaFileSystem;
import net.splitcells.network.presentations.NetworkPresentationsFileSystem;
import net.splitcells.network.worker.via.java.NetworkWorkerFileSystem;
import net.splitcells.network.worker.via.java.NetworkWorkerLogFileSystem;
import net.splitcells.shell.OsiFileSystem;
import net.splitcells.shell.lib.OsiLibFileSystem;
import net.splitcells.project.ProjectFileSystem;
import net.splitcells.network.system.SystemsFileSystem;
import net.splitcells.symbiosis.SymbiosisFileSystem;
import net.splitcells.website.WebsiteServerFileSystem;
import net.splitcells.website.binaries.BinaryFileSystem;
import net.splitcells.website.content.defaults.WebsiteContentDefaultsFileSystem;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.ServerConfig;
import net.splitcells.website.server.config.PasswordAuthenticationEnabled;
import net.splitcells.website.server.security.authentication.Authentication;
import net.splitcells.website.server.security.authorization.Authorization;

import java.nio.file.Path;
import java.nio.file.Paths;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.resource.FileSystemUnion.fileSystemsUnion;
import static net.splitcells.dem.resource.FileSystems.fileSystemOnLocalHost;
import static net.splitcells.gel.data.view.View.MIRROR_NAME;
import static net.splitcells.website.server.processor.BinaryMessage.binaryMessage;
import static net.splitcells.website.server.project.renderer.ObjectsRenderer.registerObject;
import static net.splitcells.website.server.security.authentication.AuthenticatorImpl.authenticatorBasedOnFiles;
import static net.splitcells.website.server.security.authorization.AuthorizerBasedOnFiles.authorizerBasedOnFiles;

public class DevDistroCell implements Cell {

    /**
     * TODO Make this configurable.
     */
    private static final Path PUBLIC_REPOS = Paths.get(System.getProperty("user.home")).resolve(
            "Documents/projects/net.splitcells.martins.avots.support.system/public/");
    private static final Path PUBLIC_ROOT_PROJECT_REPO = PUBLIC_REPOS.resolve("net.splitcells.network");
    private static final Path PUBLIC_ROOT_SUB_PROJECTS = PUBLIC_ROOT_PROJECT_REPO.resolve("projects");

    public static void main(String... args) {
        System.setProperty(net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY_KEY, "true");
        Dem.serve(DevDistroCell.class);
    }

    public static void useLocalFileSystem(Environment env) {
        useLocalFileSystem(env.config());
    }

    private static FileSystemView publicSourceCodeFilesystem(String projectName) {
        return fileSystemsUnion(fileSystemOnLocalHost(PUBLIC_REPOS.resolve(projectName))
                , fileSystemOnLocalHost(PUBLIC_REPOS.resolve(projectName + "/target/classes/" + projectName + ".resources")));
    }

    private static FileSystemView publicSubSourceCodeFilesystem(String projectName) {
        return fileSystemsUnion(fileSystemOnLocalHost(PUBLIC_ROOT_SUB_PROJECTS.resolve(projectName))
                , fileSystemOnLocalHost(PUBLIC_ROOT_SUB_PROJECTS.resolve(projectName + "/target/classes/" + projectName + ".resources")));
    }

    public static void useLocalFileSystem(Configuration config) {
        config.withConfigValue(NetworkMediaFileSystem.class
                        , publicSourceCodeFilesystem("net.splitcells.network.media"))
                .withConfigValue(BinaryFileSystem.class
                        , publicSourceCodeFilesystem("net.splitcells.website.content.binaries"))
                .withConfigValue(NetworkWorkerLogFileSystem.class
                        , fileSystemOnLocalHost(PUBLIC_REPOS.resolve("net.splitcells.network.log")))
                .withConfigValue(NetworkLogFileSystem.class
                        , fileSystemOnLocalHost(PUBLIC_REPOS.resolve("net.splitcells.network.log")))
                .withConfigValue(CinFileSystem.class, publicSubSourceCodeFilesystem("net.splitcells.cin"))
                .withConfigValue(DemFileSystem.class, publicSubSourceCodeFilesystem("net.splitcells.dem"))
                .withConfigValue(DemApiFileSystem.class, publicSubSourceCodeFilesystem("net.splitcells.dem.api"))
                .withConfigValue(GelCoreFileSystem.class, publicSubSourceCodeFilesystem("net.splitcells.gel.core"))
                .withConfigValue(GelDocFileSystem.class, publicSubSourceCodeFilesystem("net.splitcells.gel.doc"))
                .withConfigValue(GelEditorFileSystem.class, publicSubSourceCodeFilesystem("net.splitcells.gel.editor"))
                .withConfigValue(GelUiFileSystem.class, publicSubSourceCodeFilesystem("net.splitcells.gel.ui"))
                .withConfigValue(GelExtFileSystem.class, publicSubSourceCodeFilesystem("net.splitcells.gel.ext"))
                .withConfigValue(NetworkFileSystem.class, publicSubSourceCodeFilesystem("net.splitcells.network"))
                .withConfigValue(NetworkWorkerFileSystem.class, publicSubSourceCodeFilesystem("net.splitcells.network.worker.via.java"))
                .withConfigValue(OsiFileSystem.class, publicSubSourceCodeFilesystem("net.splitcells.shell"))
                .withConfigValue(OsiLibFileSystem.class, publicSubSourceCodeFilesystem("net.splitcells.shell.lib"))
                .withConfigValue(SystemsFileSystem.class, publicSubSourceCodeFilesystem("net.splitcells.network.system"))
                .withConfigValue(WebsiteServerFileSystem.class, publicSubSourceCodeFilesystem("net.splitcells.website.server"))
                .withConfigValue(WebsiteContentDefaultsFileSystem.class, publicSubSourceCodeFilesystem("net.splitcells.website.content.default"))
                .withConfigValue(NetworkCommunityFileSystem.class, publicSourceCodeFilesystem("net.splitcells.network.community"))
                .withConfigValue(DistroFileSystem.class, publicSourceCodeFilesystem("net.splitcells.martins.avots.distro"))
                .withConfigValue(NetworkDistroFileSystem.class
                        , fileSystemOnLocalHost(PUBLIC_REPOS.resolve("net.splitcells.network.distro/projects/net.splitcells.network.distro")))
                .withConfigValue(ProjectFileSystem.class
                        , publicSubSourceCodeFilesystem("net.splitcells.project"))
                .withConfigValue(NetworkPresentationsFileSystem.class, publicSourceCodeFilesystem("net.splitcells.network.presentations"))
                .withConfigValue(CinTextFileSystem.class, publicSourceCodeFilesystem("net.splitcells.cin.text"))
                .withConfigValue(SymbiosisFileSystem.class, publicSourceCodeFilesystem("net.splitcells.symbiosis"))
                .withConfigValue(NetworkHubFileSystem.class, fileSystemsUnion(fileSystemOnLocalHost(PUBLIC_REPOS.resolve("net.splitcells.network.hub/projects/net.splitcells.network.hub"))
                        , fileSystemOnLocalHost(PUBLIC_REPOS.resolve("net.splitcells.network.hub/projects/net.splitcells.network.hub/target/classes/net.splitcells.network.hub.resources"))))
        ;
    }

    private static void config(Environment env) {
        {
            env.withConfig(DistroCell::envConfig)
                    .withConfig(DevDistroCell::useLocalFileSystem)
                    .config()
                    .withConfigValue(CinServiceInitTest.class, true)
                    // .withInitedOption(CinService.class) TODO Enable this when the ticket #51 Bootstrap game based on optimization networks is being worked on again.
                    .withConfigValue(PasswordAuthenticationEnabled.class, true)
                    .withConfigValue(Authentication.class, authenticatorBasedOnFiles())
                    .withConfigValue(Authorization.class, authorizerBasedOnFiles())
                    .withInitedOption(TableModificationCounter.class)
            ;
            env.config().configValue(ServerConfig.class).withIsServerForGeneralPublic(false);
            // TODO Move this connector to the core code.
            env.config().configValue(Tables.class).withConnector(table -> {
                if (!table.name().equals(MIRROR_NAME)) {
                    registerObject(table.discoverableRenderer());
                }
            });
            /* TODO The ObjectsRenderers' errors cause the server to fail
               to process editor requests in multithreaded environments.
               ObjectsRenderers also cause errors in single-threaded environments,
               but they do cause the server to fail to process editor requests.
            env.config().configValue(Databases.class)
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
             */
        }
    }

    @Override
    public String groupId() {
        return "net.splitcells.martins.avots";
    }

    @Override
    public String artifactId() {
        return "distro";
    }

    @Override
    public void accept(Environment env) {
        config(env);
        env.withCell(DistroCell.class);
    }
}
