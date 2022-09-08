//Made with Blockbench
//Paste this code into your mod.

public static class ModelLightningGod extends ModelBase {
	private final ModelRenderer helmet;
	private final ModelRenderer bodywear;
	private final ModelRenderer rightArmwear;
	private final ModelRenderer leftArmwear;
	private final ModelRenderer rightLegwear;
	private final ModelRenderer leftLegwear;
	private final ModelRenderer eyes;
	private final ModelRenderer aura1;
	private final ModelRenderer aura2;
	private final ModelRenderer modelrenderer;

	public ModelLightningGod() {
		textureWidth = 64;
		textureHeight = 64;

		helmet = new ModelRenderer(this);
		helmet.setRotationPoint(0.0F, 0.0F, 0.0F);
		helmet.cubeList.add(new ModelBox(helmet, 0, 0, -4.0F, -8.0F, -4.0F, 8,
				8, 8, 0.0F, false));

		bodywear = new ModelRenderer(this);
		bodywear.setRotationPoint(0.0F, 0.0F, 0.0F);
		bodywear.cubeList.add(new ModelBox(bodywear, 0, 48, -4.0F, 0.0F, -2.0F,
				8, 12, 4, 0.0F, false));

		rightArmwear = new ModelRenderer(this);
		rightArmwear.setRotationPoint(-5.0F, 2.0F, 0.0F);
		rightArmwear.cubeList.add(new ModelBox(rightArmwear, 0, 16, -3.0F,
				-2.0F, -2.0F, 4, 12, 4, 0.0F, false));

		leftArmwear = new ModelRenderer(this);
		leftArmwear.setRotationPoint(5.0F, 2.0F, 0.0F);
		leftArmwear.cubeList.add(new ModelBox(leftArmwear, 0, 16, -1.0F, -2.0F,
				-2.0F, 4, 12, 4, 0.0F, true));

		rightLegwear = new ModelRenderer(this);
		rightLegwear.setRotationPoint(-1.9F, 12.0F, 0.0F);
		rightLegwear.cubeList.add(new ModelBox(rightLegwear, 0, 32, -2.0F,
				0.0F, -2.0F, 4, 12, 4, 0.0F, false));

		leftLegwear = new ModelRenderer(this);
		leftLegwear.setRotationPoint(1.9F, 12.0F, 0.0F);
		leftLegwear.cubeList.add(new ModelBox(leftLegwear, 0, 32, -2.0F, 0.0F,
				-2.0F, 4, 12, 4, 0.0F, true));

		eyes = new ModelRenderer(this);
		eyes.setRotationPoint(0.0F, 0.0F, -0.2F);
		eyes.cubeList.add(new ModelBox(eyes, 26, 0, -3.0F, -5.0F, -3.9F, 6, 1,
				0, 0.0F, false));

		aura1 = new ModelRenderer(this);
		aura1.setRotationPoint(0.0F, 24.0F, 0.0F);
		aura1.cubeList.add(new ModelBox(aura1, 20, 16, -4.0F, -32.0F, -2.0F, 8,
				32, 4, 2.0F, false));

		aura2 = new ModelRenderer(this);
		aura2.setRotationPoint(0.0F, 24.0F, 0.0F);
		aura2.cubeList.add(new ModelBox(aura2, 40, 0, -4.0F, -32.0F, -2.0F, 8,
				32, 4, 2.0F, false));

		modelrenderer = new ModelRenderer(this);
		modelrenderer.setRotationPoint(0.0F, 0.0F, 0.0F);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3,
			float f4, float f5) {
		float scale = 4f;
		GlStateManager.pushMatrix();
		GlStateManager.scale(scale, scale, scale);
		GlStateManager.translate(0.0F, -1.0F, 0.0F);
		helmet.render(f5);
		bodywear.render(f5);
		rightArmwear.render(f5);
		leftArmwear.render(f5);
		rightLegwear.render(f5);
		leftLegwear.render(f5);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,
				240f, 240f);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 0.8F);
		GL11.glDisable(GL11.GL_LIGHTING);
		GlStateManager.enableAlpha();
		eyes.render(f5);
		if (Math.random() < 0.5F)
			aura1.render(f5);
		else
			aura2.render(f5);
		GlStateManager.disableAlpha();
		GL11.glEnable(GL11.GL_LIGHTING);
		GlStateManager.popMatrix();
	}
	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y,
			float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
	public void setRotationAngles(float limbSwing, float limbSwingAmount,
			float ageInTicks, float netHeadYaw, float headPitch,
			float scaleFactor, Entity entityIn) {
		super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks,
				netHeadYaw, headPitch, scaleFactor, entityIn);
		leftLegwear.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * -1.0F
				* limbSwingAmount;
		rightArmwear.rotateAngleX = MathHelper.cos(limbSwing * 0.3331F
				+ (float) Math.PI)
				* limbSwingAmount;
		helmet.rotateAngleY = netHeadYaw / (180F / (float) Math.PI);
		helmet.rotateAngleX = headPitch / (180F / (float) Math.PI);
		eyes.rotateAngleY = netHeadYaw / (180F / (float) Math.PI);
		eyes.rotateAngleX = headPitch / (180F / (float) Math.PI);
		rightLegwear.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.0F
				* limbSwingAmount;
		leftArmwear.rotateAngleX = MathHelper.cos(limbSwing * 0.3331F)
				* limbSwingAmount;
		rightArmwear.rotateAngleZ = 0.0F;
		leftArmwear.rotateAngleZ = 0.0F;

		if (this.swingProgress > 0.0F) {
			EnumHandSide enumhandside = this.getMainHand(entityIn);
			ModelRenderer modelrenderer = this.getArmForSide(enumhandside);
			float f1 = this.swingProgress;
			f1 = 1.0F - this.swingProgress;
			f1 = f1 * f1;
			f1 = f1 * f1;
			f1 = 1.0F - f1;
			float f2 = MathHelper.sin(f1 * (float) Math.PI);
			float f3 = MathHelper.sin(this.swingProgress * (float) Math.PI)
					* -(helmet.rotateAngleX - 0.7F) * 0.75F;
			modelrenderer.rotateAngleX = (float) ((double) modelrenderer.rotateAngleX - ((double) f2 * 1.2D + (double) f3));
			modelrenderer.rotateAngleY += bodywear.rotateAngleY * 2.0F;
			modelrenderer.rotateAngleZ += MathHelper.sin(this.swingProgress
					* (float) Math.PI)
					* -0.4F;
		}

		rightArmwear.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
		leftArmwear.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
		rightArmwear.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
		leftArmwear.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
	}

	protected ModelRenderer getArmForSide(EnumHandSide side) {
		return side == EnumHandSide.LEFT ? leftArmwear : rightArmwear;
	}

	protected EnumHandSide getMainHand(Entity entityIn) {
		if (entityIn instanceof EntityLivingBase) {
			EntityLivingBase entitylivingbase = (EntityLivingBase) entityIn;
			EnumHandSide enumhandside = entitylivingbase.getPrimaryHand();
			return entitylivingbase.swingingHand == EnumHand.MAIN_HAND
					? enumhandside
					: enumhandside.opposite();
		} else {
			return EnumHandSide.RIGHT;
		}
	}
}