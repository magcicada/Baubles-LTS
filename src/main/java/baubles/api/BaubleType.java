package baubles.api;

import io.netty.util.internal.ConcurrentSet;
import net.minecraft.util.ResourceLocation;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public enum BaubleType {
	AMULET(0),
	RING(1,2),
	BELT(3),
	TRINKET(0,1,2,3,4,5,6),
	HEAD(4),
	BODY(5),
	CHARM(6);

	final int[] validSlots;

	BaubleType(int ... validSlots) {
		this.validSlots = validSlots;
	}

	public boolean hasSlot(int slot) {
		for (int s : validSlots) {
			if (s == slot) return true;
		}
		return false; 
	}

	public int[] getValidSlots() {
		return validSlots;
	}

	static ConcurrentMap<String, ConcurrentSet<ResourceLocation>> iconQueues = new ConcurrentHashMap<>();

	static Map<String, ResourceLocation> icons = new HashMap<>();

	public static void processIcons() {

		if (!icons.isEmpty()) {
			icons = new HashMap<>();
		}
		iconQueues.forEach((k, v) -> {

			if (!icons.containsKey(k)) {
				List<ResourceLocation> sortedList = new ArrayList<>(v);
				Collections.sort(sortedList);
				icons.put(k, sortedList.get(sortedList.size() - 1));
			}
		});
	}
}
