package za.dats.bukkit.memorystone.economy.payment.methods;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.plugin.Plugin;

import za.dats.bukkit.memorystone.economy.payment.Method;

/**
 * Vault Implementation of Method
 */
public class VaultMethod implements Method {
    private net.milkbowl.vault.Vault Vault;

    public net.milkbowl.vault.Vault getPlugin() {
        return this.Vault;
    }

    public String getName() {
        return this.Vault.getName();
    }

    public String getVersion() {
        return "1.2";
    }
    
    public int fractionalDigits() {
    	return this.Vault.
    }

	public String format(double amount) {
        return this.Vault.format(amount);
    }

    public boolean hasBanks() {
        return false;
    }

    public boolean hasBank(String bank) {
        return false;
    }

    public boolean hasAccount(String name) {
        return (new Accounts()).exists(name);
    }

    public boolean hasBankAccount(String bank, String name) {
        return false;
    }

    public MethodAccount getAccount(String name) {
        return new iCoAccount((new Accounts()).get(name));
    }

    public MethodBankAccount getBankAccount(String bank, String name) {
        return null;
    }

    public boolean isCompatible(Plugin plugin) {
        return plugin.getDescription().getName().equalsIgnoreCase("iconomy") 
        		&& plugin.getClass().getName().equals("com.iCo6.iConomy") 
        		&& plugin instanceof iConomy;
    }

    public void setPlugin(Plugin plugin) {
        iConomy = (iConomy)plugin;
    }

    public class iCoAccount implements MethodAccount {
        private Account account;
        private Holdings holdings;

        public iCoAccount(Account account) {
            this.account = account;
            this.holdings = account.getHoldings();
        }

        public Account getiCoAccount() {
            return account;
        }

        public double balance() {
            return this.holdings.getBalance();
        }

        public boolean set(double amount) {
            if(this.holdings == null) return false;
            this.holdings.setBalance(amount);
            return true;
        }

        public boolean add(double amount) {
            if(this.holdings == null) return false;
            this.holdings.add(amount);
            return true;
        }

        public boolean subtract(double amount) {
            if(this.holdings == null) return false;
            this.holdings.subtract(amount);
            return true;
        }

        public boolean multiply(double amount) {
            if(this.holdings == null) return false;
            this.holdings.multiply(amount);
            return true;
        }

        public boolean divide(double amount) {
            if(this.holdings == null) return false;
            this.holdings.divide(amount);
            return true;
        }

        public boolean hasEnough(double amount) {
            return this.holdings.hasEnough(amount);
        }

        public boolean hasOver(double amount) {
            return this.holdings.hasOver(amount);
        }

        public boolean hasUnder(double amount) {
            return this.holdings.hasUnder(amount);
        }

        public boolean isNegative() {
            return this.holdings.isNegative();
        }

        public boolean remove() {
            if(this.account == null) return false;
            this.account.remove();
            return true;
        }
    }
}
