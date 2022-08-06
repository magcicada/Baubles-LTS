package baubles.api.cap;

import baubles.api.IBauble;
import baubles.common.Config;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.ItemStackHandler;

public class BaublesContainer extends ItemStackHandler implements IBaublesItemHandler {

	private final static int BAUBLE_SLOTS = 7;
	private boolean[] changed = new boolean[BAUBLE_SLOTS];
	private boolean blockEvents=false;
	private EntityLivingBase player;

	protected NonNullList<ItemStack> previousStacks;

	protected boolean isHidden = false;

	public BaublesContainer()
	{
		super(BAUBLE_SLOTS);
	}

	public BaublesContainer(int size) {
		this.setSize(size);
	}

	public BaublesContainer(NonNullList<ItemStack> stacks) {
		this.stacks = stacks;
		this.previousStacks = NonNullList.create();

		for (int i = 0; i < stacks.size(); i++) {
			previousStacks.add(ItemStack.EMPTY);
		}
	}

	@Override
	public void setSize(int size)
	{
		if (!Config.useCurioGUI) {
			if (size < BAUBLE_SLOTS) size = BAUBLE_SLOTS;
			super.setSize(size);
			boolean[] old = changed;
			changed = new boolean[size];
			for (int i = 0; i < old.length && i < changed.length; i++) {
				changed[i] = old[i];
			}
		} else {
			this.stacks = NonNullList.create();
			this.previousStacks = NonNullList.create();

			for (int i = 0; i < size; i++) {
				this.stacks.add(ItemStack.EMPTY);
				this.previousStacks.add(ItemStack.EMPTY);
			}
		}
	}

	/**
	 * Returns true if automation is allowed to insert the given stack (ignoring
	 * stack size) into the given slot.
	 */
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack, EntityLivingBase player) {
		if (stack == null || stack.isEmpty() || !stack.hasCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null)) {
			return false;
		}
		IBauble bauble = stack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);
		return bauble.canEquip(stack, player) && bauble.getBaubleType(stack).hasSlot(slot);
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		if (stack==null || stack.isEmpty() || this.isItemValidForSlot(slot, stack, player)) {
			super.setStackInSlot(slot, stack);
		}
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if (!this.isItemValidForSlot(slot, stack, player)) {
			return stack;
		}
		return super.insertItem(slot, stack, simulate);
	}

	@Override
	public boolean isEventBlocked() {
		return blockEvents;
	}

	@Override
	public void setEventBlock(boolean blockEvents) {
		this.blockEvents = blockEvents;
	}

	@Override
	protected void onContentsChanged(int slot) {
		setChanged(slot,true);
	}

	@Override
	public boolean isChanged(int slot) {
		if (changed == null) {
			changed = new boolean[this.getSlots()];
		}
		return changed[slot];
	}

	@Override
	public void setChanged(int slot, boolean change) {
		if (changed == null) {
			changed = new boolean[this.getSlots()];
		}
		this.changed[slot] = change;
	}

	@Override
	public void setPlayer(EntityLivingBase player) {
		this.player = player;
	}

	public void addSize(int amount) {

		if (amount < 0) {
			throw new IllegalArgumentException("Amount cannot be negative!");
		}

		for (int i = 0; i < amount; i++) {
			this.stacks.add(ItemStack.EMPTY);
			this.previousStacks.add(ItemStack.EMPTY);
		}
	}

	public void removeSize(int amount) {

		if (amount < 0) {
			throw new IllegalArgumentException("Amount cannot be negative!");
		}
		int targetSize = this.stacks.size() - amount;

		while (this.stacks.size() > targetSize) {
			this.stacks.remove(this.stacks.size() - 1);
		}

		while (this.previousStacks.size() > targetSize) {
			this.previousStacks.remove(this.previousStacks.size() - 1);
		}
	}

	public boolean isHidden() {
		return isHidden;
	}

	public void setHidden(boolean hidden) {
		isHidden = hidden;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound compound = super.serializeNBT();
		compound.setBoolean("Hidden", isHidden);
		return compound;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		this.isHidden = nbt.hasKey("Hidden", Constants.NBT.TAG_BYTE) && nbt.getBoolean("Hidden");
		super.deserializeNBT(nbt);
	}
}
