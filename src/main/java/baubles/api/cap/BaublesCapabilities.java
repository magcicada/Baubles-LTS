package baubles.api.cap;

import baubles.api.IBauble;
import baubles.common.Baubles;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class BaublesCapabilities {
	/**
	 * Access to the baubles' capability.
	 */
	@CapabilityInject(IBaublesItemHandler.class)
	public static final Capability<IBaublesItemHandler> CAPABILITY_BAUBLES = null;

	@CapabilityInject(IBauble.class)
	public static final Capability<IBauble> CAPABILITY_ITEM_BAUBLE = null;

	public static class CapabilityBaubles<T extends IBaublesItemHandler> implements IStorage<IBaublesItemHandler> {

		@Override
		public NBTBase writeNBT (Capability<IBaublesItemHandler> capability, IBaublesItemHandler instance, EnumFacing side) {
			return null;
		}

		@Override
		public void readNBT (Capability<IBaublesItemHandler> capability, IBaublesItemHandler instance, EnumFacing side, NBTBase nbt){ }
	}
	
	public static class CapabilityItemBaubleStorage implements IStorage<IBauble> {

		@Override
		public NBTBase writeNBT (Capability<IBauble> capability, IBauble instance, EnumFacing side) {
			return null;
		}

		@Override
		public void readNBT (Capability<IBauble> capability, IBauble instance, EnumFacing side, NBTBase nbt) {

		}
	}

	public static final ResourceLocation ID_INVENTORY = new ResourceLocation(Baubles.MODID, "inventory");

	public static final ResourceLocation ID_ITEM = new ResourceLocation(Baubles.MODID, "item");
}
