#!/usr/bin/env sh
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
set -e
sudo apt-get install -y podman-compose netavark aardvark-dns
cd src/main/podman
podman-compose down # Ensure a restart, so that all configs are really applied.
podman-compose up --detach
