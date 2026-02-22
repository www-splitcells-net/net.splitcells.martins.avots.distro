# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
set -e
echo You can now open Grafana via http://localhost:9000.
ssh -L 9000:localhost:3000 martins-avots@live.splitcells.net
echo You can now open Pyroscope via http://localhost:4040.
ssh -L 4040:localhost:4040 martins-avots@live.splitcells.net