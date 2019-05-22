package com.nic.st.power;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.nic.st.StarTech;
import com.nic.st.util.Utils;
import lucraft.mods.lucraftcore.superpowers.abilities.Ability;
import lucraft.mods.lucraftcore.superpowers.abilities.AbilityConstant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

/**
 * Created by Nictogen on 1/6/19.
 */
public class AbilityPowerCyclone extends AbilityConstant
{
	public AbilityPowerCyclone(EntityLivingBase player)
	{
		super(player);
	}

	@Override public void updateTick()
	{
		if (!entity.world.isRemote && ticks % 20 == 0)
			shootRocket((context == EnumAbilityContext.OFF_HAND) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, entity);
	}

	public static void shootRocket(EnumHand hand, EntityLivingBase player)
	{
		EntityPowerRocket rocket = new EntityPowerRocket(player.world, player);
		EnumHandSide side = (player.getPrimaryHand() == EnumHandSide.RIGHT) ?
				(hand == EnumHand.MAIN_HAND) ? EnumHandSide.RIGHT : EnumHandSide.LEFT :
				(hand == EnumHand.MAIN_HAND) ? EnumHandSide.LEFT : EnumHandSide.RIGHT;
		Vec3d eyes = player.getPositionEyes(0.0f);
		Vec3d rotVec = Utils.getVectorForRotation(player.rotationPitch,
				(side == EnumHandSide.RIGHT) ? player.rotationYaw + 90 : player.rotationYaw - 90);
		Vec3d offset = eyes.add(rotVec.scale(0.3)).subtract(0, 0.1, 0);
		rocket.setLocationAndAngles(offset.x, offset.y, offset.z, rocket.rotationYaw, rocket.rotationPitch);
		rocket.shoot(player, -45f - player.world.rand.nextInt(45), player.world.rand.nextInt(360), 0.0f, 0.25f, 0.5f);
		player.world.spawnEntity(rocket);
	}

	@SideOnly(Side.CLIENT)
	public static class ClientHandler
	{

		@SubscribeEvent
		public void onUpdate(LivingEvent.LivingUpdateEvent event)
		{
			for (Ability ability : Ability.getAbilities(event.getEntityLiving()))
			{
				if (ability instanceof AbilityPowerCyclone && event.getEntity().world.isRemote && ability.isUnlocked() && ability.isEnabled())
					smokeRing(event.getEntityLiving(), ability.getTicks());
			}
		}

		@SideOnly(Side.CLIENT)
		private void smokeRing(EntityLivingBase player, int progress)
		{
			World w = player.world;

			for (int y = 1; y <= 10 && y <= progress / 10; y++)
			{
				double radius = (10 - y) + Utils.d(w, 8) * Utils.p(w);
				for (int i = 0; i < 8; i++)
				{
					double deltaX = Math.cos(Math.toRadians(i * 45 + (player.ticksExisted) * 6)) * radius;
					double deltaZ = -Math.sin(Math.toRadians(i * 45 + (player.ticksExisted) * 6)) * radius;
					double finalX = player.posX + deltaX;
					double finalZ = player.posZ + deltaZ;
					Particle p = Minecraft.getMinecraft().effectRenderer
							.spawnEffectParticle(EnumParticleTypes.EXPLOSION_LARGE.getParticleID(), finalX, player.posY + y + Utils.d(w, 4) * Utils.p(w),
									finalZ, 0,
									0, 0);
					if (p != null)
					{
						if (player.getRNG().nextInt(5) == 0)
						{
							p.setRBGColorF((float) Math.max(p.getRedColorF() + 0.5, 0.0), (float) Math.max(p.getGreenColorF() - 0.5, 0.0),
									(float) Math.max(p.getBlueColorF() + 0.5, 0.0));
						}
						else
							p.setRBGColorF((float) Math.max(p.getRedColorF() - 0.5, 0.0), (float) Math.max(p.getGreenColorF() - 0.5, 0.0),
									(float) Math.max(p.getBlueColorF() - 0.5, 0.0));

					}
				}
			}
		}

		@SubscribeEvent(receiveCanceled = true)
		public void onInput(InputEvent event)
		{
			turnOffKeys();
		}

		@SubscribeEvent(receiveCanceled = true)
		public void onInput(TickEvent.ClientTickEvent event)
		{
			turnOffKeys();
		}

		private void turnOffKeys()
		{
			GameSettings s = Minecraft.getMinecraft().gameSettings;
			EntityPlayer player = Minecraft.getMinecraft().player;
			if (player != null && player.getHeldItemMainhand().getItem() instanceof ItemPowerStone)
			{
				KeyBinding.setKeyBindState(s.keyBindForward.getKeyCode(), false);
				KeyBinding.setKeyBindState(s.keyBindBack.getKeyCode(), false);
				KeyBinding.setKeyBindState(s.keyBindLeft.getKeyCode(), false);
				KeyBinding.setKeyBindState(s.keyBindRight.getKeyCode(), false);
				KeyBinding.setKeyBindState(s.keyBindJump.getKeyCode(), false);

				if (!Minecraft.getMinecraft().player.isCreative() && Minecraft.getMinecraft().player.getHeldItemMainhand().getItem() instanceof ItemPowerStone)
					for (int i = 0; i < s.keyBindsHotbar.length; i++)
					{
						if (s.keyBindsHotbar[i].isPressed())
						{
							KeyBinding.setKeyBindState(s.keyBindsHotbar[i].getKeyCode(), false);
						}
					}
			}
		}

		@SubscribeEvent
		public void onMouse(MouseEvent event)
		{
			EntityPlayer player = Minecraft.getMinecraft().player;
			if (player == null || player.isCreative() || event.getDwheel() == 0)
				return;
			for (Ability ability : Ability.getAbilities(player))
			{
				if (ability instanceof AbilityPowerCyclone && ability.isUnlocked() && ability.isEnabled())
				{
					event.setCanceled(true);
					return;
				}
			}
		}

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

			for (Ability ability : Ability.getAbilities(event.getEntityLiving()))
			{
				if (ability instanceof AbilityPowerCyclone)
				{
					int progress = ability.getTicks();

					if (progress > 0)
					{
						TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
						IResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();
						NetworkPlayerInfo info = ReflectionHelper
								.getPrivateValue(AbstractClientPlayer.class, (AbstractClientPlayer) event.getEntityPlayer(), 0);

						skin = info.getLocationSkin();
						BufferedImage replacementImage = TextureUtil.readBufferedImage(resourceManager.getResource(skin).getInputStream());
						BufferedImage powerImage = TextureUtil.readBufferedImage(resourceManager.getResource(POWER_BACKGROUND).getInputStream());
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

						Field textureMap = NetworkPlayerInfo.class.getDeclaredFields()[1];
						textureMap.setAccessible(true);
						Map<MinecraftProfileTexture.Type, ResourceLocation> playerTextures = (Map<MinecraftProfileTexture.Type, ResourceLocation>) textureMap
								.get(info);
						playerTextures.put(MinecraftProfileTexture.Type.SKIN, textureManager.getDynamicTextureLocation("power_skin", replacementTexture));
						return;
					}
				}
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
	}

}
