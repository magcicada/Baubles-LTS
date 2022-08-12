package baubles.common.container;

import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.BaublesContainer;
import baubles.api.cap.IBaublesItemHandler;
import baubles.common.Baubles;
import baubles.common.Config;
import baubles.common.network.PacketHandler;
import baubles.common.network.server.SPacketBaubleScroll;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.EnumActionResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.SortedMap;

public class ContainerPlayerExpanded extends Container
{
	/**
	 * The crafting matrix inventory.
	 */
	public final InventoryCrafting craftMatrix = new InventoryCrafting(this, 2, 2);
	public final InventoryCraftResult craftResult = new InventoryCraftResult();
	public IBaublesItemHandler baubles;

	/**
	 * Determines if inventory manipulation should be handled.
	 */
	public boolean isLocalWorld;
	private final EntityPlayer thePlayer;

	private int lastScrollIndex;
	private static final EntityEquipmentSlot[] equipmentSlots = new EntityEquipmentSlot[] {EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};

	public ContainerPlayerExpanded(InventoryPlayer playerInv, boolean par2, EntityPlayer player)
	{
		this.isLocalWorld = par2;
		this.thePlayer = player;
		baubles = player.getCapability(BaublesCapabilities.CAPABILITY_BAUBLES, null);

		this.addSlotToContainer(new SlotCrafting(playerInv.player, this.craftMatrix, this.craftResult, 0, 154, 28));

		for (int i = 0; i < 2; ++i)
		{
			for (int j = 0; j < 2; ++j)
			{
				if (!Config.useCurioGUI) {
					this.addSlotToContainer(new Slot(this.craftMatrix, j + i * 2, 116 + j * 18, 18 + i * 18));
				} else {
					this.addSlotToContainer(new Slot(this.craftMatrix, j + i * 2, 98 + j * 18, 18 + i * 18));
				}
			}
		}

		for (int k = 0; k < 4; k++)
		{
			final EntityEquipmentSlot slot = equipmentSlots[k];
			this.addSlotToContainer(new Slot(playerInv, 36 + (3 - k), 8, 8 + k * 18)
			{
				@Override
				public int getSlotStackLimit()
				{
					return 1;
				}
				@Override
				public boolean isItemValid(ItemStack stack)
				{
					return stack.getItem().isValidArmor(stack, slot, player);
				}
				@Override
				public boolean canTakeStack(EntityPlayer playerIn)
				{
					ItemStack itemstack = this.getStack();
					return (itemstack.isEmpty() || playerIn.isCreative() || !EnchantmentHelper.hasBindingCurse(itemstack)) && super.canTakeStack(playerIn);
				}
				@Override
				public String getSlotTexture()
				{
					return ItemArmor.EMPTY_SLOT_NAMES[slot.getIndex()];
				}
			});
		}

		if (!Config.useCurioGUI) {
			this.addSlotToContainer(new SlotBauble(player, baubles, 0, 77, 8));
			this.addSlotToContainer(new SlotBauble(player, baubles, 1, 77, 8 + 1 * 18));
			this.addSlotToContainer(new SlotBauble(player, baubles, 2, 77, 8 + 2 * 18));
			this.addSlotToContainer(new SlotBauble(player, baubles, 3, 77, 8 + 3 * 18));
			this.addSlotToContainer(new SlotBauble(player, baubles, 4, 96, 8));
			this.addSlotToContainer(new SlotBauble(player, baubles, 5, 96, 8 + 1 * 18));
			this.addSlotToContainer(new SlotBauble(player, baubles, 6, 96, 8 + 2 * 18));
		}

		for (int i = 0; i < 3; ++i)
		{
			for (int j = 0; j < 9; ++j)
			{
				this.addSlotToContainer(new Slot(playerInv, j + (i + 1) * 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int i = 0; i < 9; ++i)
		{
			this.addSlotToContainer(new Slot(playerInv, i, 8 + i * 18, 142));
		}

		if (!Config.useCurioGUI) {
			this.addSlotToContainer(new Slot(playerInv, 40, 96, 62) {
				@Override
				public boolean isItemValid(ItemStack stack) {
					return super.isItemValid(stack);
				}

				@Override
				public String getSlotTexture() {
					return "minecraft:items/empty_armor_slot_shield";
				}
			});
		} else {

			this.addSlotToContainer(new Slot(playerInv, 40, 77, 62) {
				@Override
				public boolean isItemValid(ItemStack stack) {
					return super.isItemValid(stack);
				}

				@Override
				public String getSlotTexture() {
					return "minecraft:items/empty_armor_slot_shield";
				}
			});
		}

		if (!Config.useCurioGUI) {
			this.onCraftMatrixChanged(this.craftMatrix);
		} else {
			if (baubles != null) {
				SortedMap<String, BaublesContainer> baubleMap = baubles.getBaubleMap();
				int slots = 0;
				int yOffset = 12;

				IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(thePlayer);

				for (String identifier : baubleMap.keySet()) {
					BaublesContainer stackHandler = baubleMap.get(identifier);

					if (!stackHandler.isHidden()) {

						for (int i = 0; i < stackHandler.getSlots() && slots < 8; i++) {
							this.addSlotToContainer(new SlotBauble(player, stackHandler, i, identifier,-18, yOffset));
							yOffset += 18;
							slots++;
						}
					}
				}
			}
		}

		if (Config.useCurioGUI) {
			this.scrollToIndex(0);
		}
	}

	public void scrollToIndex(int indexIn) {
		if (Config.useCurioGUI) {

			if (this.baubles != null) {

				IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(thePlayer);
				SortedMap<String, BaublesContainer> baubleMap = baubles.getBaubleMap();
				int slots = 0;
				int yOffset = 12;
				int index = 0;
				this.inventorySlots.subList(46, this.inventorySlots.size()).clear();
				this.inventoryItemStacks.subList(46, this.inventoryItemStacks.size()).clear();

				for (String identifier : baubleMap.keySet()) {
					BaublesContainer stackHandler = baubleMap.get(identifier);

					if (!stackHandler.isHidden()) {

						for (int i = 0; i < stackHandler.getSlots() && slots < 8; i++) {

							if (index >= indexIn) {
								this.addSlotToContainer(new SlotBauble(thePlayer, stackHandler, i, identifier,-18, yOffset));
								yOffset += 18;
								slots++;
							}
							index++;
						}
					}
				}

				if (!this.isLocalWorld) {
					EntityPlayerMP mPlayer = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(thePlayer.getUniqueID());
					PacketHandler.INSTANCE.sendTo(new SPacketBaubleScroll(this.windowId, indexIn), mPlayer);
				}
				lastScrollIndex = indexIn;
			}
		}
	}

	public void scrollTo(float pos) {
		if (Config.useCurioGUI) {
			if (this.baubles != null) {
				int k = (baubles.getSlots() - 8);
				int j = (int) ((double) (pos * (float) k) + 0.5D);

				if (j < 0) {
					j = 0;
				}

				if (j == this.lastScrollIndex) {
					return;
				}

				if (this.isLocalWorld) {
					PacketHandler.INSTANCE.sendToServer(new SPacketBaubleScroll(this.windowId, j));
				}
			}
		}
	}

	public boolean canScroll() {
		return baubles != null && baubles.getSlots() > 8;
	}

	/**
	 * Callback for when the crafting matrix is changed.
	 */
	@Override
	public void onCraftMatrixChanged(IInventory par1IInventory)
	{
		this.slotChangedCraftingGrid(this.thePlayer.getEntityWorld(), this.thePlayer, this.craftMatrix, this.craftResult);
	}

	/**
	 * Called when the container is closed.
	 */
	@Override
	public void onContainerClosed(EntityPlayer player)
	{
		super.onContainerClosed(player);
		this.craftResult.clear();

		if (!player.world.isRemote)
		{
			this.clearContainer(player, player.world, this.craftMatrix);
		}
	}

	@Override
	public boolean canInteractWith(@Nonnull EntityPlayer par1EntityPlayer)
	{
		return true;
	}

	/**
	 * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
	 */
	@Nonnull
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);

		if (slot != null && slot.getHasStack())
		{
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			EntityEquipmentSlot entityequipmentslot = EntityLiving.getSlotForItemStack(itemstack);
			if (!Config.useCurioGUI) {
				int slotShift = baubles.getSlots();

				if (index == 0) {
					if (!this.mergeItemStack(itemstack1, 9 + slotShift, 45 + slotShift, true)) {
						return ItemStack.EMPTY;
					}

					slot.onSlotChange(itemstack1, itemstack);
				} else if (index < 5) {
					if (!this.mergeItemStack(itemstack1, 9 + slotShift, 45 + slotShift, false)) {
						return ItemStack.EMPTY;
					}
				} else if (index < 9) {
					if (!this.mergeItemStack(itemstack1, 9 + slotShift, 45 + slotShift, false)) {
						return ItemStack.EMPTY;
					}
				}

				// baubles -> inv
				else if (index < 9 + slotShift) {
					if (!this.mergeItemStack(itemstack1, 9 + slotShift, 45 + slotShift, false)) {
						return ItemStack.EMPTY;
					}
				}

				// inv -> armor
				else if (entityequipmentslot.getSlotType() == EntityEquipmentSlot.Type.ARMOR && !(this.inventorySlots.get(8 - entityequipmentslot.getIndex())).getHasStack()) {
					int i = 8 - entityequipmentslot.getIndex();

					if (!this.mergeItemStack(itemstack1, i, i + 1, false)) {
						return ItemStack.EMPTY;
					}
				}

				// inv -> offhand
				else if (entityequipmentslot == EntityEquipmentSlot.OFFHAND && !this.inventorySlots.get(45 + slotShift).getHasStack()) {
					if (!this.mergeItemStack(itemstack1, 45 + slotShift, 46 + slotShift, false)) {
						return ItemStack.EMPTY;
					}
				}

				// inv -> bauble
				else if (itemstack.hasCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null)) {
					IBauble bauble = itemstack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);
					for (int baubleSlot : bauble.getBaubleType(itemstack).getValidSlots()) {
						if (bauble.canEquip(itemstack1, thePlayer) && !this.inventorySlots.get(baubleSlot + 9).getHasStack() &&
								!this.mergeItemStack(itemstack1, baubleSlot + 9, baubleSlot + 10, false)) {
							return ItemStack.EMPTY;
						}
						if (itemstack1.getCount() == 0) break;
					}
				} else if (index >= 9 + slotShift && index < 36 + slotShift) {
					if (!this.mergeItemStack(itemstack1, 36 + slotShift, 45 + slotShift, false)) {
						return ItemStack.EMPTY;
					}
				} else if (index >= 36 + slotShift && index < 45 + slotShift) {
					if (!this.mergeItemStack(itemstack1, 9 + slotShift, 36 + slotShift, false)) {
						return ItemStack.EMPTY;
					}
				} else if (!this.mergeItemStack(itemstack1, 9 + slotShift, 45 + slotShift, false)) {
					return ItemStack.EMPTY;
				}

				if (itemstack1.isEmpty()) {
					slot.putStack(ItemStack.EMPTY);
				} else {
					slot.onSlotChanged();
				}

				if (itemstack1.getCount() == itemstack.getCount()) {
					return ItemStack.EMPTY;
				}

				if (itemstack1.isEmpty() && !baubles.isEventBlocked() && slot instanceof SlotBauble &&
						itemstack.hasCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null)) {

					itemstack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null).onUnequipped(itemstack, playerIn);
				}

