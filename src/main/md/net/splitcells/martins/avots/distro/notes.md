----
* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
* SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
----
# Notes
## Keylogger in IDE - 2020-05-31
Updated IntelliJ on Ubuntu today and got destroyed by keyboard lag.
What was the reason? It seems to be caused by the plugin "Code With Me" for Online Coding Collaborative,
which is installed by default.
So let it get me straight: A service for sharing coding sessions causes keyboard lag,
even though I am not using it?
That's a bad optic, because it basically looks like a keylogger.

I found this via the following log message in the IDE: `380 ms to grab EDT for CWMTelephonyGroup#update (com.jetbrains.rd.platform.codeWithMe.telephony.CWMTelephonyGroup)`
Fixed it by installing an older version: `flatpak update --commit=3af5e35de5d37d561877ea1985309e791377cbe1453a45dcdc8240055edfd4c8 com.jetbrains.IntelliJ-IDEA-Community`
## Multiple Cursors with IntelliJ - 2020-11-04
Enter block mode by pressing `Alt + Shift + Insert` or by `Edit -> Column Selection Mode`.
In this mode you can create multiple cursors via `Shift + Up/Down`.

Alternatively press `Ctrl` twice.

