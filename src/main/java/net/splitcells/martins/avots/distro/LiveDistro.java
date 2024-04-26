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
import net.splitcells.dem.environment.resource.Console;
import net.splitcells.dem.resource.communication.log.Logs;
import net.splitcells.dem.resource.communication.log.MessageFilter;

import static net.splitcells.dem.Dem.sleepAtLeast;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.resource.communication.log.ServerLog.serverLog;
import static net.splitcells.network.distro.java.Distro.ensureSslCertificatePresence;
import static net.splitcells.network.distro.java.Distro.setGlobalUnixStateLogger;
import static net.splitcells.network.distro.java.acme.Certificate.certificate;

public class LiveDistro {
    public static void main(String... args) {
        Dem.process(() -> {
            try (final var liveService = Distro.liveService()) {
                liveService.start();
                // TODO Is this sleep really needed?
                sleepAtLeast(3000l);
                try {
                    certificate("live.splitcells.net", "contacts@splitcells.net");
                } catch (Throwable t) {
                    Logs.logs().appendWarning(perspective("ACME experiment failed."), t);
                }
                Dem.waitIndefinitely();
            }
        }, env -> {
            setGlobalUnixStateLogger(env);
            env.config().withConfigValue(Logs.class, serverLog(env.config().configValue(Console.class)
                    , env.config().configValue(MessageFilter.class)));
            net.splitcells.network.distro.Distro.configurator(env);
            Distro.envConfig(env);
            ensureSslCertificatePresence(env);
        });
    }
}
