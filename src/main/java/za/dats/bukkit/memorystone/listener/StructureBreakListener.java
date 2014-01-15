/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package za.dats.bukkit.memorystone.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import za.dats.bukkit.memorystone.Config;
import za.dats.bukkit.memorystone.util.structure.events.StructureBreakEvent;

/**
 *
 * @author quietus
 */
public class StructureBreakListener implements Listener {
    
    @EventHandler
            public void StructureBroken(StructureBreakEvent e){
                    Player player = e.getPlayer();
                    Block brokenblock = e.getbrokenblock();
// check permissions!
		if (!player.hasPermission("memorystone.break")) {
			e.setCancelled(true);
			player.sendMessage(Config.getColorLang("nobreakpermission"));
			return;
		}

		// lightning strike!
		if (Config.isEffectEnabled(Config.MemoryEffect.LIGHTNING_ON_BREAK)) {
			brokenblock.getWorld().strikeLightningEffect(brokenblock.getLocation());
		}

		// if (!this.plugin.getConfigManager().isQuiet()) {
		// }
}
}