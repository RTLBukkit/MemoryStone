package za.dats.bukkit.memorystone.util.structure.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockBreakEvent;
import za.dats.bukkit.memorystone.util.structure.Structure;

/**
 *
 * @author quietus
 */
public class StructureBreakEvent extends Event implements Cancellable{

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private Structure structure;

    public StructureBreakEvent(Structure structure, BlockBreakEvent event) {
        this.structure = structure; 
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
    
}
