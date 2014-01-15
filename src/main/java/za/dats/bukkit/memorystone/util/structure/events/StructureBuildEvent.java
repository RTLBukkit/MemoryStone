package za.dats.bukkit.memorystone.util.structure.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import za.dats.bukkit.memorystone.util.structure.Structure;

/**
 *
 * @author quietus
 */
public class StructureBuildEvent extends Event implements Cancellable{

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private Structure structure;

    public StructureBuildEvent(Structure structure) {
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
