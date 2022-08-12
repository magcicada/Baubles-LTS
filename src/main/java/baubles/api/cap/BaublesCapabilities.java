package baubles.api.cap;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.common.Baubles;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.Constants;

import java.util.List;
import java.util.Set;
import java.util.SortedMap;

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
			SortedMap<String, BaublesContainer> baubleMap = instance.getBaubleMap();
			NBTTagCompound compound = new NBTTagCompound();
			NBTTagList taglist = new NBTTagList();

			for (String identifier : baubleMap.keySet()) {
				BaublesContainer stackHandler = baubleMap.get(identifier);
				NBTTagCompound itemtag = stackHandler.serializeNBT();
				itemtag.setString("Identifier", identifier);
				taglist.appendTag(itemtag);
			}
			compound.setTag("Baubles", taglist);
			return compound;
		}

		@Override
		public void readNBT (Capability<IBaublesItemHandler> capability, IBaublesItemHandler instance, EnumFacing side, NBTBase nbt) {
			NBTTagList tagList = ((NBTTagCompound)nbt).getTagList("Baubles", Constants.NBT.TAG_COMPOUND);
			List<NBTBase> tagList1 = Lists.newArrayList(tagList);

			if (!tagList.isEmpty()) {
				SortedMap<String, BaublesContainer> baubles = instance.getDefaultSlots();

				for (int i = 0; i < tagList1.size(); i++) {
					NBTTagCompound itemtag = tagList.getCompoundTagAt(i);
					String identifier = itemtag.getString("Identifier");
					BaubleType type = BaublesApi.getType(identifier);
					BaublesContainer stackHandler = new BaublesContainer();
					stackHandler.deserializeNBT(itemtag);

					if (type != null) {
						baubles.put(identifier, stackHandler);
					} else {

						for (int j = 0; j < stackHandler.getSlots(); j++) {
							ItemStack stack = stackHandler.getStackInSlot(j);

							if (!stack.isEmpty()) {
								instance.addInvalid(stackHandler.getStackInSlot(j));
							}
						}
					}
				}
				instance.setBaubleMap(baubles);
			}
		}
	}
	
	public static class CapabilityItemBaubleStorage implements IStorage<IBauble> {

		@Override
		public NBTBase writeNBT (Capability<IBauble> capability, IBauble instance, EnumFacing side) {
			return new NBTTagCompound();
		}

		@Override
		public void readNBT (Capability<IBauble> capability, IBauble instance, EnumFacing side, NBTBase nbt) {

		}
	}
}
