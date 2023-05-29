
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import net.narutomod.item.ItemBakuton;
import net.narutomod.item.ItemJutsu;
import net.narutomod.ElementsNarutomodMod;

//import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntityC1 extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 231;
	public static final int ENTITYID_RANGED = 232;

	public EntityC1(ElementsNarutomodMod instance) {
		super(instance, 544);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
		 .id(new ResourceLocation("narutomod", "c_1"), ENTITYID).name("c_1").tracker(64, 3, true).build());
	}

	public static class EC extends ItemBakuton.ExplosiveClay {
		public EC(World world) {
			super(world);
			this.setSize(0.4F, 0.8F);
		}

		public EC(EntityLivingBase ownerIn) {
			super(ownerIn);
			this.setSize(0.4F, 0.8F);
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(14.0D);
			this.getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue(0.4D);
		}

	    @Override
	    public boolean attackEntityAsMob(Entity entityIn) {
	    	EntityLivingBase owner = this.getOwner();
	    	if (!this.world.isRemote) {
		    	this.world.createExplosion(owner, entityIn.posX, entityIn.posY, entityIn.posZ,
			     4f, net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, owner));
	    		this.setDead();
	    	}
	    	entityIn.hurtResistantTime = 10;
	    	return entityIn.attackEntityFrom(ItemJutsu.causeJutsuDamage(this, owner), 8f + this.rand.nextFloat() * 4f);
	    }

	    /*public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float powerIn) {
				EC c1 = new EC(entity);
				Vec3d vec = entity.getLookVec();
				vec = entity.getPositionVector().addVector(vec.x, 1d, vec.z);
				c1.setPosition(vec.x, vec.y, vec.z);
				entity.world.spawnEntity(c1);
				return true;
			}
	    }*/
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		new Renderer().register();
	}

	public static class Renderer extends EntityRendererRegister {
		@SideOnly(Side.CLIENT)
		@Override
		public void register() {
			RenderingRegistry.registerEntityRenderingHandler(EC.class, renderManager -> new RenderCustom(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderCustom extends RenderBiped<EC> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/vex1.png");
	
			public RenderCustom(RenderManager renderManagerIn) {
				super(renderManagerIn, new ModelCustom(), 0.3F);
			}
	
			@Override
			protected void preRenderCallback(EC entity, float partialTickTime) {
				GlStateManager.scale(0.4F, 0.4F, 0.4F);
			}
	
			@Override
			protected ResourceLocation getEntityTexture(EC entity) {
				return this.texture;
			}
		}
		
		@SideOnly(Side.CLIENT)
		public class ModelCustom extends ModelBiped {
		    protected ModelRenderer leftWing;
		    protected ModelRenderer rightWing;
		
		    public ModelCustom() {
		        this(0.0F);
		    }
		
		    public ModelCustom(float p_i47224_1_) {
		        super(p_i47224_1_, 0.0F, 64, 64);
		        this.bipedLeftLeg.showModel = false;
		        this.bipedHeadwear.showModel = false;
		        this.bipedRightLeg = new ModelRenderer(this, 32, 0);
		        this.bipedRightLeg.addBox(-1.0F, -1.0F, -2.0F, 6, 10, 4, 0.0F);
		        this.bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
		        this.rightWing = new ModelRenderer(this, 0, 32);
		        this.rightWing.addBox(-20.0F, 0.0F, 0.0F, 20, 12, 1);
		        this.leftWing = new ModelRenderer(this, 0, 32);
		        this.leftWing.mirror = true;
		        this.leftWing.addBox(0.0F, 0.0F, 0.0F, 20, 12, 1);
		    }
		
		    @Override
		    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		        super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
		        this.rightWing.render(scale);
		        this.leftWing.render(scale);
		    }
		
		    @Override
		    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
		        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
		        this.bipedRightLeg.rotateAngleX += ((float)Math.PI / 5F);
		        this.rightWing.rotationPointZ = 2.0F;
		        this.leftWing.rotationPointZ = 2.0F;
		        this.rightWing.rotationPointY = 1.0F;
		        this.leftWing.rotationPointY = 1.0F;
		        this.rightWing.rotateAngleY = 0.47123894F + MathHelper.cos(ageInTicks * 0.8F) * (float)Math.PI * 0.05F;
		        this.leftWing.rotateAngleY = -this.rightWing.rotateAngleY;
		        this.leftWing.rotateAngleZ = -0.47123894F;
		        this.leftWing.rotateAngleX = 0.47123894F;
		        this.rightWing.rotateAngleX = 0.47123894F;
		        this.rightWing.rotateAngleZ = 0.47123894F;
		    }
		}
	}
}
