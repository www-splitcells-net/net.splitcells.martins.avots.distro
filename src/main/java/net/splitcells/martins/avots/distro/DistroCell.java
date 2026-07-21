/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
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
import net.splitcells.website.server.config.PublicContactEMailAddress;
import net.splitcells.website.server.config.PublicDomain;

import java.util.Optional;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.utils.reflection.ClassesRelated.resourceOfClass;
import static net.splitcells.website.server.ProjectConfig.projectConfig;

public class DistroCell implements Cell {
    private static final Optional<String> DETAILED_XSL_MENU = Optional.of(
            resourceOfClass(DistroCell.class, "detailed-menu.xsl"));
    private static final Optional<String> WINDOW_MENU_XSL = Optional.of(
            resourceOfClass(DistroCell.class, "window-menu.xsl"));

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
        env.withCell(net.splitcells.network.distro.DistroCell.class);
        configValue(ServerConfig.class)
                .withDetailedXslMenu(DETAILED_XSL_MENU)
                .withXslWindowMenu(WINDOW_MENU_XSL)
                .withAdditionalProject(projectConfig("/", configValue(DistroFileSystem.class)))
                .withAdditionalProject(projectConfig("/", configValue(SymbiosisFileSystem.class)));
        env.config()
                .withConfigValue(PublicDomain.class, Optional.of("live.splitcells.net"))
                .withConfigValue(PublicContactEMailAddress.class, Optional.of("contacts@splitcells.net"))
                .withConfigValue(NetworkWorkerLogFileSystem.class, env.config().configValue(NetworkLogFileSystem.class))
                .withInitedOption(HostUtilizationRecordService.class)
                .withInitedOption(ServerService.class);
        env.withCell(net.splitcells.network.distro.DistroCell.class);
    }
}
