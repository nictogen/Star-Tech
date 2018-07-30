package com.nic.st.power;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.nic.st.StarTech;
import com.nic.st.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

/**
 * Created by Nictogen on 7/30/18.
 */
public class PowerClientEventHandler
{

	private static final ResourceLocation POWER_BACKGROUND = new ResourceLocation(StarTech.MODID, "textures/power_background.png");
	public static DynamicTexture extendedTexture = null;
	public static DynamicTexture glowingTexture = null;
	private static ResourceLocation skin = null;
	private static DynamicTexture replacementTexture = null;
	private static ArrayList<EntityPlayer> layersAddedTo = new ArrayList<>();
	private static World lastWorld = null;

	@SubscribeEvent
	public void onRenderPlayerPre(RenderPlayerEvent.Pre event) throws IOException, IllegalAccessException
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

		TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
		IResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();
		NetworkPlayerInfo info = ReflectionHelper.getPrivateValue(AbstractClientPlayer.class, (AbstractClientPlayer) event.getEntityPlayer(), 0);

		skin = info.getLocationSkin();
		BufferedImage replacementImage = TextureUtil.readBufferedImage(resourceManager.getResource(skin).getInputStream());
		BufferedImage powerImage = TextureUtil.readBufferedImage(resourceManager.getResource(POWER_BACKGROUND).getInputStream());
		BufferedImage extendedImage = new BufferedImage(replacementImage.getWidth(), replacementImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
		BufferedImage glowingImage = new BufferedImage(replacementImage.getWidth(), replacementImage.getHeight(), BufferedImage.TYPE_INT_ARGB);

		Random r = new Random(EntityPlayer.getUUID(event.getEntityPlayer().getGameProfile()).getLeastSignificantBits());

		int progress = ItemPowerStone.getPowerStoneDuration(event.getEntityPlayer());

		if (progress > 0)
		{
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

			Field textureMap = NetworkPlayerInfo.class.getDeclaredFields()[1];
			textureMap.setAccessible(true);
			Map<MinecraftProfileTexture.Type, ResourceLocation> playerTextures = (Map<MinecraftProfileTexture.Type, ResourceLocation>) textureMap.get(info);
			playerTextures.put(MinecraftProfileTexture.Type.SKIN, textureManager.getDynamicTextureLocation("power_skin", replacementTexture));
		}
	}

	@SubscribeEvent
	public void onRenderPlayerPost(RenderPlayerEvent.Post event) throws IllegalAccessException
	{

		if (skin != null)
		{
			NetworkPlayerInfo info = ReflectionHelper.getPrivateValue(AbstractClientPlayer.class, (AbstractClientPlayer) event.getEntityPlayer(), 0);
			Field textureMap = NetworkPlayerInfo.class.getDeclaredFields()[1];
			textureMap.setAccessible(true);
			Map<MinecraftProfileTexture.Type, ResourceLocation> playerTextures = (Map<MinecraftProfileTexture.Type, ResourceLocation>) textureMap.get(info);
			Minecraft.getMinecraft().getTextureManager().deleteTexture(playerTextures.get(MinecraftProfileTexture.Type.SKIN));
			playerTextures.put(MinecraftProfileTexture.Type.SKIN, skin);
			skin = null;
		}

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

	@SubscribeEvent(receiveCanceled = true)
	public void onInput(InputEvent event)
	{
		GameSettings s = Minecraft.getMinecraft().gameSettings;

		if (Minecraft.getMinecraft().player != null && ItemPowerStone.getPowerStoneDuration(Minecraft.getMinecraft().player) != 0)
		{
			if (s.keyBindForward.isPressed())
				KeyBinding.setKeyBindState(s.keyBindForward.getKeyCode(), false);
			if (s.keyBindBack.isPressed())
				KeyBinding.setKeyBindState(s.keyBindBack.getKeyCode(), false);
			if (s.keyBindLeft.isPressed())
				KeyBinding.setKeyBindState(s.keyBindLeft.getKeyCode(), false);
			if (s.keyBindRight.isPressed())
				KeyBinding.setKeyBindState(s.keyBindRight.getKeyCode(), false);
			if (s.keyBindJump.isPressed())
				KeyBinding.setKeyBindState(s.keyBindJump.getKeyCode(), false);
		}
	}

}
