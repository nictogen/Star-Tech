package com.nic.st.power;

import com.nic.st.client.ParticleColoredCloudOld;
import lucraft.mods.lucraftcore.superpowers.abilities.AbilityToggle;
import lucraft.mods.lucraftcore.util.helper.PlayerHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Random;
import java.util.UUID;

/**
 * Created by Nictogen on 1/22/19.
 */
public class AbilityPowerImpower extends AbilityToggle
{
	private static final UUID uuid = UUID.fromString("1864fefb-b78a-4311-b212-cf71326a1843");
	private int stacks = 0;

	public AbilityPowerImpower(EntityLivingBase player)
	{
		super(player);
	}

	@Override public boolean action()
	{
		if (entity.isSneaking())
		{
			if (stacks == 0)
				return false;
			stacks--;
			Random r = entity.getRNG();

			for (int i = 0; i < 360 / 8; i++)
			{
				float deltaX = (float) Math.cos(Math.toRadians(i * 8));
				float deltaZ = (float) -Math.sin(Math.toRadians(i * 8));
				double finalX = (entity.posX + deltaX);
				double finalZ = (entity.posZ + deltaZ);
				int n = (int) (r.nextGaussian()*30.0);
				PlayerHelper.spawnParticleForAll(entity.world, 50.0, ParticleColoredCloudOld.ID, finalX, entity.posY + entity.getEyeHeight() + 0.2, finalZ, 0, -0.5f, 0, 224 + n, 0, 224 + n);
			}
			return true;
		}
		else
		{
			if (stacks == 10)
				return false;
			stacks++;
			Random r = entity.getRNG();
			for (int i = 0; i < 360 / 8; i++)
			{
				float deltaX = (float) Math.cos(Math.toRadians(i * 8));
				float deltaZ = (float) -Math.sin(Math.toRadians(i * 8));
				double finalX = (entity.posX + deltaX);
				double finalZ = (entity.posZ + deltaZ);
				int n = (int) (r.nextGaussian()*30.0);
				PlayerHelper.spawnParticleForAll(entity.world, 50.0, ParticleColoredCloudOld.ID, finalX, entity.posY, finalZ, 0, 1.0f, 0, 224 + n, 0, 224 + n);
			}
			return true;
		}
	}

	@Override public boolean isEnabled()
	{
		return stacks > 0;
	}

	@Override public void updateTick()
	{
		setModifier(SharedMonsterAttributes.ATTACK_DAMAGE, 5.0f * stacks);
		setModifier(SharedMonsterAttributes.ARMOR, 10.0f * stacks);
		setModifier(SharedMonsterAttributes.MOVEMENT_SPEED, -0.01f * stacks);
		setModifier(SharedMonsterAttributes.FLYING_SPEED, -0.01f * stacks);
	}

	public void setModifier(IAttribute attribute, float mod)
	{
		if (entity.getAttributeMap().getAttributeInstance(attribute) == null)
			return;
		if (entity.getAttributeMap().getAttributeInstance(attribute).getModifier(uuid) != null)
			if (entity.getAttributeMap().getAttributeInstance(attribute).getModifier(uuid).getAmount() != mod)
				entity.getAttributeMap().getAttributeInstance(attribute).removeModifier(uuid);

		if (entity.getAttributeMap().getAttributeInstance(attribute).getModifier(uuid) == null)
		{
			AttributeModifier modifier = new AttributeModifier(uuid, getUnlocalizedName(), mod, 0).setSaved(false);
			entity.getAttributeMap().getAttributeInstance(attribute).applyModifier(modifier);
		}
	}

	private void removeModifier(IAttribute attribute)
	{
		if (entity.getAttributeMap().getAttributeInstance(attribute) == null)
			return;
		if (entity.getAttributeMap().getAttributeInstance(attribute).getModifier(uuid) != null)
		{
			entity.getAttributeMap().getAttributeInstance(attribute).removeModifier(uuid);
		}
	}

	private void removeModifiers()
	{
		removeModifier(SharedMonsterAttributes.ATTACK_DAMAGE);
		removeModifier(SharedMonsterAttributes.MOVEMENT_SPEED);
		removeModifier(SharedMonsterAttributes.FLYING_SPEED);
		removeModifier(SharedMonsterAttributes.ARMOR);
	}

	@Override public void lastTick()
	{
		super.lastTick();
		removeModifiers();
	}

	@Override public NBTTagCompound serializeNBT()
	{
		NBTTagCompound compound = super.serializeNBT();
		compound.setInteger("stacks", stacks);
		return compound;
	}

	@Override public void deserializeNBT(NBTTagCompound nbt)
	{
		super.deserializeNBT(nbt);
		this.stacks = nbt.getInteger("stacks");
	}
}
