#!/usr/bin/env sh
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
set -e
sudo apt-get install -y podman-compose netavark aardvark-dns
cd src/main/podman
podman-compose up --detach
podman-compose restart # Ensure a restart, so that all configs are really applied, if the pods where already running.
exit

TODO

ssh -L 9000:localhost:3000 martins-avots@live.splitcells.net

admin password

sudo nano /etc/containers/registries.conf
unqualified-search-registries = ["docker.io"]
sudo nano /etc/containers/containers.conf
[network]
network_backend = "netavark"
