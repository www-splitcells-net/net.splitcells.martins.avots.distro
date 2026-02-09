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

import net.splitcells.dem.environment.Cell;
import net.splitcells.dem.environment.Environment;
import net.splitcells.dem.environment.resource.HostUtilizationRecordService;
import net.splitcells.network.log.NetworkLogFileSystem;
import net.splitcells.network.worker.via.java.NetworkWorkerLogFileSystem;
import net.splitcells.symbiosis.SymbiosisFileSystem;
import net.splitcells.website.server.*;

import java.util.Optional;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.utils.reflection.ClassesRelated.resourceOfClass;
import static net.splitcells.website.server.ProjectConfig.projectConfig;

public class DistroCell implements Cell {
    private static final Optional<String> DETAILED_XSL_MENU = Optional.of(
            resourceOfClass(DistroCell.class, "detailed-menu.xsl"));
    private static final Optional<String> WINDOW_MENU_XSL = Optional.of(
            resourceOfClass(DistroCell.class, "window-menu.xsl"));

    public static void envConfig(Environment env) {
        env.config().withConfigValue(NetworkWorkerLogFileSystem.class, env.config().configValue(NetworkLogFileSystem.class));
        env.config().withInitedOption(HostUtilizationRecordService.class);
    }

    public Config config(Config arg) {
        arg.withDetailedXslMenu(DETAILED_XSL_MENU)
                .withXslWindowMenu(WINDOW_MENU_XSL)
                .withAdditionalProject(projectConfig("/", configValue(DistroFileSystem.class)));
        return arg;
    }

    private Config config() {
        final var config = net.splitcells.network.distro.DistroCell.config(configValue(ServerConfig.class))
                .withDetailedXslMenu(DETAILED_XSL_MENU)
                .withXslWindowMenu(WINDOW_MENU_XSL)
                .withAdditionalProject(projectConfig("/", configValue(DistroFileSystem.class)))
                .withAdditionalProject(projectConfig("/", configValue(SymbiosisFileSystem.class)));
        return config;
    }

    private static Config liveConfig(Config config) {
        return config;
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
        env.config()
                .withConfigValue(ServerConfig.class, liveConfig(config()))
                .withInitedOption(ServerService.class)
        ;
        env.withCell(net.splitcells.network.distro.DistroCell.class);
    }
}
