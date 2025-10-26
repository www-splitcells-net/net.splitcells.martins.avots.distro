#!/usr/bin/env sh
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
set -e
cd ../net.splitcells.network
bin/worker.execute.py \
  --program-name=net.splitcells.martins.avots.distro.livedistrocell \
  --bootstrap-remote=martins-avots@live.splitcells.net \
  --verbose=true
bin/worker.execute.py \
  --program-name=net.splitcells.martins.avots.distro.livedistrocell \
  --build-remote=martins-avots@live.splitcells.net \
  --verbose=true
bin/worker.execute.py \
  --program-name=net.splitcells.martins.avots.distro.livedistrocell \
  --source-repo=net.splitcells.martins.avots.distro \
  --execute-via-ssh-at=martins-avots@live.splitcells.net \
  --class-for-execution=net.splitcells.martins.avots.distro.LiveDistroCell \
  --is-daemon=true \
  --port-publishing=8443:8443,8080:8080 \
  --use-playwright=true \
  --verbose=true