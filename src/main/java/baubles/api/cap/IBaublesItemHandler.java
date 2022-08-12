package baubles.api.cap;

import baubles.api.BaubleType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import java.util.SortedMap;

public interface IBaublesItemHandler extends IItemHandlerModifiable {

	/**
	 * Checks if the item is valid for the baubles slot
	 */
	boolean isItemValidForSlot(int slot, ItemStack stack, EntityLivingBase player);

	/**
	 * Used internally to prevent equip/unequip events from triggering when they shouldn't
	 */
	boolean isEventBlocked();

	void setEventBlock(boolean blockEvents);

	/**
	 * Used internally for syncing. Indicates if the inventory has changed since last sync
	 */
	boolean isChanged(int slot);

	void setChanged(int slot, boolean changed);

	void setPlayer(EntityLivingBase player);

	/**
	 * An unmodifiable view of the map of the current bauble items, sorted by the {@link BaubleType} identifier
	 * @return  The current bauble equipped
	 */
	SortedMap<String, BaublesContainer> getBaubleMap();

	SortedMap<String, BaublesContainer> getDefaultSlots();

	/**
	 * Sets the current baubles map to the one passed in
	 * @param map The baubles collection that will replace the current one
	 */
	void setBaubleMap(SortedMap<String, BaublesContainer> map);

	/**
	 * Adds an ItemStack to the invalid cache
	 * Used internally for storing items found in the process of disabling/removing slots to be given back to
	 * the player or dropped on the ground in other cases
	 * @param stack The ItemStack to add
	 */
	void addInvalid(ItemStack stack);


	/**
	 * Sets the ItemStack in the given slot index for the given {@link BaubleType} identifier
	 * @param identifier    The identifier for the {@link BaubleType}
	 * @param slot          The slot index of the {@link BaublesContainer} for the given identifier
	 * @param stack         The ItemStack to place in the slot
	 */
	void setStackInSlot(String identifier, int slot, ItemStack stack);

	/**
	 * Enables the {@link BaubleType} for a given identifier, adding the default settings to the bauble map
	 * @param identifier The identifier for the {@link BaubleType}
	 */
	void enableBauble(String identifier);

	/**
	 * Disables the {@link BaubleType} for a given identifier, removing it from the bauble map
	 * Note that the default implementation handles catching and returning ItemStacks that are found in these slots
	 * @param identifier The identifier for the {@link BaubleType}
	 */
	void disableBauble(String identifier);

}