				ItemStack itemstack2 = slot.onTake(playerIn, itemstack1);

				if (index == 0) {
					playerIn.dropItem(itemstack2, false);
				}
			} else {
				if (index == 0) {

					if (!this.mergeItemStack(itemstack1, 9, 45, true)) {
						return ItemStack.EMPTY;
					}
					slot.onSlotChange(itemstack1, itemstack);
				} else if (index < 5) {

					if (!this.mergeItemStack(itemstack1, 9, 45, false)) {
						return ItemStack.EMPTY;
					}
				} else if (index < 9) {

					if (!this.mergeItemStack(itemstack1, 9, 45, false)) {
						return ItemStack.EMPTY;
					}
				} else if (entityequipmentslot.getSlotType() == EntityEquipmentSlot.Type.ARMOR && !(this.inventorySlots.get(8 - entityequipmentslot.getIndex())).getHasStack()) {
					int i = 8 - entityequipmentslot.getIndex();

					if (!this.mergeItemStack(itemstack1, i, i + 1, false)) {
						return ItemStack.EMPTY;
					}
				} else if (index < 46 && BaublesApi.getOBaubles(itemstack).isPresent()) {

					if (this.mergeItemStack(itemstack1, 46, this.inventorySlots.size(), false)) {
						return ItemStack.EMPTY;
					}
				} else if (entityequipmentslot == EntityEquipmentSlot.OFFHAND && !(this.inventorySlots.get(45)).getHasStack()) {

					if (!this.mergeItemStack(itemstack1, 45, 46, false)) {
						return ItemStack.EMPTY;
					}
				} else if (!this.mergeItemStack(itemstack1, 9, 45, false)) {
					return ItemStack.EMPTY;
				}

				if (itemstack1.isEmpty()) {
					slot.putStack(ItemStack.EMPTY);
				} else {
					slot.onSlotChanged();
				}

				if (itemstack1.getCount() == itemstack.getCount()) {
					return ItemStack.EMPTY;
				}
				ItemStack itemstack2 = slot.onTake(playerIn, itemstack1);

				if (index == 0) {
					playerIn.dropItem(itemstack2, false);
				}
			}
		}

		return itemstack;
	}

	//private void unequipBauble(ItemStack stack) { }

	@Override
	public boolean canMergeSlot(ItemStack stack, Slot slot)
	{
		return slot.inventory != this.craftResult && super.canMergeSlot(stack, slot);
	}
}
