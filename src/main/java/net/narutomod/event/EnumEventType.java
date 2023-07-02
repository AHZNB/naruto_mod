package net.narutomod.event;

import java.util.Map;
import com.google.common.collect.Maps;

public enum EnumEventType {
	NO_EVENT(0), 
	CYLINDRICAL_EXPLOSION(1), 
	SPHERICAL_EXPLOSION(2), 
	DELAYED_SPAWN(3), 
	VILLAGE_SIEGE(4),
	//METEOR_SHOWER(5),
	SET_BLOCKS(6),
	VANILLA_EXPLOSION(7),
	DELAYED_CALLBACK(8),
	SOMETHING_ELSE(-1);
		
	private final int index;
	private static final Map<Integer, EnumEventType> EVENT_TYPES = Maps.newHashMap();
		
	private EnumEventType(int idx) {
		this.index = idx;
	}

	public int getIndex() {
		return this.index;
	}

	public static EnumEventType getTypeFromIndex(int index) {
		return EVENT_TYPES.get(Integer.valueOf(index));
	}
		
	static {
		for (EnumEventType type : values())
			EVENT_TYPES.put(Integer.valueOf(type.getIndex()), type);
	}
}
