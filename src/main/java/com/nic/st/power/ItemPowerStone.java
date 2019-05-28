package com.nic.st.power;

import com.nic.st.StarTech;
import com.nic.st.entity.EntityItemIndestructibleST;
import lucraft.mods.lucraftcore.infinity.EntityItemIndestructible;
import lucraft.mods.lucraftcore.infinity.EnumInfinityStone;
import lucraft.mods.lucraftcore.infinity.ModuleInfinity;
import lucraft.mods.lucraftcore.infinity.items.ItemInfinityStone;
import lucraft.mods.lucraftcore.superpowers.abilities.Ability;
import lucraft.mods.lucraftcore.superpowers.abilities.supplier.IAbilityProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * Created by Nictogen on 7/27/18.
 */
public class ItemPowerStone extends ItemInfinityStone implements IAbilityProvider
{
	public ItemPowerStone()
	{
		setRegistryName(StarTech.MODID, "power_stone");
		setTranslationKey("power_stone");
		setMaxStackSize(1);
		setCreativeTab(ModuleInfinity.TAB);
	}

	@Override public EnumInfinityStone getType()
	{
		return EnumInfinityStone.POWER;
	}

	@Override public boolean isContainer()
	{
		return false;
	}

	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand hand)
	{
		ItemStack stack = player.getHeldItem(hand);
		return new ActionResult<>(EnumActionResult.PASS, stack);
	}

	public void onEntityItemIndestructibleUpdate(EntityItemIndestructible entityItem)
	{
		if (!entityItem.getItem().hasTagCompound())
			entityItem.getItem().setTagCompound(new NBTTagCompound());
		entityItem.getItem().getTagCompound().setTag("lc_item_abilities", new NBTTagCompound());
	}

	@Nullable
	@Override
	public Entity createEntity(World world, Entity location, ItemStack itemstack)
	{
		EntityItemIndestructibleST item = new EntityItemIndestructibleST(world, location.posX, location.posY, location.posZ, itemstack);
		item.setEntitySize(entityHeight, entityWidth);
		item.motionX = location.motionX;
		item.motionY = location.motionY;
		item.motionZ = location.motionZ;
		return item;
	}

	@Override public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
	{
		return oldStack.getItem() != newStack.getItem();
	}

	@Override public Ability.AbilityMap addStoneAbilities(EntityLivingBase entity, Ability.AbilityMap abilities, Ability.EnumAbilityContext context)
	{
		abilities.put("power_blast", new AbilityPowerBlast(entity).setMaxCooldown(100));
		abilities.put("power_impower", new AbilityPowerImpower(entity));
		abilities.put("power_rocket_burst", new AbilityRocketBurst(entity).setMaxCooldown(100));
		return super.addStoneAbilities(entity, abilities, context);
	}

	@Override public Ability.AbilityMap addDefaultAbilities(EntityLivingBase entity, Ability.AbilityMap abilities, Ability.EnumAbilityContext context)
	{
		abilities.put("power_tendrils", new AbilityTendrils(entity));
		abilities.put("power_cyclone", new AbilityPowerCyclone(entity));
		abilities.put("power_burnout", new AbilityGiveBurnout(entity));
		return abilities;
	}

	@SideOnly(Side.CLIENT)
	public static class ClientHandler
	{
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
			if (player != null && (player.getHeldItemMainhand().getItem() instanceof ItemPowerStone || player.getHeldItemOffhand().getItem() instanceof ItemPowerStone))
			{
				Minecraft.getMinecraft().displayGuiScreen(null);
				KeyBinding.setKeyBindState(s.keyBindForward.getKeyCode(), false);
				KeyBinding.setKeyBindState(s.keyBindBack.getKeyCode(), false);
				KeyBinding.setKeyBindState(s.keyBindLeft.getKeyCode(), false);
				KeyBinding.setKeyBindState(s.keyBindRight.getKeyCode(), false);
				KeyBinding.setKeyBindState(s.keyBindJump.getKeyCode(), false);
				KeyBinding.setKeyBindState(s.keyBindInventory.getKeyCode(), false);
				KeyBinding.setKeyBindState(s.keyBindDrop.getKeyCode(), false);

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
			if (player == null || player.isCreative() || event.getDwheel() == 0 || !(player.getHeldItemMainhand().getItem() instanceof ItemPowerStone))
				return;
			event.setCanceled(true);
			Minecraft.getMinecraft().displayGuiScreen(null);
		}


	}
}
