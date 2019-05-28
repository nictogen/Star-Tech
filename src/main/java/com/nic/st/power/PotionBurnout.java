package com.nic.st.power;

import com.nic.st.ClientProxy;
import com.nic.st.StarTech;
import com.nic.st.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Nictogen on 2019-05-27.
 */
@Mod.EventBusSubscriber
public class PotionBurnout extends Potion
{
	public PotionBurnout()
	{
		super(false, 0xbe18f0);
		this.setRegistryName(StarTech.MODID, "burnout");
		this.setPotionName("effect.burnout");
		this.registerPotionAttributeModifier(SharedMonsterAttributes.MAX_HEALTH, "bfda4cb7-8f9c-4a14-8bff-3e374ddae374", -1.0, 0);
	}

	public static PotionBurnout POTION_BURNOUT = new PotionBurnout();

	public static void giveBurnout(EntityLivingBase entityLivingBase)
	{
		int duration = (entityLivingBase.isPotionActive(PotionBurnout.POTION_BURNOUT)) ?
				entityLivingBase.getActivePotionEffect(PotionBurnout.POTION_BURNOUT).getDuration() + 2 :
				2;
		entityLivingBase
				.addPotionEffect(new PotionEffect(PotionBurnout.POTION_BURNOUT, duration, duration / 10, false, !(entityLivingBase instanceof EntityPlayer)));
		if (entityLivingBase.getMaxHealth() <= 0.1)
		{
			if (entityLivingBase instanceof EntityPlayer && ((EntityPlayer) entityLivingBase).capabilities.isCreativeMode)
				return;
			entityLivingBase.world.createExplosion(null, entityLivingBase.posX, entityLivingBase.posY, entityLivingBase.posZ, 3.0f, true);
		}
	}

	@Override public boolean isReady(int duration, int amplifier)
	{
		return duration / 10 != amplifier && (duration + 1) / 10 != amplifier;
	}

	@Override public void performEffect(EntityLivingBase entityLivingBaseIn, int amplifier)
	{
		super.performEffect(entityLivingBaseIn, amplifier);
		int duration = entityLivingBaseIn.getActivePotionEffect(POTION_BURNOUT).getDuration();
		entityLivingBaseIn.removePotionEffect(POTION_BURNOUT);
		entityLivingBaseIn
				.addPotionEffect(new PotionEffect(PotionBurnout.POTION_BURNOUT, duration, duration / 10, false, !(entityLivingBaseIn instanceof EntityPlayer)));
	}

	@SubscribeEvent
	public static void onRegisterPotions(RegistryEvent.Register<Potion> e)
	{
		e.getRegistry().register(POTION_BURNOUT);
	}

	@Override
	public boolean shouldRender(PotionEffect effect)
	{
		return false;
	}

