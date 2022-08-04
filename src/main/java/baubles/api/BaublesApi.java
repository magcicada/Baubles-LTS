package baubles.api;

import baubles.api.cap.*;
import baubles.api.inv.BaublesInventoryWrapper;
import baubles.common.Baubles;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import io.netty.util.internal.ConcurrentSet;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInterModComms;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Azanor
 */
public class BaublesApi {


	/**
	 * Retrieves the baubles inventory capability handler for the supplied player
	 */
	public static IBaublesItemHandler getBaublesHandler(EntityPlayer player)
	{
		IBaublesItemHandler handler = player.getCapability(BaublesCapabilities.CAPABILITY_BAUBLES, null);
		handler.setPlayer(player);
		return handler;
	}

	/**
	 * Retrieves the baubles capability handler wrapped as a IInventory for the supplied player
	 */
	@Deprecated
	public static IInventory getBaubles(EntityPlayer player)
	{
		IBaublesItemHandler handler = player.getCapability(BaublesCapabilities.CAPABILITY_BAUBLES, null);
		handler.setPlayer(player);
		return new BaublesInventoryWrapper(handler, player);
	}

	/**
	 * Returns if the passed in item is equipped in a bauble slot. Will return the first slot found
	 * @return -1 if not found and slot number if it is found 
	 */
	public static int isBaubleEquipped(EntityPlayer player, Item bauble) {
		IBaublesItemHandler handler = getBaublesHandler(player);
		for (int a=0;a<handler.getSlots();a++) {
			if (!handler.getStackInSlot(a).isEmpty() && handler.getStackInSlot(a).getItem()==bauble) return a;
		}
		return -1;
	}

}
