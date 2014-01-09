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
		 * TODO: Add a pay-to world node
		 * A pay-to node can be defined per structure in structuretypes.yml
		 * Can be either PLAYER_WORLD or STONE_WORLD
		 * Defaults to STONE_WORLD
		 */
		
		//Withdraw from the player
		EconomyResponse wStatus = economy.withdrawPlayer(player.getName(), player.getWorld().getName(), stone.getTeleportCost());
		if (wStatus.type == ResponseType.SUCCESS){
			//Check if stone owners are paid, and attempt to pay them
			if (Config.isEconomyOwnerPaid()){
				EconomyResponse dStatus = economy.depositPlayer(stone.getStructure().getOwner(), stone.getStructure().getWorld().getName(), stone.getTeleportCost());
				//Deposit failed for some reason
				if (dStatus.type != ResponseType.SUCCESS){
					player.sendMessage(dStatus.errorMessage);
					//Return the withdrawn amount to the player (shouldn't fail if the withdraw was successful)
					economy.depositPlayer(player.getName(), player.getWorld().getName(), stone.getTeleportCost());
					return false;
				}
			}
			return true;
		}else{
			player.sendMessage(wStatus.errorMessage);
			return false;
		}
	}

	public boolean payMemorizeCost(Player player, MemoryStone stone) {
		/*
		 * TODO: Add a pay-to world node
		 * A pay-to node can be defined per structure in structuretypes.yml
		 * Can be either PLAYER_WORLD or STONE_WORLD
		 * Defaults to STONE_WORLD
		 */
		
		//Withdraw from the player
		EconomyResponse wStatus = economy.withdrawPlayer(player.getName(), player.getWorld().getName(), stone.getMemorizeCost());
		if (wStatus.type == ResponseType.SUCCESS){
			//Check if stone owners are paid, and attempt to pay them
			if (Config.isEconomyOwnerPaid()){
				EconomyResponse dStatus = economy.depositPlayer(stone.getStructure().getOwner(), stone.getStructure().getWorld().getName(), stone.getMemorizeCost());
				//Deposit failed for some reason
				if (dStatus.type != ResponseType.SUCCESS){
					player.sendMessage(dStatus.errorMessage);
					//Return the withdrawn amount to the player (shouldn't fail if the withdraw was successful)
					economy.depositPlayer(player.getName(), player.getWorld().getName(), stone.getMemorizeCost());
					return false;
				}
			}
			return true;
		}else{
			player.sendMessage(wStatus.errorMessage);
			return false;
		}
	}

	public boolean payBuildCost(Player player, StructureType stone) {
		EconomyResponse wStatus = economy.withdrawPlayer(player.getName(), player.getWorld().getName(), getBuildCost(stone));
		if (wStatus.type == ResponseType.SUCCESS){
			return true;
		}else{
			player.sendMessage(wStatus.errorMessage);
			return false;
		}
	}
}
