package com.nic.st.power;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

/**
 * Created by Nictogen on 7/28/18.
 */
public class EntityPowerRocket extends EntityThrowable
{
	public EntityPowerRocket(World worldIn)
	{
		super(worldIn);
	}

	public EntityPowerRocket(World worldIn, double x, double y, double z)
	{
		super(worldIn, x, y, z);
	}

	public EntityPowerRocket(World worldIn, EntityLivingBase throwerIn)
	{
		super(worldIn, throwerIn);
	}

	@Override public void onUpdate()
	{
		super.onUpdate();
		if (ticksExisted > 20 && ticksExisted % 10 == 0)
		{
			shoot(this, rand.nextInt(360), rand.nextInt(360), 0.0f, 0.25f, 0.5f);
		}

		if (ticksExisted == 150)
			onImpact(null);
	}

	/**
	 * Called when this EntityThrowable hits a block or entity.
	 */
	protected void onImpact(RayTraceResult result)
	{
		if (result != null && result.entityHit == this.thrower)
			return;
		if (!this.world.isRemote)
		{
			this.world.createExplosion(this.thrower, this.posX, this.posY, this.posZ, 5.0f, false);
			this.setDead();
		}
	}

	@Override public boolean hasNoGravity()
	{
		return this.ticksExisted < 20;
	}

}
