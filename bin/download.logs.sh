#!/usr/bin/env sh
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
[ ! -d "bin" ] && cd ..
mkdir -p target/logs
ssh.download.file \
    --remote-file-address martins-avots@live.splitcells.net:.local/state/net.splitcells.martins.avots.distro.livedistro/logs \
    --target-file=target/logs