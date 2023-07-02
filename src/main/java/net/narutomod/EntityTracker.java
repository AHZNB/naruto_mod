/**
 * This mod element is always locked. Enter your code in the methods below.
 * If you don't need some of these methods, you can remove them as they
 * are overrides of the base class ElementsNarutomodMod.ModElement.
 *
 * You can register new events in this class too.
 *
 * As this class is loaded into mod element list, it NEEDS to extend
 * ModElement class. If you remove this extend statement or remove the
 * constructor, the compilation will fail.
 *
 * If you want to make a plain independent class, create it in
 * "Workspace" -> "Source" menu.
 *
 * If you change workspace package, modid or prefix, you will need
 * to manually adapt this file to these changes or remake it.
*/
package net.narutomod;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.Map;
import java.util.UUID;
import java.util.Iterator;
import com.google.common.collect.Maps;

@ElementsNarutomodMod.ModElement.Tag
public class EntityTracker extends ElementsNarutomodMod.ModElement {
	private static final Map<UUID, SessionDataHolder> entityMap = Maps.newHashMap();

	/**
	 * Do not remove this constructor
	 */
	public EntityTracker(ElementsNarutomodMod instance) {
		super(instance, 532);
	}

	public static SessionDataHolder getOrCreate(Entity entity) {
		SessionDataHolder p = entityMap.get(entity.getUniqueID());
		if (p == null || !p.entity.isAddedToWorld()) {
			if (p != null) {
				p.remove();
			}
			p = new SessionDataHolder(entity);
		}
		return p;
	}

	public static void clearRemovedData() {
		Iterator<SessionDataHolder> iter = entityMap.values().iterator();
		while (iter.hasNext()) {
			if (!iter.next().entity.isAddedToWorld()) {
				iter.remove();
			}
		}
	}

	public static int trackingTotal() {
		return entityMap.size();
	}

	public static class SessionDataHolder {
		public final Entity entity;
		public BlockPos prevBlockPos;
		public AxisAlignedBB lastBB;
		public int lastLoggedXpTime;

		public SessionDataHolder(Entity entityIn) {
			this.entity = entityIn;
			entityMap.put(entityIn.getUniqueID(), this);
		}

		public static SessionDataHolder get(Entity entityIn) {
			return entityMap.get(entityIn.getUniqueID());
		}

		public void remove() {
			entityMap.remove(this.entity.getUniqueID());
		}

		public void saveBB() {
			this.lastBB = this.entity.getEntityBoundingBox();
		}

		public double lastPosX() {
			return this.lastBB.minX + (this.lastBB.maxX - this.lastBB.minX) / 2;
		}

		public double lastPosY() {
			return this.lastBB.minY;
		}

		public double lastPosZ() {
			return this.lastBB.minZ + (this.lastBB.maxZ - this.lastBB.minZ) / 2;
		}

		@Override
		public String toString() {
			return "lastBB:"+this.lastBB;
		}
	}
}
