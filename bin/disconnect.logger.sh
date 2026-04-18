# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
lsof -ti:9000 | xargs kill -9 # Grafana
lsof -ti:4040 | xargs kill -9 # Pyroscope
lsof -ti:3100 | xargs kill -9 # Loki
