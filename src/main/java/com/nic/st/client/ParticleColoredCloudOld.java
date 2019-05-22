package com.nic.st.client;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleCloud;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * Created by Nictogen on 1/22/19.
 */
@SideOnly(Side.CLIENT)
public class ParticleColoredCloudOld extends ParticleCloud
{

	public static final int ID = 2512;

	@SideOnly(Side.CLIENT)
	public static class Factory implements IParticleFactory
	{

		@Nullable
		@Override
		public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... args) {
			return new ParticleColoredCloudOld(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, args[0], args[1], args[2]);
		}

	}

	protected ParticleColoredCloudOld(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double speedX, double speedY, double speedZ, int red, int green, int blue) {
		super(worldIn, xCoordIn, yCoordIn, zCoordIn, speedX, speedY, speedZ);
		this.particleRed = (float) red / 255F;
		this.particleGreen = (float) green / 255F;
		this.particleBlue = (float) blue / 255F;
		this.canCollide = false;
		setMaxAge(5);
	}

}
