#!/usr/bin/env sh
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
# SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects

# Killing processes by title or lsof causes also kills the Firefox browser.
# Killing all SSH commands is easier.
killall ssh