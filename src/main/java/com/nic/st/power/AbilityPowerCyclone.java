package com.nic.st.power;

import com.nic.st.util.Utils;
import lucraft.mods.lucraftcore.superpowers.abilities.Ability;
import lucraft.mods.lucraftcore.superpowers.abilities.AbilityConstant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
				if (ability instanceof AbilityPowerCyclone && event.getEntity().world.isRemote && ability.isUnlocked())
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

	}

}
