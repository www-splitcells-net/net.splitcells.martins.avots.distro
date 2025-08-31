#!/usr/bin/env sh
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
set -e
[ ! -d "bin" ] && cd ..
cd ../net.splitcells.network
bin/worker.execute.py \
  --bootstrap-remote=splitcells@raspberrypi-v2.local \
  --auto-configure-cpu-architecture-explicitly=true \
  --verbose=true
bin/worker.execute.py \
  --build-remote=splitcells@raspberrypi-v2.local \
  --auto-configure-cpu-architecture-explicitly=true \
  --verbose=true
bin/worker.execute.py \
  --execute-via-ssh-at=splitcells@raspberrypi-v2.local \
  --command="cd ~/.local/state/net.splitcells.network.worker/repos/public/net.splitcells.network && bin/test.everything" \
  --auto-configure-cpu-architecture-explicitly=true