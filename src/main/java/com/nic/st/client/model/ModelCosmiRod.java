package com.nic.st.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

/**
 * RonanHammer - Neon
 * Created using Tabula 4.1.1
 */
public class ModelCosmiRod extends ModelBase
{
	public ModelRenderer hammerMain;
	public ModelRenderer handleMain;
	public ModelRenderer cornerFUL;
	public ModelRenderer cornerFUR;
	public ModelRenderer cornerFDL;
	public ModelRenderer cornerFDR;
	public ModelRenderer cornerBUR;
	public ModelRenderer cornerBUL;
	public ModelRenderer cornerBDL;
	public ModelRenderer cornerBDR;
	public ModelRenderer stonePower;
	public ModelRenderer detail;
	public ModelRenderer handleMiddle;
	public ModelRenderer handleEnd;
	public ModelRenderer handleEndR;
	public ModelRenderer tip;

	public ModelCosmiRod() {
		this.textureWidth = 64;
		this.textureHeight = 32;
		this.cornerFUL = new ModelRenderer(this, 0, 0);
		this.cornerFUL.mirror = true;
		this.cornerFUL.setRotationPoint(3.3F, -0.3F, -0.3F);
		this.cornerFUL.addBox(0.0F, 0.0F, 0.0F, 3, 3, 4, 0.0F);
		this.cornerBDR = new ModelRenderer(this, 0, 0);
		this.cornerBDR.setRotationPoint(-0.3F, 3.3F, 4.3F);
		this.cornerBDR.addBox(0.0F, 0.0F, 0.0F, 3, 3, 4, 0.0F);
		this.tip = new ModelRenderer(this, 0, 10);
		this.tip.setRotationPoint(-0.5F, 12.0F, -0.5F);
		this.tip.addBox(0.0F, 0.0F, 0.0F, 2, 2, 2, 0.0F);
		this.cornerFDR = new ModelRenderer(this, 0, 0);
		this.cornerFDR.setRotationPoint(-0.3F, 3.3F, -0.3F);
		this.cornerFDR.addBox(0.0F, 0.0F, 0.0F, 3, 3, 4, 0.0F);
		this.cornerBUR = new ModelRenderer(this, 24, 0);
		this.cornerBUR.setRotationPoint(-0.3F, -0.3F, 4.3F);
		this.cornerBUR.addBox(0.0F, 0.0F, 0.0F, 3, 3, 7, 0.0F);
		this.setRotateAngle(cornerBUR, -0.045553093477052F, 0.0F, 0.0F);
		this.handleMiddle = new ModelRenderer(this, 56, 20);
		this.handleMiddle.setRotationPoint(0.5F, 6.0F, 0.5F);
		this.handleMiddle.addBox(0.0F, 0.0F, 0.0F, 2, 10, 2, 0.0F);
		this.detail = new ModelRenderer(this, 52, 0);
		this.detail.setRotationPoint(1.5F, 6.0F, 1.5F);
		this.detail.addBox(-1.5F, -1.5F, -1.5F, 3, 3, 3, 0.0F);
		this.setRotateAngle(detail, 0.7853981633974483F, 0.0F, 0.0F);
		this.cornerBDL = new ModelRenderer(this, 0, 0);
		this.cornerBDL.mirror = true;
		this.cornerBDL.setRotationPoint(3.3F, 3.3F, 4.3F);
		this.cornerBDL.addBox(0.0F, 0.0F, 0.0F, 3, 3, 4, 0.0F);
		this.stonePower = new ModelRenderer(this, 25, 12);
		this.stonePower.setRotationPoint(-0.5F, 3.0F, 4.0F);
		this.stonePower.addBox(0.0F, -1.0F, -1.0F, 7, 2, 2, 0.0F);
		this.setRotateAngle(stonePower, 0.7853981633974483F, 0.0F, 0.0F);
		this.handleMain = new ModelRenderer(this, 52, 0);
		this.handleMain.setRotationPoint(-1.5F, -13.0F, -1.5F);
		this.handleMain.addBox(0.0F, 0.0F, 0.0F, 3, 6, 3, 0.0F);
		this.cornerFUR = new ModelRenderer(this, 0, 0);
		this.cornerFUR.setRotationPoint(-0.3F, -0.3F, -0.3F);
		this.cornerFUR.addBox(0.0F, 0.0F, 0.0F, 3, 3, 4, 0.0F);
		this.cornerFDL = new ModelRenderer(this, 0, 0);
		this.cornerFDL.mirror = true;
		this.cornerFDL.setRotationPoint(3.3F, 3.3F, -0.3F);
		this.cornerFDL.addBox(0.0F, 0.0F, 0.0F, 3, 3, 4, 0.0F);
		this.handleEndR = new ModelRenderer(this, 35, 19);
		this.handleEndR.setRotationPoint(1.0F, 10.0F, 1.0F);
		this.handleEndR.addBox(-0.5F, 0.0F, -0.5F, 1, 12, 1, 0.0F);
		this.setRotateAngle(handleEndR, 0.0F, 0.7853981633974483F, 0.0F);
		this.handleEnd = new ModelRenderer(this, 35, 19);
		this.handleEnd.setRotationPoint(0.5F, 10.0F, 0.5F);
		this.handleEnd.addBox(0.0F, 0.0F, 0.0F, 1, 12, 1, 0.0F);
		this.hammerMain = new ModelRenderer(this, 0, 18);
		this.hammerMain.setRotationPoint(-3.0F, -19.0F, -4.0F);
		this.hammerMain.addBox(0.0F, 0.0F, 0.0F, 6, 6, 8, 0.0F);
		this.cornerBUL = new ModelRenderer(this, 24, 0);
		this.cornerBUL.mirror = true;
		this.cornerBUL.setRotationPoint(3.3F, -0.3F, 4.3F);
		this.cornerBUL.addBox(0.0F, 0.0F, 0.0F, 3, 3, 7, 0.0F);
		this.setRotateAngle(cornerBUL, -0.045553093477052F, 0.0F, 0.0F);
		this.hammerMain.addChild(this.cornerFUL);
		this.hammerMain.addChild(this.cornerBDR);
		this.handleEnd.addChild(this.tip);
		this.hammerMain.addChild(this.cornerFDR);
		this.hammerMain.addChild(this.cornerBUR);
		this.handleMain.addChild(this.handleMiddle);
		this.handleMain.addChild(this.detail);
		this.hammerMain.addChild(this.cornerBDL);
		this.hammerMain.addChild(this.stonePower);
		this.hammerMain.addChild(this.cornerFUR);
		this.hammerMain.addChild(this.cornerFDL);
		this.handleMiddle.addChild(this.handleEndR);
		this.handleMiddle.addChild(this.handleEnd);
		this.hammerMain.addChild(this.cornerBUL);
	}

	public void render(float f5) {
		this.handleMain.render(f5);
		this.hammerMain.render(f5);
	}

	/**
	 * This is a helper function from Tabula to set the rotation of model parts
	 */
	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
