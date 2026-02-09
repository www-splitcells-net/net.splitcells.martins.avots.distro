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
import net.splitcells.dem.environment.Cell;
import net.splitcells.dem.environment.Environment;
import net.splitcells.dem.environment.resource.Console;
import net.splitcells.dem.resource.communication.log.Logs;
import net.splitcells.dem.resource.communication.log.MessageFilter;
import net.splitcells.dem.resource.profiling.PyroscopeService;
import net.splitcells.gel.ext.GelExtCell;
import net.splitcells.network.distro.DistroCell;
import net.splitcells.network.distro.java.acme.AcmeServerUri;
import net.splitcells.network.distro.java.acme.PublicKeyCryptoConfig;
import net.splitcells.network.system.SystemCell;
import net.splitcells.website.server.RedirectServer;
import net.splitcells.website.server.ServerConfig;
import net.splitcells.website.server.WebsiteServerCell;
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

import static net.splitcells.dem.resource.communication.log.LogLevel.DEBUG;
import static net.splitcells.dem.resource.communication.log.ServerLogger.serverLog;
import static net.splitcells.gel.ui.editor.geal.EditorProcessorTest.TEST_OPTIMIZATION_GUI;
import static net.splitcells.network.distro.java.DistroCell.ensureSslCertificatePresence;
import static net.splitcells.network.distro.java.DistroCell.setGlobalUnixStateLogger;
import static net.splitcells.network.distro.java.acme.AcmeServerUri.PRODUCTION_ACME_SERVER;
import static net.splitcells.network.distro.java.acme.PublicKeyCryptoConfigurator.publicKeyCryptoConfig;
import static net.splitcells.network.distro.java.acme.SelfSignedPublicKeyCryptoConfigurator.selfSignedPublicKeyCryptoConfigurator;
import static net.splitcells.website.server.security.authentication.AuthenticatorImpl.authenticatorBasedOnFiles;
import static net.splitcells.website.server.security.authorization.AuthorizerBasedOnFiles.authorizerBasedOnFiles;

/**
 * <p>You can test this {@link Cell} locally with some preparations:</p>
 * <ol>
 *     <li>Add `127.0.1.1       live.splitcells.net` to `/etc/hosts`</li>
 *     <li>Download public crypto keys from live server and store it at
 *     `/home/splitcells/.local/state/net.splitcells.martins.avots.distro.livedistrocell/config/net/splitcells/network/distro/java/acme/publickeycryptoconfigurator`</li>
 * </ol>
 */
public class LiveDistroCell implements Cell {

    public static void main(String... args) {
        Dem.serve(LiveCryptoSetupCell.class);
        Dem.serve(LiveDistroCell.class);
    }

    protected static void configCryptoSetup(Environment env, boolean initViaCells) {
        final var publicKeyCryptoConfig = selfSignedPublicKeyCryptoConfigurator().selfSignedPublicKeyCryptoConfig();
        env.config().withConfigValue(PublicIdentityPemStore.class, Optional.of(publicKeyCryptoConfig.publicPem()))
                .withConfigValue(PrivateIdentityPemStore.class, Optional.of(publicKeyCryptoConfig.privatePem()))
                .withConfigValue(SslEnabled.class, true);
        baseConfig(env, initViaCells);
    }

    protected static void configForPublicServer(Environment env, boolean initViaCells) {
        env.config()
                .withConfigValue(PublicDomain.class, Optional.of("live.splitcells.net"))
                .withConfigValue(PublicContactEMailAddress.class, Optional.of("contacts@splitcells.net"))
                .withConfigValue(PublicIdentityPemStore.class, Optional.of(publicKeyCryptoConfig().publicPem()))
                .withConfigValue(PrivateIdentityPemStore.class, Optional.of(publicKeyCryptoConfig().privatePem()))
                .withConfigValue(SslEnabled.class, true)
                .withInitedOption(HtmlLiveTester.class)
                .withConfigValue(HtmlLiveTest.class, TEST_OPTIMIZATION_GUI)
                .withConfigValue(MessageFilter.class, logMessage -> true)
                .withConfigValue(InternalPublicPort.class, Optional.of(8443)) // This is required, because from inside the container, the port is not the public one, but the one in the mapping of the Dockerfile.
                .withConfigValue(PasswordAuthenticationEnabled.class, true)
                .withConfigValue(Authentication.class, authenticatorBasedOnFiles())
                .withConfigValue(Authorization.class, authorizerBasedOnFiles())
                .withInitedOption(PyroscopeService.class)
        ;
        baseConfig(env, initViaCells);
    }


    protected static void baseConfig(Environment env, boolean initViaCells) {
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
        if (!initViaCells) {
            WebsiteServerCell.configureNoneCellInit(env.config().configValue(ServerConfig.class));
            SystemCell.config(env.config().configValue(ServerConfig.class));
            net.splitcells.martins.avots.distro.DistroCell.baseConfig(env.config().configValue(ServerConfig.class));
        }
        net.splitcells.network.distro.java.DistroCell.config(env.config().configValue(ServerConfig.class));
        net.splitcells.martins.avots.distro.DistroCell.envConfig(env);
        ensureSslCertificatePresence(env);
    }

    @Override
    public String groupId() {
        return "net.splitcells";
    }

    @Override
    public String artifactId() {
        return "martins.avots.distro";
    }

    @Override
    public void accept(Environment env) {
        env.withCell(SystemCell.class);
        DistroCell.config(env.config().configValue(ServerConfig.class));
        net.splitcells.martins.avots.distro.DistroCell.baseConfig(env.config().configValue(ServerConfig.class));
        GelExtCell.configureForWebserver(env);
        configForPublicServer(env, true);
    }
}
