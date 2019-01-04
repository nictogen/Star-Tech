package com.nic.st.entity;

import com.nic.st.StarTech;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;

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
			if(result != null && result.entityHit != null)
			{
				//			for (EntityLivingBase entitylivingbase : this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(5.0D)))
				//			{
				//				if (entitylivingbase != this.thrower && this.getDistanceSq(entitylivingbase) <= 25.0D)
				//				{
				//					boolean flag = false;
				//
				//					for (int i = 0; i < 2; ++i)
				//					{
				//						RayTraceResult raytraceresult = this.world.rayTraceBlocks(getPositionVector(),
				//								new Vec3d(entitylivingbase.posX, entitylivingbase.posY + (double) entitylivingbase.height * 0.5D * (double) i,
				//										entitylivingbase.posZ), false, true, false);
				//
				//						if (raytraceresult == null || raytraceresult.typeOfHit == RayTraceResult.Type.MISS)
				//						{
				//							flag = true;
				//							break;
				//						}
				//					}
				//
				//					if (flag)
				//					{
				//						float f1 = (15f - this.getDistance(entitylivingbase));
				//						entitylivingbase.attackEntityFrom(DamageSource.causeThrownDamage(this, thrower), (float) (f1 * damage));
				//					}
				//				}
				//			}
				result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, thrower), (float) (damage * 10));
				//			this.world.playSound(null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 1.0F, (1.0F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F) * 0.7F);
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
			StarTech.proxy.onLaserImpact(world, posX, posY, posZ, Color.RED);
		}
	}
}
