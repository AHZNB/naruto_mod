
package net.narutomod.potion;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.EnumFacing;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.Minecraft;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.Potion;
import net.minecraft.block.Block;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.procedure.ProcedureAirPunch;
//import net.narutomod.item.ItemIryoJutsu;
import net.narutomod.Chakra;
import net.narutomod.Particles;
import net.narutomod.ElementsNarutomodMod;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;

@ElementsNarutomodMod.ModElement.Tag
public class PotionChakraEnhancedStrength extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:chakra_enhanced_strength")
	public static final Potion potion = null;
	public static boolean shouldShowInHUD = true;

	public PotionChakraEnhancedStrength(ElementsNarutomodMod instance) {
		super(instance, 529);
	}

	@Override
	public void initElements() {
		elements.potions.add(() -> new PotionCustom());
	}

	public static class PotionCustom extends Potion {
		private final ResourceLocation potionIcon;
		public PotionCustom() {
			super(false, 0x0033FFCC);
			setBeneficial();
			setRegistryName("chakra_enhanced_strength");
			setPotionName("effect.chakra_enhanced_strength");
			potionIcon = new ResourceLocation("narutomod:textures/strength.png");
			//this.registerPotionAttributeModifier(SharedMonsterAttributes.ATTACK_SPEED, "97405670-df28-4a45-bf9a-7aba911051a6", 0.1d, 2);
			//this.registerPotionAttributeModifier(SharedMonsterAttributes.ATTACK_DAMAGE, "6fec22d7-dbdf-4928-a4d5-00dea8b96939", 1d, 0);
		}

		@Override
		public boolean isInstant() {
			return true;
		}

		@Override
		public boolean shouldRenderInvText(PotionEffect effect) {
			return false;
		}

		@Override
		public boolean shouldRenderHUD(PotionEffect effect) {
			return true;
		}

		@SideOnly(Side.CLIENT)
		@Override
		public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
			if (mc.currentScreen != null) {
				mc.getTextureManager().bindTexture(potionIcon);
				Gui.drawModalRectWithCustomSizedTexture(x + 6, y + 7, 0, 0, 18, 18, 18, 18);
			}
		}

		@SideOnly(Side.CLIENT)
		@Override
		public void renderHUDEffect(int x, int y, PotionEffect effect, Minecraft mc, float alpha) {
			mc.getTextureManager().bindTexture(potionIcon);
			Gui.drawModalRectWithCustomSizedTexture(x + 3, y + 3, 0, 0, 18, 18, 18, 18);
		}

		@Override
		public boolean isReady(int duration, int amplifier) {
			return true;
		}
	}

	public class EntityHook {
		public class Punch extends ProcedureAirPunch {
			private final boolean griefing;

			public Punch(World world) {
				this.blockDropChance = 0.1F;
				this.griefing = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(world, null);
			}

			@Override
			protected void preExecuteParticles(EntityLivingBase player) {
				Vec3d vec = player.getLookVec();
				Vec3d vec1 = player.getPositionVector().addVector(0d, 1.2d, 0d).add(vec);
				Vec3d vec2 = vec.scale(this.getRange(0));
				double d = MathHelper.atan2(this.getFarRadius(0), this.getRange(0));
				for (int i = 0; i < (int)(this.getRange(0) * 50); i++) {
					//Vec3d vec3 = vec2.scale((this.rand.nextDouble() * 0.17d) + 0.03d)
					 //.addVector((this.rand.nextDouble() - 0.5d) * this.getFarRadius(0) * 0.15d,
					 //(this.rand.nextDouble() - 0.5d) * this.getFarRadius(0) * 0.15d, 
					 //(this.rand.nextDouble() - 0.5d) * this.getFarRadius(0) * 0.15d);
					Vec3d vec3 = vec2.scale((this.rand.nextDouble() * 0.05d) + 0.2d)
					 .rotatePitch((float)(this.rand.nextGaussian() * d))
					 .rotateYaw((float)(this.rand.nextGaussian() * d));
					Particles.spawnParticle(player.world, Particles.Types.SMOKE, vec1.x, vec1.y, vec1.z,
					 1, 0d, 0d, 0d, vec3.x, vec3.y, vec3.z, 0x20ffffff, (int)this.getRange(0) * 2 + this.rand.nextInt(21), 12);
				}
				for (int i = 1, j = (int)(this.getRange(0) * 2.5d); i <= j; i++) {
					Vec3d vec3 = vec2.scale(-0.0012d * i);
					Particles.spawnParticle(player.world, Particles.Types.SONIC_BOOM, vec1.x, vec1.y, vec1.z,
					 1, 0d, 0d, 0d, vec3.x, vec3.y, vec3.z, 0x00ffffff | ((int)((1f-(float)i/j)*0x40)<<24), i,
					 (int)(5f * (1f + ((float)i/j) * 0.5f)));
				}
			}

			@Override
			protected EntityItem processAffectedBlock(EntityLivingBase player, BlockPos pos, EnumFacing facing) {
				if (this.griefing && player.world.getBlockState(pos).isFullBlock()
				 && player.world.getBlockState(pos.up()).getCollisionBoundingBox(player.world, pos.up()) == Block.NULL_AABB) {
					EntityFallingBlock entity = new EntityFallingBlock(player.world, 0.5d+pos.getX(), pos.getY(), 0.5d+pos.getZ(), player.world.getBlockState(pos));
					entity.motionY = 0.45d;
					player.world.spawnEntity(entity);
				}
				return super.processAffectedBlock(player, pos, facing);
			}

			@Override
			protected float getBreakChance(BlockPos pos, EntityLivingBase player, double range) {
				return player.getActivePotionEffect(potion).getIsAmbient()
				//return player instanceof EntityPlayer 
				 ? 1.0F - (float)((Math.sqrt(player.getDistanceSqToCenter(pos)) - 4.0D) / range)
				 : 0.0F;
			}
		}

		@SubscribeEvent
		public void onLivingHurt(LivingHurtEvent event) {
			if (event.getSource().getImmediateSource() instanceof EntityLivingBase && !event.getSource().isExplosion()
			 && event.getSource() instanceof EntityDamageSource && !((EntityDamageSource)event.getSource()).getIsThornsDamage()) {
				EntityLivingBase attacker = (EntityLivingBase)event.getSource().getImmediateSource();
				if (attacker.isPotionActive(potion)) {
					int amplifier = attacker.getActivePotionEffect(potion).getAmplifier();
					if (Chakra.pathway(attacker).consume((double)amplifier)) {
						EntityLivingBase target = event.getEntityLiving();
						target.world.playSound(null, target.posX, target.posY, target.posZ, SoundEvents.ENTITY_GENERIC_EXPLODE,
						  SoundCategory.BLOCKS, 1.0F, (1.0F + (target.getRNG().nextFloat() - target.getRNG().nextFloat()) * 0.2F) * 0.7F);
						new Punch(attacker.world).execute(attacker, (double)amplifier * 0.4d, 0.1d * amplifier);
						//ProcedureUtils.pushEntity(attacker, target, 10d, 0.1f * amplifier);
						event.setAmount(event.getAmount() + amplifier);
					}
				}
			}
		}

		@SubscribeEvent
		public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
			EntityPlayer player = event.getEntityPlayer();
			if (player.isPotionActive(potion)) {
				PotionEffect eff = player.getActivePotionEffect(potion);
				if (eff.getIsAmbient()) {
					int amplifier = eff.getAmplifier();
					if (Chakra.pathway(player).consume(0.05d * amplifier)) {
						event.setNewSpeed(event.getOriginalSpeed() * (1f + 0.15f * amplifier));
					}
				}
			}
		}

		@SubscribeEvent
		@SideOnly(Side.CLIENT)
		public void onHUDRender(RenderGameOverlayEvent event) {
			if (event.getType() == RenderGameOverlayEvent.ElementType.POTION_ICONS && !event.isCancelable()) {
				Minecraft mc = Minecraft.getMinecraft();
				if (mc.player.isPotionActive(potion)) {
					PotionEffect eff = mc.player.getActivePotionEffect(potion);
					if (!eff.doesShowParticles()) {
						int i = 0;
						for (PotionEffect effect : mc.player.getActivePotionEffects()) {
							Potion potion = effect.getPotion();
							if (potion.shouldRenderHUD(effect) && effect.doesShowParticles() && potion.isBeneficial()) {
								++i;
							}
						}
						int l = 1;
						++i;
						int k = event.getResolution().getScaledWidth();
						if (mc.isDemo()) {
							l += 15;
						}
						int i1 = eff.getPotion().getStatusIconIndex();
						k = k - 25 * i;
						mc.getTextureManager().bindTexture(GuiContainer.INVENTORY_BACKGROUND);
						GlStateManager.enableBlend();
						GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
						mc.ingameGUI.drawTexturedModalRect(k, l, 165, 166, 24, 24);
						mc.ingameGUI.drawTexturedModalRect(k + 3, l + 3, i1 % 8 * 18, 198 + i1 / 8 * 18, 18, 18);
						eff.getPotion().renderHUDEffect(k, l, eff, mc, 1f);
					}
				}
			}
		}
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new EntityHook());
	}
}
