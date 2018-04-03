package com.nic.st.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.util.Random;

/**
 * Created by Nictogen on 4/3/18.
 */
public class EntityBullet extends EntityThrowable
{
	public double damage = 1.0;

	public EntityBullet(World worldIn)
	{
		super(worldIn);
	}

	public EntityBullet(World worldIn, EntityLivingBase throwerIn)
	{
		super(worldIn, throwerIn);
	}

	public EntityBullet(World worldIn, double x, double y, double z)
	{
		super(worldIn, x, y, z);
	}

	@Override public void onUpdate()
	{
		super.onUpdate();
		if (this.ticksExisted > 80)
		{
			onImpact(null);
		}
	}

	/**
	 * Called when this EntityThrowable hits a block or entity.
	 */
	protected void onImpact(RayTraceResult result)
	{
		if (!this.world.isRemote)
		{
			for (EntityLivingBase entitylivingbase : this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(5.0D)))
			{
				if (entitylivingbase != this.thrower && this.getDistanceSq(entitylivingbase) <= 25.0D)
				{
					boolean flag = false;

					for (int i = 0; i < 2; ++i)
					{
						RayTraceResult raytraceresult = this.world.rayTraceBlocks(getPositionVector(),
								new Vec3d(entitylivingbase.posX, entitylivingbase.posY + (double) entitylivingbase.height * 0.5D * (double) i,
										entitylivingbase.posZ), false, true, false);

						if (raytraceresult == null || raytraceresult.typeOfHit == RayTraceResult.Type.MISS)
						{
							flag = true;
							break;
						}
					}

					if (flag)
					{
						float f1 = (15f - this.getDistance(entitylivingbase));
						entitylivingbase.attackEntityFrom(DamageSource.causeThrownDamage(this, thrower), (float) (f1 * damage));
					}
				}
			}
			this.world.setEntityState(this, (byte) 3);
			this.setDead();
		}
	}

	@Override public boolean hasNoGravity()
	{
		return true;
	}

	/**
	 * Handler for {@link World#setEntityState}
	 */
	@SideOnly(Side.CLIENT)
	public void handleStatusUpdate(byte id)
	{
		if (id == 3)
		{
			NBTTagCompound compound = new NBTTagCompound();
			NBTTagList explosions = new NBTTagList();
			NBTTagCompound explosion = new NBTTagCompound();
			explosion.setByte("Trail", (byte) 1);
			explosion.setByte("Type", (byte) 4);
			int[] colors = new int[5];
			for (int i = 0; i < 4; i++)
			{
				Random r = new Random();
				colors[i] = new Color(1.0f, r.nextFloat() * 0.5f + 0.3f, 0.0f).getRGB();
			}
			colors[4] = Color.red.getRGB();
			explosion.setIntArray("Colors", colors);
			explosions.appendTag(explosion);
			compound.setTag("Explosions", explosions);
			this.world.makeFireworks(this.posX, this.posY, this.posZ, this.motionX, this.motionY, this.motionZ, compound);
		}
	}
}
