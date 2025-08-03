/* This file is part of Vault.

    Vault is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Vault is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with Vault.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.milkbowl.vault;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Optional;
import java.util.logging.Logger;

public class Vault extends JavaPlugin {

    private Logger log;
    private static Vault instance;

    @Override
    public void onDisable() {
        // Remove all Service Registrations
        getServer().getServicesManager().unregisterAll(this);
    }

    @Override
    public void onEnable() {
        instance = this;
        log = this.getLogger();

        getCommand("vault-info").setExecutor(this);
        getCommand("vault-convert").setExecutor(this);

        log.info(String.format("Enabled Version %s", getDescription().getVersion()));
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String commandLabel, final String[] args) {

        if (command.getName().equalsIgnoreCase("vault-info")) {
            if (!sender.hasPermission("vault.admin.info")) {
                sender.sendMessage("You do not have permission to use that command!");
                return true;
            } else {
                infoCommand(sender);
                return true;
            }
        } else if (command.getName().equalsIgnoreCase("vault-convert")) {
            if (!sender.hasPermission("vault.admin.convert")) {
                sender.sendMessage("You do not have permission to use that command!");
                return true;
            } else {
                convertCommand(sender, args);
                return true;
            }
        } else {
            // Show help
            sender.sendMessage("VaultUnlocked Commands:");
            sender.sendMessage("  /vault-info - Displays information about Vault");
            sender.sendMessage("  /vault-convert [economy1] [economy2] - Converts from one Economy to another");
            return true;
        }
    }

    private void convertCommand(final CommandSender sender, final String[] args) {
        final Collection<RegisteredServiceProvider<Economy>> econs = this.getServer().getServicesManager().getRegistrations(Economy.class);
        final Collection<RegisteredServiceProvider<net.milkbowl.vault2.economy.Economy>> econs2 = this.getServer().getServicesManager().getRegistrations(net.milkbowl.vault2.economy.Economy.class);

        if (econs == null || econs.size() < 2 && econs2.isEmpty()) {

            sender.sendMessage("You must have at least 2 economies loaded to convert.");
            return;

        } else if (args.length != 2) {
            sender.sendMessage("You must specify only the economy to convert from and the economy to convert to. (names should not contain spaces)");
            return;
        }

        Economy econ1 = null;
        Economy econ2 = null;
        net.milkbowl.vault2.economy.Economy econ1Unlocked = null;
        net.milkbowl.vault2.economy.Economy econ2Unlocked = null;

        final StringBuilder economies = new StringBuilder();
        for (final RegisteredServiceProvider<Economy> econ : econs) {

            final String econName = econ.getProvider().getName().replace(" ", "");
            if (econName.equalsIgnoreCase(args[0])) {

                econ1 = econ.getProvider();
            } else if (econName.equalsIgnoreCase(args[1])) {

                econ2 = econ.getProvider();
            }

            if (economies.length() > 0) {

                economies.append(", ");
            }
            economies.append(econName);
        }

        for (final RegisteredServiceProvider<net.milkbowl.vault2.economy.Economy> econ : econs2) {

            final String econName = econ.getProvider().getName().replace(" ", "");
            if (econName.equalsIgnoreCase(args[0])) {

                econ1Unlocked = econ.getProvider();
            } else if (econName.equalsIgnoreCase(args[1])) {

                econ2Unlocked = econ.getProvider();
            }

            if (economies.length() > 0) {

                economies.append(", ");
            }
            economies.append(econName);
        }

        if (econ1 == null && econ1Unlocked == null) {

            sender.sendMessage("Could not find " + args[0] + " loaded on the server, check your spelling.");
            sender.sendMessage("Valid economies are: " + economies);
            return;
        } else if (econ2 == null && econ2Unlocked == null) {

            sender.sendMessage("Could not find " + args[1] + " loaded on the server, check your spelling.");
            sender.sendMessage("Valid economies are: " + economies);
            return;
        }

        sender.sendMessage("This may take some time to convert, expect server lag.");
        final boolean useUnlocked1 = (econ1Unlocked != null);
        final boolean useUnlocked2 = (econ2Unlocked != null);
        final String pluginID = "vault conversion";

        for (final OfflinePlayer op : Bukkit.getServer().getOfflinePlayers()) {

            if (useUnlocked1) {
                if (useUnlocked2) {
                    if (econ2Unlocked.hasAccount(op.getUniqueId())) {
                        continue;
                    }
                    econ2Unlocked.createAccount(op.getUniqueId(), op.getName());
                    final BigDecimal diff = econ1Unlocked.getBalance(pluginID, op.getUniqueId()).subtract(econ2Unlocked.getBalance(pluginID, op.getUniqueId()));
                    if (diff.compareTo(BigDecimal.ZERO) > 0) {
                        econ2Unlocked.deposit(pluginID, op.getUniqueId(), diff);
                    } else if (diff.compareTo(BigDecimal.ZERO) < 0) {
                        econ2Unlocked.withdraw(pluginID, op.getUniqueId(), diff.negate());
                    }
                } else {
                    if (econ2.hasAccount(op)) {
                        continue;
                    }
                    econ2.createPlayerAccount(op);
                    final BigDecimal diff = econ1Unlocked.getBalance(pluginID, op.getUniqueId()).subtract(BigDecimal.valueOf(econ2.getBalance(op)));
                    if (diff.compareTo(BigDecimal.ZERO) > 0) {
                        econ2.depositPlayer(op, diff.doubleValue());
                    } else if (diff.compareTo(BigDecimal.ZERO) < 0) {
                        econ2.withdrawPlayer(op, diff.negate().doubleValue());
                    }
                }
            } else {
                if (useUnlocked2) {
                    if (econ2Unlocked.hasAccount(op.getUniqueId())) {
                        continue;
                    }
                    econ2Unlocked.createAccount(op.getUniqueId(), op.getName());
                    final BigDecimal diff = BigDecimal.valueOf(econ1.getBalance(op)).subtract(econ2Unlocked.getBalance(pluginID, op.getUniqueId()));
                    if (diff.compareTo(BigDecimal.ZERO) > 0) {
                        econ2Unlocked.deposit(pluginID, op.getUniqueId(), diff);
                    } else if (diff.compareTo(BigDecimal.ZERO) < 0) {
                        econ2Unlocked.withdraw(pluginID, op.getUniqueId(), diff.negate());
                    }
                } else {
                    if (econ2.hasAccount(op)) {
                        continue;
                    }
                    econ2.createPlayerAccount(op);
                    final BigDecimal diff = BigDecimal.valueOf(econ1.getBalance(op)).subtract(BigDecimal.valueOf(econ2.getBalance(op)));
                    if (diff.compareTo(BigDecimal.ZERO) > 0) {
                        econ2.depositPlayer(op, diff.doubleValue());
                    } else if (diff.compareTo(BigDecimal.ZERO) < 0) {
                        econ2.withdrawPlayer(op, diff.negate().doubleValue());
                    }
                }
            }
        }
        sender.sendMessage("Conversion complete, please verify the data before using it.");
    }

    private void infoCommand(final CommandSender sender) {
        // Get String of Registered Economy Services
        final StringBuilder registeredEcons = new StringBuilder();
        final Collection<RegisteredServiceProvider<Economy>> econs = this.getServer().getServicesManager().getRegistrations(Economy.class);
        for (final RegisteredServiceProvider<Economy> econ : econs) {

            if (registeredEcons.length() > 0) registeredEcons.append(", ");
            registeredEcons.append(econ.getProvider().getName());
        }

        //VaultUnlocked Plugins
        final StringBuilder registeredModernEcons = new StringBuilder();
        final Collection<RegisteredServiceProvider<net.milkbowl.vault2.economy.Economy>> econs2 = this.getServer().getServicesManager().getRegistrations(net.milkbowl.vault2.economy.Economy.class);
        for (final RegisteredServiceProvider<net.milkbowl.vault2.economy.Economy> econ : econs2) {

            if (registeredModernEcons.length() > 0) registeredModernEcons.append(", ");
            registeredModernEcons.append(econ.getProvider().getName());
        }

        // Get String of Registered Permission Services
        final StringBuilder registeredPerms = new StringBuilder();
        final Collection<RegisteredServiceProvider<Permission>> perms = this.getServer().getServicesManager().getRegistrations(Permission.class);
        for (final RegisteredServiceProvider<Permission> perm : perms) {

            if (registeredPerms.length() > 0) registeredPerms.append(", ");
            registeredPerms.append(perm.getProvider().getName());
        }

        //VaultUnlocked Plugins
        final StringBuilder registeredModernPerms = new StringBuilder();
        final Collection<RegisteredServiceProvider<net.milkbowl.vault2.permission.Permission>> perms2 = this.getServer().getServicesManager().getRegistrations(net.milkbowl.vault2.permission.Permission.class);
        for (final RegisteredServiceProvider<net.milkbowl.vault2.permission.Permission> perm : perms2) {

            if (registeredModernPerms.length() > 0) registeredModernPerms.append(", ");
            registeredModernPerms.append(perm.getProvider().getName());
        }

        final StringBuilder registeredChats = new StringBuilder();
        final Collection<RegisteredServiceProvider<Chat>> chats = this.getServer().getServicesManager().getRegistrations(Chat.class);
        for (final RegisteredServiceProvider<Chat> chat : chats) {

            if (registeredChats.length() > 0) registeredChats.append(", ");
            registeredChats.append(chat.getProvider().getName());
        }

        //VaultUnlocked Plugins
        final StringBuilder registeredModernChats = new StringBuilder();
        final Collection<RegisteredServiceProvider<net.milkbowl.vault2.chat.Chat>> chats2 = this.getServer().getServicesManager().getRegistrations(net.milkbowl.vault2.chat.Chat.class);
        for (final RegisteredServiceProvider<net.milkbowl.vault2.chat.Chat> chat : chats2) {

            if (registeredModernChats.length() > 0) registeredModernChats.append(", ");
            registeredModernChats.append(chat.getProvider().getName());
        }

        // Get Economy & Permission primary Services
        Economy econ = null;
        final RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp != null) {
            econ = rsp.getProvider();
        }

        //VaultUnlocked
        net.milkbowl.vault2.economy.Economy econ2 = null;
        final RegisteredServiceProvider<net.milkbowl.vault2.economy.Economy> rsp2 = getServer().getServicesManager().getRegistration(net.milkbowl.vault2.economy.Economy.class);
        if (rsp2 != null) {
            econ2 = rsp2.getProvider();
        }

        Permission perm = null;
        final RegisteredServiceProvider<Permission> rspp = getServer().getServicesManager().getRegistration(Permission.class);
        if (rspp != null) {
            perm = rspp.getProvider();
        }

        //VaultUnlocked
        net.milkbowl.vault2.permission.Permission perm2 = null;
        final RegisteredServiceProvider<net.milkbowl.vault2.permission.Permission> rspp2 = getServer().getServicesManager().getRegistration(net.milkbowl.vault2.permission.Permission.class);
        if (rspp2 != null) {
            perm2 = rspp2.getProvider();
        }

        Chat chat = null;
        final RegisteredServiceProvider<Chat> rspc = getServer().getServicesManager().getRegistration(Chat.class);
        if (rspc != null) {
            chat = rspc.getProvider();
        }

        //VaultUnlocked
        net.milkbowl.vault2.chat.Chat chat2 = null;
        final RegisteredServiceProvider<net.milkbowl.vault2.chat.Chat> rspc2 = getServer().getServicesManager().getRegistration(net.milkbowl.vault2.chat.Chat.class);
        if (rspc2 != null) {
            chat2 = rspc2.getProvider();
        }

        // Send user some info!
        sender.sendMessage(String.format("[%s] Vault v%s Information", getDescription().getName(), getDescription().getVersion()));
        sender.sendMessage(String.format("[%s] Economy Legacy: %s%s", getDescription().getName(), (econ == null) ? "None" : econ.getName(), (registeredEcons.length() == 0) ? "" : " [" + registeredEcons + "]"));
        sender.sendMessage(String.format("[%s] Economy Modern: %s%s", getDescription().getName(), (econ2 == null) ? "None" : econ2.getName(), (registeredModernEcons.length() == 0) ? "" : " [" + registeredModernEcons + "]"));
        sender.sendMessage(String.format("[%s] Permission Legacy: %s%s", getDescription().getName(), (perm == null) ? "None" : perm.getName(), (registeredPerms.length() == 0) ? "" : " [" + registeredPerms + "]"));
        sender.sendMessage(String.format("[%s] Permission Modern: %s%s", getDescription().getName(), (perm2 == null) ? "None" : perm2.getName(), (registeredModernPerms.length() == 0) ? "" : " [" + registeredModernPerms + "]"));
        sender.sendMessage(String.format("[%s] Chat Legacy: %s%s", getDescription().getName(), (chat == null) ? "None" : chat.getName(), (registeredChats.length() == 0) ? "" : " [" + registeredChats + "]"));
        sender.sendMessage(String.format("[%s] Chat Modern: %s%s", getDescription().getName(), (chat2 == null) ? "None" : chat2.getName(), (registeredModernChats.length() == 0) ? "" : " [" + registeredChats + "]"));
    }

    public Optional<net.milkbowl.vault2.economy.Economy> modernProvider() {

        final RegisteredServiceProvider<net.milkbowl.vault2.economy.Economy> econ = getServer().getServicesManager().getRegistration(net.milkbowl.vault2.economy.Economy.class);

        return Optional.ofNullable(econ.getProvider());
    }

    public static Vault instance() {
        return instance;
    }
}
