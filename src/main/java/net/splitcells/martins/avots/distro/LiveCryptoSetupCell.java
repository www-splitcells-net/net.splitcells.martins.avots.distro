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
import net.splitcells.network.system.SystemCell;
import net.splitcells.website.server.security.encryption.PrivateIdentityPemStore;
import net.splitcells.website.server.security.encryption.PublicIdentityPemStore;
import net.splitcells.website.server.security.encryption.SslEnabled;

import java.util.Optional;

import static net.splitcells.dem.utils.reflection.ClassesRelated.simplifiedName;
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

    @Override public void accept(Environment env) {
        env.withCell(DistroCell.class);
        new LiveDistroCell().configCryptoSetup(env, true);
    }

    @Override public void run() {
        publicKeyCryptoConfig();
    }
}
