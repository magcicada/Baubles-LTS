package baubles.api.cap;

import baubles.common.Baubles;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;

public class BaublesContainerProvider implements INBTSerializable<NBTTagCompound>, ICapabilityProvider {

	private final BaublesContainer container;
	public BaublesContainerProvider(BaublesContainer container) {
		this.container = container;
	}

	@Override
	public boolean hasCapability (Capability<?> capability, EnumFacing facing) {
		return capability == BaublesCapabilities.CAPABILITY_BAUBLES;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability (Capability<T> capability, EnumFacing facing) {
		if (capability == BaublesCapabilities.CAPABILITY_BAUBLES) {
			return (T) this.container;
		}
		return null;
	}

	@Override
	public NBTTagCompound serializeNBT () {
		return this.container.serializeNBT();
	}

	@Override
	public void deserializeNBT (NBTTagCompound nbt) {
		this.container.deserializeNBT(nbt);
	}
}
