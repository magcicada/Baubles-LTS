package baubles.common.container;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.BaublesContainer;
import baubles.api.cap.IBaublesItemHandler;
import baubles.common.Baubles;
import baubles.common.Config;
import baubles.common.event.BaubleEquipmentChangeEvent;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nullable;

public class SlotBauble extends SlotItemHandler
{
	int baubleSlot;

	BaubleType baubleType;

	private String identifier;
	EntityPlayer player;



	public SlotBauble(EntityPlayer player, IBaublesItemHandler itemHandler, int slot, int xPosition, int yPosition)
	{
		super(itemHandler, slot, xPosition, yPosition);
		this.baubleSlot = slot;
		this.player = player;
	}
	public SlotBauble(EntityPlayer player, IBaublesItemHandler itemHandler, int slot, String identifier, int xPosition, int yPosition) {
		super(itemHandler, slot, xPosition, yPosition);
		this.identifier = identifier;
		this.player = player;
	}

	@SideOnly(Side.CLIENT)
	public String getSlotName() {
		String key = "baubles.identifier." + identifier;
		if (!I18n.hasKey(key)) {
			return identifier.substring(0, 1).toUpperCase() + identifier.substring(1);
		}
		return I18n.format(key);
	}

	/**
	 * Check if the stack is a valid item for this slot.
	 */
	@Override
	public boolean isItemValid(ItemStack stack)
	{
		return ((IBaublesItemHandler)getItemHandler()).isItemValidForSlot(baubleSlot, stack, player);
	}

	@Override
	public boolean canTakeStack(EntityPlayer player) {
		ItemStack stack = getStack();
		if(stack.isEmpty())
			return false;

		IBauble bauble = stack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);
		return bauble.canUnequip(stack, player);
	}

	@Override
	public ItemStack onTake(EntityPlayer playerIn, ItemStack stack) {
		if (!getHasStack() && !((IBaublesItemHandler)getItemHandler()).isEventBlocked() &&
				stack.hasCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null)) {
			stack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null).onUnequipped(stack, playerIn);
		}
		super.onTake(playerIn, stack);
		MinecraftForge.EVENT_BUS.post(new BaubleEquipmentChangeEvent(playerIn, baubleSlot, stack, ItemStack.EMPTY));
		return stack;
	}

	@Override
	public void putStack(ItemStack stack) {
		ItemStack oldstack = getStack().copy();

		if (getHasStack() && !ItemStack.areItemStacksEqual(stack,getStack()) &&
				!((IBaublesItemHandler)getItemHandler()).isEventBlocked() &&
				getStack().hasCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null)) {
			getStack().getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null).onUnequipped(getStack(), player);
		}

		if (getHasStack() && !ItemStack.areItemStacksEqual(oldstack,getStack())
				&& !((IBaublesItemHandler)getItemHandler()).isEventBlocked() &&
				getStack().hasCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null)) {
			getStack().getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null).onEquipped(getStack(), player);
		}

		if (!ItemStack.areItemStacksEqual(oldstack,getStack())){
			MinecraftForge.EVENT_BUS.post(new BaubleEquipmentChangeEvent(player, baubleSlot, oldstack, getStack()));
		}

		super.putStack(stack);
	}

	@Override
	public int getSlotStackLimit()
	{
		return 1;
	}

	@Nullable
	@SideOnly(Side.CLIENT)
	@Override
	public String getSlotTexture() {
		if (baubleType != null) {
			return BaublesApi.getIcons().get(baubleType.toString().toLowerCase()).toString();
		}
		return super.getSlotTexture();
	}
}
