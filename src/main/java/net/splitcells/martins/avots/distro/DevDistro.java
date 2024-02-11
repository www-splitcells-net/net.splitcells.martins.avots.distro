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
import net.splitcells.dem.environment.Environment;
import net.splitcells.gel.GelCoreFileSystem;
import net.splitcells.gel.doc.GelDocFileSystem;
import net.splitcells.gel.ext.GelExtFileSystem;
import net.splitcells.gel.ui.GelUiFileSystem;
import net.splitcells.network.NetworkFileSystem;
import net.splitcells.network.blog.NetworkBlogFileSystem;
import net.splitcells.network.community.NetworkCommunityFileSystem;
import net.splitcells.network.log.NetworkLogFileSystem;
import net.splitcells.network.media.NetworkMediaFileSystem;
import net.splitcells.network.worker.via.java.NetworkWorkerFileSystem;
import net.splitcells.os.state.interfaces.OsiFileSystem;
import net.splitcells.os.state.interfaces.lib.OsiLibFileSystem;
import net.splitcells.project.files.standard.ProjectStandardFileSystem;
import net.splitcells.system.SystemsFileSystem;
import net.splitcells.website.WebsiteServerFileSystem;
import net.splitcells.website.binaries.BinaryFileSystem;
import net.splitcells.website.content.defaults.WebsiteContentDefaultsFileSystem;

import java.nio.file.Path;
import java.nio.file.Paths;

import static net.splitcells.dem.resource.FileSystemUnion.fileSystemsUnion;
import static net.splitcells.dem.resource.FileSystems.fileSystemOnLocalHost;

public class DevDistro {

    private static final Path PUBLIC_REPOS = Paths.get(System.getProperty("user.home")).resolve(
            "Documents/projects/net.splitcells.martins.avots.support.system/public/");
    private static final Path PUBLIC_ROOT_PROJECT_REPO = PUBLIC_REPOS.resolve("net.splitcells.network");
    private static final Path PUBLIC_ROOT_SUB_PROJECTS = PUBLIC_ROOT_PROJECT_REPO.resolve("projects");

    public static void main(String... args) {
        Dem.process(() -> {
            try (final var liveService = Distro.liveService()) {
                liveService.start();
                Dem.waitIndefinitely();
            }
        }, env -> {
            Distro.envConfig(env);
            useLocalFileSystem(env);
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
                .withConfigValue(NetworkBlogFileSystem.class
                        , fileSystemOnLocalHost(PUBLIC_ROOT_SUB_PROJECTS.resolve("net.splitcells.network.blog")))
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
                .withConfigValue(ProjectStandardFileSystem.class
                        , fileSystemOnLocalHost(PUBLIC_ROOT_SUB_PROJECTS.resolve("net.splitcells.project.files.standard")))
        ;
    }
}
