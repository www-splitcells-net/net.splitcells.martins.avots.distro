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

import net.splitcells.dem.Dem;
import net.splitcells.dem.environment.Environment;
import net.splitcells.dem.environment.resource.Console;
import net.splitcells.dem.resource.communication.log.Logs;
import net.splitcells.dem.resource.communication.log.MessageFilter;
import net.splitcells.network.distro.java.acme.AcmeServerUri;
import net.splitcells.website.server.config.PublicContactEMailAddress;
import net.splitcells.website.server.config.PublicDomain;
import net.splitcells.website.server.security.IdentityPemStore;
import net.splitcells.website.server.security.SslEnabled;

import java.util.Optional;

import static net.splitcells.dem.Dem.sleepAtLeast;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.resource.communication.log.ServerLog.serverLog;
import static net.splitcells.network.distro.java.Distro.ensureSslCertificatePresence;
import static net.splitcells.network.distro.java.Distro.setGlobalUnixStateLogger;
import static net.splitcells.network.distro.java.acme.AcmeServerUri.PRODUCTION_ACME_SERVER;
import static net.splitcells.network.distro.java.acme.Certificate.certificatePem;

public class LiveDistro {
    public static void main(String... args) {
        Dem.process(() -> {
            final byte[] certificate;
            try (final var liveService = Distro.liveService()) {
                liveService.start();
                certificate = certificatePem();
            }
            Dem.process(() -> {
                try (final var liveService = Distro.liveService()) {
                    Dem.waitIndefinitely();
                }
            }, env -> {
                baseConfig(env);
                env.config().withConfigValue(IdentityPemStore.class, Optional.of(certificate))
                        .withConfigValue(SslEnabled.class, true);
            });
        }, env -> baseConfig(env));
    }

    private static void baseConfig(Environment env) {
        setGlobalUnixStateLogger(env);
        env.config().withConfigValue(Logs.class, serverLog(env.config().configValue(Console.class)
                        , env.config().configValue(MessageFilter.class)))
                .withConfigValue(PublicDomain.class, Optional.of("live.splitcells.net"))
                .withConfigValue(PublicContactEMailAddress.class, Optional.of("contacts@splitcells.net"))
                .withConfigValue(AcmeServerUri.class, PRODUCTION_ACME_SERVER);
        net.splitcells.network.distro.Distro.configurator(env);
        Distro.envConfig(env);
        ensureSslCertificatePresence(env);
    }
}
