#!/usr/bin/env sh
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
set -e
[ ! -d "bin" ] && cd ..
# TODO net.splitcells.network.worker.bootstrap.remote.at ubuntu@riscv.local
mvn clean compile exec:java \
  -Dexec.mainClass="net.splitcells.network.worker.via.java.NetworkWorker" \
  -Dexec.args="--bootstrap-remote=ubuntu@riscv.local"
