/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.martins.avots.distro;

import net.splitcells.dem.Dem;
import net.splitcells.dem.environment.Cell;
import net.splitcells.dem.environment.Environment;
import net.splitcells.network.distro.java.Slf4jCell;
import net.splitcells.network.distro.java.acme.AcmeServerUri;
import net.splitcells.network.distro.java.acme.CurrentAcmeAuthorization;
import net.splitcells.network.system.SystemCell;
import net.splitcells.website.server.ServerConfig;
import net.splitcells.website.server.projects.extension.ProjectsRendererExtensions;
import net.splitcells.website.server.security.authentication.Authentication;
import net.splitcells.website.server.security.encryption.PrivateIdentityPemStore;
import net.splitcells.website.server.security.encryption.PublicIdentityPemStore;
import net.splitcells.website.server.security.encryption.SslEnabled;

import java.util.Optional;

import static net.splitcells.dem.utils.reflection.ClassesRelated.simplifiedName;
import static net.splitcells.network.distro.java.acme.AcmeChallengeFile.acmeChallengeFile;
import static net.splitcells.network.distro.java.acme.AcmeServerUri.PRODUCTION_ACME_SERVER;
import static net.splitcells.network.distro.java.acme.PublicKeyCryptoConfigurator.publicKeyCryptoConfig;
import static net.splitcells.network.distro.java.acme.SelfSignedPublicKeyCryptoConfigurator.selfSignedPublicKeyCryptoConfigurator;

public class LiveCryptoSetupCell implements Cell {

    @Override public String programName() {
        return simplifiedName(LiveDistroCell.class);
    }

    @Override public String groupId() {
        return "net.splitcells";
    }

    @Override public String artifactId() {
        return "martins.avots.distro";
    }

    /**
     * No access to the user data is possible, as no {@link Authentication} is configured.
     * This needs to be the case, because otherwise users would do an authentication to the server unencrypted via HTTP,
     * which would make their username, password and data visible to others.
     * 
     * @param env The configuration that bootstraps this {@link Cell}.
     */
    @Override public void accept(Environment env) {
        env
                .withCell(net.splitcells.martins.avots.distro.DistroCell.class)
                .withCell(Slf4jCell.class);
        env.config()
                .withConfigValue(SslEnabled.class, false)
                .withConfigValue(AcmeServerUri.class, PRODUCTION_ACME_SERVER)
                .withInitedOption(CurrentAcmeAuthorization.class)
                .configValue(ProjectsRendererExtensions.class)
                .withAppended(acmeChallengeFile());
        env.config().configValue(ServerConfig.class).withOpenPort(8080);
    }

    @Override public void run() {
        publicKeyCryptoConfig();
    }
}
