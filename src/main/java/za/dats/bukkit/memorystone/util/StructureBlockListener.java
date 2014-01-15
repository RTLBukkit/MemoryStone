package za.dats.bukkit.memorystone.util;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import org.bukkit.Bukkit;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
//import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
//import org.bukkit.event.entity.EntityListener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import za.dats.bukkit.memorystone.Config;
import za.dats.bukkit.memorystone.Config.MemoryEffect;
import za.dats.bukkit.memorystone.MemoryStonePlugin;
import za.dats.bukkit.memorystone.economy.EconomyManager;
import za.dats.bukkit.memorystone.util.structure.Structure;
import za.dats.bukkit.memorystone.util.structure.StructureType;
import za.dats.bukkit.memorystone.util.structure.events.StructureBreakEvent;

/**
 * @author tim (originally HTBlockListener)
 * @author cmdrdats
 * 
 */
// public class StructureBlockListener extends BlockListener {
public class StructureBlockListener implements Listener {

	private final JavaPlugin plugin;
	private final StructureManager structureManager;

	public StructureBlockListener(JavaPlugin plugin, StructureManager structureManager) {
		this.plugin = plugin;
		this.structureManager = structureManager;
	}

	public void registerEvents() {
		PluginManager pm = this.plugin.getServer().getPluginManager();
		pm.registerEvents(this, this.plugin);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockPlace(BlockPlaceEvent event) throws IOException {
            checkPlacedBlock(event.getPlayer(), event.getBlock(), event);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreaking(BlockBreakEvent event) throws IOException {

		Block brokenblock = event.getBlock();
		Set<Structure> totems = structureManager.getStructuresFromBlock(brokenblock);

		if (totems == null)
			return;
                Boolean cancelled = false;
                for (Structure structure : totems) {
                    StructureBreakEvent e = new StructureBreakEvent(structure, event);
                    Bukkit.getServer().getPluginManager().callEvent(e);
                    if(e.isCancelled())
                        cancelled = e.isCancelled();
		}
                //If any structure breaks were cancelled, cancel the block break
                if (cancelled) {
                    event.setCancelled(cancelled);
                    return;
                }
        }
      
        @EventHandler(priority = EventPriority.MONITOR)
        public void onBlockBroken(BlockBreakEvent event) throws IOException {
            Block brokenblock = event.getBlock();
            blockBroke(brokenblock,event.getPlayer());
            
	}
        
        public void blockBroke(Block brokenblock, Player breaker) throws IOException{
                   
            Set<Structure> totems = structureManager.getStructuresFromBlock(brokenblock);
            
            for(Structure structure : totems) {
                    structureManager.removeStructure(breaker, structure);
                    structureManager.saveStructures();
                }
        }
        
	@EventHandler(priority = EventPriority.HIGH)
	protected void onEntityExploding(EntityExplodeEvent event) {
            //Current behavior is to protect all blocks in structure from explosions.
		for (Block brokenBlock : event.blockList()) {
			Set<Structure> totems = structureManager.getStructuresFromBlock(brokenBlock);
			if (totems != null) {
				event.setCancelled(true);
				return;
			}
		}
	}
        
        @EventHandler(priority = EventPriority.MONITOR)
	protected void onEntityExploded(EntityExplodeEvent event) {
            //Current behavior is to protect all blocks in structure from explosions.
		for (Block brokenBlock : event.blockList()) {
			Set<Structure> totems = structureManager.getStructuresFromBlock(brokenBlock);
			if (totems != null) {
				event.setCancelled(true);
				return;
			}
		}
	}

	public Structure checkPlacedBlock(Player player, Block behind, BlockPlaceEvent event) throws IOException {
		String owner = player.getName();

		Block placedblock = behind;
		List<StructureType> structureTypes = structureManager.getStructureTypes();

		TOTEMBUILD: for (StructureType structureType : structureTypes) {

			Structure structure = new Structure(structureType, placedblock, owner);
			if (!structure.verifyStructure()) {
				continue;
			}

			// check permissions!

			if (!player.hasPermission("memorystone.build")) {
				player.sendMessage(Config.getColorLang("nobuildpermission"));
				if (event != null) {
					event.setCancelled(true);
				}
				return null;
			}

			if (structureType.getPermissionRequired() != null && structureType.getPermissionRequired().length() > 0) {
				if (!player.hasPermission(structureType.getPermissionRequired())) {
					player.sendMessage(Config.getColorLang("nobuildpermission"));
					if (event != null) {
						event.setCancelled(true);
					}
					return null;
				}
			}

			for (Block block : structure.getBlocks()) {
				if (structureManager.getStructuresFromBlock(block) != null) {
					break TOTEMBUILD;
				}
			}

			EconomyManager economyManager = MemoryStonePlugin.getInstance().getEconomyManager();
			if (economyManager.isEconomyEnabled() && !player.hasPermission("memorystone.usefree")) {
				if (!economyManager.payBuildCost(player, structureType)) {
					player.sendMessage(Config.getColorLang("cantaffordbuild", "cost", economyManager.getBuildCostString(structureType)));
					if (event != null) {
						event.setCancelled(true);
					}
					return null;
				}
			}

			// lightning strike!
			if (Config.isEffectEnabled(MemoryEffect.LIGHTNING_ON_CREATE)) {
				placedblock.getWorld().strikeLightningEffect(placedblock.getLocation());
			}

			structure.setOwner(player.getName());
			structureManager.addStructure(player, structure);
			structureManager.saveStructures();
			return structure;
		}

		return null;
	}

}
