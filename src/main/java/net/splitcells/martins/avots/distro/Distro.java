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

import net.splitcells.dem.environment.Environment;
import net.splitcells.dem.environment.resource.HostUtilizationRecordService;
import net.splitcells.dem.environment.resource.Service;
import net.splitcells.network.system.SystemCell;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.Server;

import java.util.Optional;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.dem.utils.reflection.ClassesRelated.resourceOfClass;
import static net.splitcells.website.server.ProgramConfig.programConfig;
import static net.splitcells.website.server.ProjectConfig.projectConfig;

public class Distro {

    private static final Optional<String> DETAILED_XSL_MENU = Optional.of(
            resourceOfClass(Distro.class, "detailed-menu.xsl"));
    private static final Optional<String> WINDOW_MENU_XSL = Optional.of(
            resourceOfClass(Distro.class, "window-menu.xsl"));

    private Distro() {
        throw constructorIllegal();
    }

    public static Service liveService() {
        final var config = liveConfig(baseConfig());
        return Server.serveToHttpAt(() -> {
            final var projectsRenderer = SystemCell.projectsRenderer(config);
            projectsRenderer.build();
            return projectsRenderer;
        }, config);
    }

    public static Service websiteService() {
        return SystemCell.projectsRenderer(websiteConfig(baseConfig())).httpServer();
    }

    private static Service projectsRenderer(Config config) {
        return SystemCell.projectsRenderer(config).httpServer();
    }

    public static void envConfig(Environment env) {
        env.config().withInitedOption(HostUtilizationRecordService.class);
    }

    public static Config baseConfig(Config arg) {
        arg.withDetailedXslMenu(DETAILED_XSL_MENU)
                .withXslWindowMenu(WINDOW_MENU_XSL)
                .withAdditionalProject(projectConfig("/", configValue(DistroFileSystem.class)));
        return arg;
    }

    @Deprecated
    private static Config baseConfig() {
        final var config = net.splitcells.network.distro.Distro.config()
                .withDetailedXslMenu(DETAILED_XSL_MENU)
                .withXslWindowMenu(WINDOW_MENU_XSL)
                .withAdditionalProject(projectConfig("/", configValue(DistroFileSystem.class)));
        return config;
    }

    private static Config websiteConfig(Config config) {
        config.clearAdditionalProgramConfigs()
                .withAdditionalProgramConfig(programConfig("About This Site"
                        , "/net/splitcells/martins/avots/website/info/about-this-site")
                        .withLogoPath(Optional.of("net/splitcells/website/images/white.background.blog.discovery.0.jpg"))
                        .withDescription(Optional.of("History And Purpose Of This Site")))
                .withAdditionalProgramConfig(programConfig("Technical Newsfeed"
                        , "/net/splitcells/CHANGELOG.global")
                        .withLogoPath(Optional.of("net/splitcells/website/images/average.source.code.image.generator.filling.via.horizontal.100.percent.jpg"))
                        .withDescription(Optional.of("Site's Technical Global Changelog")))
                .withAdditionalProgramConfig(programConfig("Splitcells Network"
                        , "/net/splitcells/network/hub/README")
                        .withLogoPath(Optional.of("net/splitcells/website/images/community.2016.12.11.chrom.0.dina4.jpg"))
                        .withDescription(Optional.of("We provide an open source ecosystem centered around optimization and operations research.")))
                .withAdditionalProgramConfig(programConfig("Personal Projects"
                        , "/net/splitcells/martins/avots/website/projects/index")
                        .withLogoPath(Optional.of("net/splitcells/website/images/starting-to-learn-how-to-draw-a-face.jpg"))
                        .withDescription(Optional.of("Projects that I support or work on.")));
        return config;
    }

    /**
     * TODO Document why does the distinction between this and {@link #websiteConfig(Config)} exist.
     *
     * @param config
     * @return
     */
    private static Config liveConfig(Config config) {
        return config;
    }
}