	@Override
	public boolean hasStatusIcon()
	{
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void renderInventoryEffect(PotionEffect effect, Gui gui, int x, int y, float z)
	{
		if (effect.getPotion() == this)
		{
			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			Minecraft.getMinecraft().renderEngine.bindTexture(ClientProxy.SUPERPOWER_ICONS);
			gui.drawTexturedModalRect(x + 8, y + 8, 16, 0, 16, 16);
			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void renderHUDEffect(PotionEffect effect, Gui gui, int x, int y, float z, float alpha)
	{
		if (effect.getPotion() == this)
		{
			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			Minecraft.getMinecraft().renderEngine.bindTexture(ClientProxy.SUPERPOWER_ICONS);
			gui.drawTexturedModalRect(x + 4, y + 4, 16, 0, 16, 16);
			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
		}
	}

	@SideOnly(Side.CLIENT)
	public static class ClientHandler
	{
		private static final ResourceLocation POWER_BACKGROUND = new ResourceLocation(StarTech.MODID, "textures/power_background.png");
		public static DynamicTexture extendedTexture = null;
		public static DynamicTexture glowingTexture = null;
		public static DynamicTexture replacementTexture = null;
		private static ArrayList<EntityPlayer> layersAddedTo = new ArrayList<>();
		private static World lastWorld = null;

		@SubscribeEvent
		public void onRenderPlayerPre(RenderPlayerEvent.Pre event) throws IOException
		{
			if (lastWorld != event.getEntityPlayer().world)
			{
				layersAddedTo.clear();
				lastWorld = event.getEntityPlayer().world;
			}
			if (!layersAddedTo.contains(event.getEntityPlayer()))
			{
				layersAddedTo.add(event.getEntityPlayer());
				event.getRenderer().addLayer(new PowerPlayerLayerRenderer(event.getRenderer()));
			}

			if (event.getEntityLiving().isPotionActive(POTION_BURNOUT))
			{
				PotionEffect e = event.getEntityLiving().getActivePotionEffect(POTION_BURNOUT);
				double progress = ((double) e.getDuration()) / (double) (event.getEntityLiving().getMaxHealth() + e.getAmplifier()) * 175.0;
				if (progress > 0)
				{
					BufferedImage replacementImage = cloneSkin(getPlayerSkin((AbstractClientPlayer) event.getEntityPlayer()));
					BufferedImage powerImage = TextureUtil
							.readBufferedImage(Minecraft.getMinecraft().getResourceManager().getResource(POWER_BACKGROUND).getInputStream());
					BufferedImage extendedImage = new BufferedImage(replacementImage.getWidth(), replacementImage.getHeight(),
							BufferedImage.TYPE_INT_ARGB);
					BufferedImage glowingImage = new BufferedImage(replacementImage.getWidth(), replacementImage.getHeight(),
							BufferedImage.TYPE_INT_ARGB);

					Random r = new Random(EntityPlayer.getUUID(event.getEntityPlayer().getGameProfile()).getLeastSignificantBits());

					for (int i = 0; i < progress; i++)
					{
						int x1 = r.nextInt(replacementImage.getWidth());
						int y1 = r.nextInt(replacementImage.getHeight());
						for (int n = 0; n < 4; n++)
						{
							int x = Math.max(Math.min(replacementImage.getWidth() - 1, Math.max(0, x1 + Utils.randomInt3(r))), 0);
							int y = Math.min(replacementImage.getHeight() - 1, Math.max(0, y1 + Utils.randomInt3(r)));

							Color powerColor = new Color(powerImage.getRGB(x, y));
							Color skinColor = new Color(replacementImage.getRGB(x, y));

							if (!powerColor.equals(Color.BLACK) && !powerColor.equals(skinColor))
							{
								if (progress - 400 < i)
								{
									extendedImage.setRGB(x, y, skinColor.getRGB());
								}
								glowingImage.setRGB(x, y, powerColor.getRGB());
								replacementImage.setRGB(x, y, powerColor.getRGB());
							}
						}
					}

					replacementTexture = new DynamicTexture(replacementImage);
					extendedTexture = new DynamicTexture(extendedImage);
					glowingTexture = new DynamicTexture(glowingImage);
				}
			}
		}

		public BufferedImage cloneSkin(BufferedImage originalImage)
		{
			BufferedImage image = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), originalImage.getType());
			for (int x = 0; x < image.getWidth(); x++)
				for (int y = 0; y < image.getHeight(); y++)
					image.setRGB(x, y, originalImage.getRGB(x, y));
			return image;
		}

		public static BufferedImage getPlayerSkin(AbstractClientPlayer player)
		{
			try
			{
				ThreadDownloadImageData t = (ThreadDownloadImageData) Minecraft.getMinecraft().getTextureManager().getTexture(player.getLocationSkin());
				Field f = ThreadDownloadImageData.class.getDeclaredFields()[5];
				f.setAccessible(true);
				return (BufferedImage) f.get(t);
			}
			catch (IllegalAccessException e)
			{
				e.printStackTrace();
				return null;
			}
			catch (ClassCastException e)
			{
				try
				{
					SimpleTexture t = (SimpleTexture) Minecraft.getMinecraft().getTextureManager().getTexture(player.getLocationSkin());
					Field f = SimpleTexture.class.getDeclaredFields()[1];
					f.setAccessible(true);
					IResource iresource = Minecraft.getMinecraft().getResourceManager().getResource((ResourceLocation) f.get(t));
					return TextureUtil.readBufferedImage(iresource.getInputStream());
				}
				catch (IOException | IllegalAccessException e2)
				{
					e2.printStackTrace();
					return null;
				}
			}
		}

		@SubscribeEvent
		public void onRenderPlayerPost(RenderPlayerEvent.Post event)
		{
			if (replacementTexture != null)
			{
				replacementTexture.deleteGlTexture();
				replacementTexture = null;
			}

			if (extendedTexture != null)
			{
				extendedTexture.deleteGlTexture();
				extendedTexture = null;
			}

			if (glowingTexture != null)
			{
				glowingTexture.deleteGlTexture();
				glowingTexture = null;
			}
		}
	}

}
