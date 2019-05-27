package com.nic.st.client;

import com.nic.st.StarTech;
import com.nic.st.client.model.ModelCosmiRod;
import lucraft.mods.lucraftcore.infinity.items.InventoryInfinityGauntlet;
import lucraft.mods.lucraftcore.infinity.render.ItemRendererInfinityStone;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

/**
 * Created by Nictogen on 2019-05-27.
 */
public class ItemRendererCosmiRod extends TileEntityItemStackRenderer
{
	public static ModelCosmiRod MODEL = new ModelCosmiRod();
	public static final ResourceLocation TEXTURE = new ResourceLocation(StarTech.MODID, "textures/cosmi_rod.png");
	public static final ResourceLocation EMPTY_TEXTURE = new ResourceLocation(StarTech.MODID, "textures/cosmi_rod_empty.png");
	public static final ResourceLocation STONE_TEXTURE = new ResourceLocation(StarTech.MODID, "textures/cosmi_rod_stone.png");

	@Override
	public void renderByItem(ItemStack stack, float partialTicks) {

		GlStateManager.pushMatrix();
		GlStateManager.translate(0.5F, 1.8F, 0.5F);
		GlStateManager.scale(-1, -1, -1);
		GlStateManager.disableCull();
		Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);

		if (stack.hasTagCompound()) {
			InventoryInfinityGauntlet inv = new InventoryInfinityGauntlet(stack);
			boolean color = false;
			for (int i = 0; i < inv.getSizeInventory() && !color; i++) {
				ItemStack s = inv.getStackInSlot(i);

				if (!s.isEmpty()) {
					color = true;
					MODEL.render(0.0625F);
					Minecraft.getMinecraft().renderEngine.bindTexture(STONE_TEXTURE);
					if (s.getItem().getTileEntityItemStackRenderer() instanceof ItemRendererInfinityStone)
					{
						Color c = ((ItemRendererInfinityStone) s.getItem().getTileEntityItemStackRenderer()).color;
						GlStateManager.color((float) c.getRed() / 255f, (float) c.getGreen() / 255f, (float) c.getBlue() / 255f);
					}
					MODEL.render(0.0625F);
					GlStateManager.color(1.0f, 1.0f, 1.0f);
					GlStateManager.pushMatrix();
					GlStateManager.rotate(45f, 1, 0, 0);
					GlStateManager.translate(-0.4f, -0.86F, 0.535F);
					GlStateManager.scale(0.35F, 0.35F, 0.35F);

					if (s.getItem().getTileEntityItemStackRenderer() != null)
						s.getItem().getTileEntityItemStackRenderer().renderByItem(s, partialTicks);

					GlStateManager.popMatrix();
				}
			}
			if(!color)
			{
				Minecraft.getMinecraft().renderEngine.bindTexture(EMPTY_TEXTURE);
				MODEL.render(0.0625F);
			}
		}

		GlStateManager.popMatrix();
	}

}
