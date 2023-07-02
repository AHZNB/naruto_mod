
package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ActionResult;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumActionResult;

import net.narutomod.gui.GuiTeamManager;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.PlayerTracker;
import net.narutomod.NarutomodMod;
import net.narutomod.ElementsNarutomodMod;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Collection;

@ElementsNarutomodMod.ModElement.Tag
public class ItemTeamScroll extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:team_scroll")
	public static final Item block = null;
	public ItemTeamScroll(ElementsNarutomodMod instance) {
		super(instance, 555);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new ItemCustom());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:team_scroll", "inventory"));
	}

	public static class ItemCustom extends Item {
		public ItemCustom() {
			this.setMaxDamage(0);
			this.maxStackSize = 1;
			this.setUnlocalizedName("team_scroll");
			this.setRegistryName("team_scroll");
			this.setCreativeTab(TabModTab.tab);
		}

		@Override
		public int getItemEnchantability() {
			return 0;
		}

		@Override
		public int getMaxItemUseDuration(ItemStack itemstack) {
			return 0;
		}

		@Override
		public float getDestroySpeed(ItemStack par1ItemStack, IBlockState par2Block) {
			return 0F;
		}

		@Override
		public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer entity, EnumHand hand) {
			ItemStack stack = entity.getHeldItem(hand);
			if (entity.isCreative() || PlayerTracker.isNinja(entity)) {
				int x = (int) entity.posX;
				int y = (int) entity.posY;
				int z = (int) entity.posZ;
				entity.openGui(NarutomodMod.instance, GuiTeamManager.GUIID, world, x, y, z);
				return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
			}
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
		}

		@Nullable
		private static ScorePlayerTeam getTeamFromItem(World world, ItemStack itemstack) {
			ScorePlayerTeam team = null;
			Scoreboard scoreboard = world.getScoreboard();
			if (itemstack.hasTagCompound() && itemstack.getTagCompound().hasKey("teamName")) {
				NBTTagCompound compound = itemstack.getTagCompound();
				String teamName = compound.getString("teamName");
				String teamDisplayName = compound.getString("teamDisplayName");
				team = scoreboard.getTeam(teamName);
				if (team == null && !world.isRemote) {
					team = scoreboard.createTeam(teamName);
				}
				if (team != null) {
					if (teamDisplayName != null && !team.getDisplayName().equals(teamDisplayName)) {
						team.setDisplayName(teamDisplayName);
					}
					if (compound.hasKey("teamMembers", 9)) {
						NBTTagList taglist = compound.getTagList("teamMembers", 10);
						int[] shouldRemove = new int[taglist.tagCount()];
						int i = 0;
						for (int j = 0; j < taglist.tagCount(); ++j) {
							NBTTagCompound compound2 = taglist.getCompoundTagAt(j);
							if (compound2.hasUniqueId("memberUUID")) {
								EntityPlayer member = world.getPlayerEntityByUUID(compound2.getUniqueId("memberUUID"));
								if (member != null) {
									ScorePlayerTeam memberTeam = scoreboard.getPlayersTeam(member.getName());
									if (memberTeam == null && !team.getMembershipCollection().contains(member.getName())) {
										scoreboard.addPlayerToTeam(member.getName(), team.getName());
									} else if (memberTeam != null && !team.isSameTeam(memberTeam)) {
										shouldRemove[i++] = j;
									}
								}
							}
						}
						if (i > 0) {
							for (int j = i - 1; j >= 0; j--) {
								taglist.removeTag(shouldRemove[j]);
							}
							compound.setTag("teamMembers", taglist);
						}
					}
				}
			}
			return team;
		}

		@Nullable
		public static ScorePlayerTeam getOrCreateTeam(World world, ItemStack stack, String teamName) {
			if (!stack.hasTagCompound()) {
				stack.setTagCompound(new NBTTagCompound());
			}
			if (!stack.getTagCompound().hasKey(teamName)) {
				Scoreboard scoreboard = world.getScoreboard();
				ScorePlayerTeam team = scoreboard.getTeam(teamName);
				if (team == null && !world.isRemote) {
					team = scoreboard.createTeam(teamName);
				}
				if (team != null) {
					stack.getTagCompound().setString("teamName", teamName);
					stack.getTagCompound().setString("teamDisplayName", teamName);
				}
				return team;
			} else {
				return getTeamFromItem(world, stack);
			}
		}

		public static void addTeamMember(ItemStack stack, EntityPlayer player) {
			ScorePlayerTeam team = getTeamFromItem(player.world, stack);
			if (team == null) {
				team = getOrCreateTeam(player.world, stack, player.getName());
			}
			if (team != null && !team.getMembershipCollection().contains(player.getName())) {
				if (!player.world.isRemote) {
					player.world.getScoreboard().addPlayerToTeam(player.getName(), team.getName());
				}
				boolean flag = true;
				NBTTagCompound compound = stack.getTagCompound();
				NBTTagList taglist = compound.hasKey("teamMembers", 9) ? compound.getTagList("teamMembers", 10) : new NBTTagList();
				for (int i = 0; i < taglist.tagCount(); i++) {
					NBTTagCompound compound3 = taglist.getCompoundTagAt(i);
					if (compound3.hasUniqueId("memberUUID") && compound3.getUniqueId("memberUUID").equals(player.getUniqueID())) {
						flag = false;
						break;
					}
				}
				if (flag) {
					NBTTagCompound compound2 = new NBTTagCompound();
					compound2.setUniqueId("memberUUID", player.getUniqueID());
					taglist.appendTag(compound2);
					compound.setTag("teamMembers", taglist);
				}
			}
		}

		public static void removeTeamMember(ItemStack stack, EntityPlayer player) {
			ScorePlayerTeam team = getTeamFromItem(player.world, stack);
			if (team != null && team.getMembershipCollection().contains(player.getName())) {
				if (!player.world.isRemote) {
					player.world.getScoreboard().removePlayerFromTeam(player.getName(), team);
				}
				if (stack.getTagCompound().hasKey("teamMembers", 9)) {
					NBTTagList taglist = stack.getTagCompound().getTagList("teamMembers", 10);
					boolean flag = false;
					for (int i = 0; i < taglist.tagCount(); i++) {
						NBTTagCompound compound3 = taglist.getCompoundTagAt(i);
						if (compound3.hasUniqueId("memberUUID") && compound3.getUniqueId("memberUUID").equals(player.getUniqueID())) {
							taglist.removeTag(i);
							flag = true;
							break;
						}
					}
					if (flag) {
						stack.getTagCompound().setTag("teamMembers", taglist);
					}
				}
			}
		}

		public static Collection<String> getTeamMembers(World world, ItemStack stack) {
			ScorePlayerTeam team = getTeamFromItem(world, stack);
			return team != null ? team.getMembershipCollection() : Collections.EMPTY_LIST;
		}

		public static String getTeamDisplayName(World world, ItemStack stack) {
			ScorePlayerTeam team = getTeamFromItem(world, stack);
			return team != null ? team.getDisplayName() : "";
		}

		public static void setTeamDisplayName(World world, ItemStack stack, String newName) {
			ScorePlayerTeam team = getTeamFromItem(world, stack);
			if (team != null) {
				if (!world.isRemote) {
					team.setDisplayName(newName);
				}
				stack.getTagCompound().setString("teamDisplayName", newName);
			}
		}
	}
}
