# VaultUnlocked - Abstraction Library for Bukkit - [![Build Status](https://ci.codemc.io/job/creatorfromhell/job/VaultUnlocked/badge/icon)](https://ci.codemc.io/job/creatorfromhell/job/VaultUnlocked/)

## For Developers:
Please see the [VaultUnlockedAPI](https://www.github.com/TheNewEconomy/VaultUnlockedAPI) page for
information on developing with VaultUnlocked's API. In the past, you would use the same
artifact as servers installed, but the API has now been split from the main
project and is under a different artifact name. Please make sure you accommodate
this change in your build process.

## Installing
Installing VaultUnlocked is as simple as copying the provided "VaultUnlocked.jar" to your
"<bukkit-install-dir>/plugins" directory, and the rest is automatic! If you
wish to perform configuration changes, this can be done via a configuration
file but should not be necessary in most cases. See the "Advanced
Configuration" section for more information.


## Why VaultUnlocked?
I have no preference regarding which library is best suited for
your plugin development efforts. I believe a central suite (or "Vault")
of solutions is a more effective approach than focusing on a single
category of plugins. This is the concept behind VaultUnlocked.

### Key Features You'll Appreciate

* **No Source Code Integration Needed**
  VaultUnlocked operates as a standalone plugin, so you only need to obtain an instance of it. This prevents conflicts with multiple plugins using the same namespaces. Simply include VaultUnlocked.jar in your download zip file for seamless integration!
* **Extensive Plugin Support**
  VaultUnlocked provides an abstraction layer not just for Economic plugins but for Permission plugins as well, ensuring broad compatibility.
* **Freedom of Choice**
  One of the best aspects of Bukkit is the freedom to choose what to use. More options benefit developers, so here’s to embracing choice!

### Enhanced Features of VaultUnlocked

* **Multi-Currency Support**
* **More Friendly PR Acceptance**
* **Folia Support**

Let me know if you need any further modifications!

## Permissions
* vault.admin
  - Determines if a player should receive the update notices

## License
Copyright (C) 2024 Daniel "creatorfromhell" Vidmar

VaultUnlocked is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

VaultUnlocked is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
with VaultUnlocked. If not, see <http://www.gnu.org/licenses/>.

## Building
VaultUnlocked comes with all libraries needed to build from the current branch and
also comes with an Apache Ant build file (build.xml) and a Maven build file
(pom.xml). Maven is currently the preferred build method.


## Dependencies
Because VaultUnlocked provides a bridge to other plugins, their binaries will be
required to build from. To ease this, they have been included in the lib
folder and will be updated from time to time. For plugin developers, it
is not necessary to use these libraries when implementing VaultUnlocked. You will
only need to compile against VaultUnlocked.


## Supported Plugins
VaultUnlocked provides abstraction for the following categories and plugins. If
you have your own plugin that you believe should be supported, you'll need
to add your own connector within your plugin as VaultUnlocked no longer maintains
new plugin connectors.

* Permissions
  - bPermissions
  - bPermissions 2 (https://dev.bukkit.org/projects/bpermissions)
  - DroxPerms
  - Group Manager (Essentials) (https://forums.bukkit.org/threads/15312/)
  - LuckPerms (https://www.spigotmc.org/resources/luckperms-an-advanced-permissions-plugin.28140/)
  - OverPermissions (https://dev.bukkit.org/projects/overpermissions)
  - Permissions 3 (https://forums.bukkit.org/threads/18430/)
  - PermissionsBukkit
  - Permissions Ex (PEX) (https://forums.bukkit.org/threads/18140/)
  - Privileges
  - rscPermissions
  - SimplyPerms
  - SuperPerms (Bukkit's default)
  - TotalPermissions (https://dev.bukkit.org/projects/totalpermissions)
  - XPerms
  - zPermissions

* Chat
  - bPermissions
  - Group Manager (Essentials) (https://forums.bukkit.org/threads/15312/)
  - iChat
  - LuckPerms (https://www.spigotmc.org/resources/luckperms-an-advanced-permissions-plugin.28140/)
  - mChat
  - mChatSuite
  - OverPermissions (https://dev.bukkit.org/projects/overpermissions)
  - Permissions 3 (https://forums.bukkit.org/threads/18430/)
  - Permissions Ex (PEX) (https://forums.bukkit.org/threads/18140/)
  - rscPermissions
  - TotalPermissions (https://dev.bukkit.org/projects/totalpermissions)
  - zPermissions
