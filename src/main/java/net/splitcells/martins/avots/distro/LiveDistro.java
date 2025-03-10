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
import net.splitcells.network.distro.DistroCell;
import net.splitcells.network.distro.java.acme.AcmeServerUri;
import net.splitcells.network.distro.java.acme.PublicKeyCryptoConfig;
import net.splitcells.website.server.RedirectServer;
import net.splitcells.website.server.config.InternalPublicPort;
import net.splitcells.website.server.config.PasswordAuthenticationEnabled;
import net.splitcells.website.server.config.PublicContactEMailAddress;
import net.splitcells.website.server.config.PublicDomain;
import net.splitcells.website.server.security.authentication.Authentication;
import net.splitcells.website.server.security.authorization.Authorization;
import net.splitcells.website.server.security.encryption.PrivateIdentityPemStore;
import net.splitcells.website.server.security.encryption.PublicIdentityPemStore;
import net.splitcells.website.server.security.encryption.SslEnabled;
import net.splitcells.website.server.test.HtmlLiveTest;
import net.splitcells.website.server.test.HtmlLiveTester;

import java.util.Optional;

import static net.splitcells.dem.Dem.process;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.communication.log.LogLevel.DEBUG;
import static net.splitcells.dem.resource.communication.log.ServerLogger.serverLog;
import static net.splitcells.gel.ui.editor.nocode.NoCodeSolutionCalculatorTest.TEST_OPTIMIZATION_GUI;
import static net.splitcells.network.distro.java.Distro.ensureSslCertificatePresence;
import static net.splitcells.network.distro.java.Distro.setGlobalUnixStateLogger;
import static net.splitcells.network.distro.java.acme.AcmeServerUri.PRODUCTION_ACME_SERVER;
import static net.splitcells.network.distro.java.acme.PublicKeyCryptoConfigurator.publicKeyCryptoConfig;
import static net.splitcells.network.distro.java.acme.SelfSignedPublicKeyCryptoConfigurator.selfSignedPublicKeyCryptoConfigurator;
import static net.splitcells.website.server.security.authentication.AuthenticatorImpl.authenticatorBasedOnFiles;
import static net.splitcells.website.server.security.authorization.AuthorizerBasedOnFiles.authorizerBasedOnFiles;

public class LiveDistro {
    public static void main(String... args) {
        Dem.process(() -> {
            final PublicKeyCryptoConfig certificate;
            try (final var liveService = Distro.liveService()) {
                liveService.start();
                certificate = publicKeyCryptoConfig();
            }
            Dem.process(() -> {
                try (final var liveService = Distro.liveService()) {
                    liveService.start();
                    Dem.waitIndefinitely();
                }
            }, env -> {
                env.config()
                        .withConfigValue(PublicIdentityPemStore.class, Optional.of(certificate.publicPem()))
                        .withConfigValue(PrivateIdentityPemStore.class, Optional.of(certificate.privatePem()))
                        .withConfigValue(SslEnabled.class, true)
                        .withInitedOption(HtmlLiveTester.class)
                        .withConfigValue(HtmlLiveTest.class, TEST_OPTIMIZATION_GUI)
                        .withConfigValue(MessageFilter.class, logMessage -> true)
                        /* The default settings sometimes crash the container,
                         * because of OutOfMemory, although enough memory is present.
                         * Furthermore, if OutOfMemory does not appear, the browser still do not work.
                         * This seems to be a Playwright specific error.
                         *
                         * TODO Theory: start 1 Playwright instance first and execute its tests.
                         * After that, one can use as many Playwright instances as needed.
                         * The reason for that would be, the Playwright's initial setup has a race condition.
                         *
                         * TODO Remove this, when multiple live testers work at once on live server. -> This seems to be working.
                         *
                         * .withConfigValue(HtmlLiveTesterCount.class, 1)
                         */
                        .withConfigValue(InternalPublicPort.class, Optional.of(8443)) // This is required, because from inside the container, the port is not the public one, but the one in the mapping of the Dockerfile.
                        .withConfigValue(PasswordAuthenticationEnabled.class, true)
                        .withConfigValue(Authentication.class, authenticatorBasedOnFiles())
                        .withConfigValue(Authorization.class, authorizerBasedOnFiles())
                ;
                baseConfig(env);
            });
        }, env -> {
            final var publicKeyCryptoConfig = selfSignedPublicKeyCryptoConfigurator().selfSignedPublicKeyCryptoConfig();
            env.config().withConfigValue(PublicIdentityPemStore.class, Optional.of(publicKeyCryptoConfig.publicPem()))
                    .withConfigValue(PrivateIdentityPemStore.class, Optional.of(publicKeyCryptoConfig.privatePem()))
                    .withConfigValue(SslEnabled.class, true);
            baseConfig(env);
        });
    }

    private static void baseConfig(Environment env) {
        setGlobalUnixStateLogger(env);
        env.config()
                .withConfigValue(MessageFilter.class, logMessage -> logMessage.priority().greaterThanOrEqual(DEBUG))
                .withConfigValue(Logs.class, serverLog(env.config().configValue(Console.class)
                        , env.config().configValue(MessageFilter.class)))
                .withConfigValue(PublicDomain.class, Optional.of("live.splitcells.net"))
                .withConfigValue(PublicContactEMailAddress.class, Optional.of("contacts@splitcells.net"))
                .withConfigValue(AcmeServerUri.class, PRODUCTION_ACME_SERVER)
                .withInitedOption(RedirectServer.class);
        DistroCell.configurator(env);
        Distro.envConfig(env);
        ensureSslCertificatePresence(env);
    }
}
