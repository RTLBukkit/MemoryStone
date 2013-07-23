package za.dats.bukkit.memorystone.economy;

import java.util.Map;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import za.dats.bukkit.memorystone.Config;
import za.dats.bukkit.memorystone.MemoryStone;
import za.dats.bukkit.memorystone.MemoryStonePlugin;
import za.dats.bukkit.memorystone.util.structure.StructureType;

public class EconomyManager {
	private Economy economy;

	public void loadEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
            MemoryStonePlugin.getInstance().info("Hooked into Vault successfully");
        }else{
        	MemoryStonePlugin.getInstance().warn("Vault not found. Economy disabled");
        }
	}

	public boolean isEconomyEnabled() {
        return Config.isEconomyEnabled() && economy != null;
    }

	public String getFormattedCost(double cost) {
		return economy.format(cost);
	}

	public String getBuildCostString(StructureType structureType) {
		return economy.format(getBuildCost(structureType));
	}

	private double getBuildCost(StructureType structureType) {
		Map<String, String> meta = structureType.getMetadata();
		double result = 0;
		if (meta.containsKey("buildcost")) {
			try {
				result += Double.parseDouble(meta.get("buildcost"));
			} catch (NumberFormatException e) {
			}
		}

		return result;
	}

	public boolean payTeleportCost(Player player, MemoryStone stone) {
		/*
		 * TODO: Add world config options
		 * withdraw-from-world: stone, player
		 * deposit-to-world: stone, player
		 */
		EconomyResponse status = economy.withdrawPlayer(player.getName(), player.getWorld().getName(), stone.getTeleportCost());
		if (status.type == ResponseType.SUCCESS){
			//Are stone owners paid?
			if (Config.isEconomyOwnerPaid()){
				economy.depositPlayer(stone.getStructure().getOwner(), stone.getStructure().getWorld().getName(), stone.getTeleportCost());
			}
			return true;
		}else{
			player.sendMessage(status.errorMessage);
			return false;
		}
		/*
		MethodAccount account = economy.getAccount(player.getName());
		if (account == null) {
			return false;
		}

		double cost = stone.getTeleportCost();
		if (!account.hasEnough(cost)) {
			return false;
		}

		if (account.subtract(cost)) {
			if (Config.isEconomyOwnerPaid()) {
				String owner = stone.getStructure().getOwner();
				if (owner == null || stone.getStructure().getOwner().length() == 0) {
					return true;
				}

				MethodAccount ownerAccount = Methods.getMethod().getAccount(owner);
				if (ownerAccount != null) {
					ownerAccount.add(cost);
				}
			}

			return true;
		}

		return false;
		*/
	}

	public boolean payMemorizeCost(Player player, MemoryStone stone) {
		MethodAccount account = Methods.getMethod().getAccount(player.getName());
		if (account == null) {
			return false;
		}

		double cost = stone.getMemorizeCost();
		if (!account.hasEnough(cost)) {
			return false;
		}

		if (account.subtract(cost)) {
			if (Config.isEconomyOwnerPaid()) {
				String owner = stone.getStructure().getOwner();
				if (owner == null || stone.getStructure().getOwner().length() == 0) {
					return true;
				}

				MethodAccount ownerAccount = Methods.getMethod().getAccount(owner);
				if (ownerAccount != null) {
					ownerAccount.add(cost);
				}
			}

			return true;
		}

		return false;
	}

	public boolean payBuildCost(Player player, StructureType stone) {
		MethodAccount account = Methods.getMethod().getAccount(player.getName());
		if (account == null) {
			return false;
		}

		double cost = getBuildCost(stone);
		if (!account.hasEnough(cost)) {
			return false;
		}

		if (account.subtract(cost)) {
			return true;
		}

		return false;
	}
}
