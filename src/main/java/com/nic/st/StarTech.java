package com.nic.st;

import com.nic.st.abilities.AbilityConcussiveBlast;
import com.nic.st.blocks.BlockBlueprintCreator;
import com.nic.st.blocks.BlockHologram;
import com.nic.st.blocks.BlockPrinter;
import com.nic.st.entity.EntityBullet;
import com.nic.st.entity.EntityItemIndestructibleST;
import com.nic.st.items.ItemBlueprint;
import com.nic.st.items.ItemCosmiRod;
import com.nic.st.items.ItemPrintedGun;
import com.nic.st.network.MessageChangeVoxel;
import com.nic.st.network.MessageMovePlayer;
import com.nic.st.power.*;
import lucraft.mods.lucraftcore.superpowers.abilities.AbilityEntry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Mod(modid = StarTech.MODID, name = StarTech.NAME, version = StarTech.VERSION)
@Mod.EventBusSubscriber
public class StarTech
{
	public static final String MODID = "star-tech";
	public static final String NAME = "Star Tech, Man! The Legendary Mod?";
	public static final String VERSION = "1.1";

	@SidedProxy(clientSide = "com.nic.st.ClientProxy", serverSide = "com.nic.st.CommonProxy")
	public static CommonProxy proxy;

	public static SimpleNetworkWrapper simpleNetworkWrapper;

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event)
	{
		event.getRegistry().register(new BlockBlueprintCreator());
		event.getRegistry().register(new BlockHologram());
		event.getRegistry().register(new BlockPrinter());
	}

	@SubscribeEvent
	public static void registerEntityEntry(RegistryEvent.Register<EntityEntry> event)
	{
		event.getRegistry().register(
				EntityEntryBuilder.create().entity(EntityBullet.class)
						.id(new ResourceLocation(MODID, "bullet"), 0).name("bullet")
						.tracker(80, 10, true)
						.build());
		event.getRegistry().register(
				EntityEntryBuilder.create().entity(EntityPowerRocket.class)
						.id(new ResourceLocation(MODID, "power_rocket"), 1).name("power_rocket")
						.tracker(80, 10, true)
						.build());
		event.getRegistry().register(
				EntityEntryBuilder.create().entity(EntityItemIndestructibleST.class)
						.id(new ResourceLocation(MODID, "indestructible_item"), 2).name("indestructible_item")
						.tracker(80, 10, true)
						.build());
	}


	@SubscribeEvent
	public static void onRegisterAbilities(RegistryEvent.Register<AbilityEntry> e) {
		e.getRegistry().register(new AbilityEntry(AbilityTendrils.class, new ResourceLocation(MODID, "tendrils")));
		e.getRegistry().register(new AbilityEntry(AbilityPowerCyclone.class, new ResourceLocation(MODID, "power_cyclone")));
		e.getRegistry().register(new AbilityEntry(AbilityPowerBlast.class, new ResourceLocation(MODID, "power_blast")));
		e.getRegistry().register(new AbilityEntry(AbilityPowerImpower.class, new ResourceLocation(MODID, "power_impower")));
		e.getRegistry().register(new AbilityEntry(AbilityRocketBurst.class, new ResourceLocation(MODID, "power_rocket_burst")));
		e.getRegistry().register(new AbilityEntry(AbilityGiveBurnout.class, new ResourceLocation(MODID, "power_give_burnout")));
		e.getRegistry().register(new AbilityEntry(AbilityConcussiveBlast.class, new ResourceLocation(MODID, "concussive_blast")));
	}


	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) throws IllegalAccessException
	{
		event.getRegistry().register(new ItemPrintedGun());
		event.getRegistry().register(new ItemBlueprint());
		event.getRegistry().register(new ItemPowerStone());
		event.getRegistry().register(new ItemCosmiRod());

		for (Field field : Blocks.class.getDeclaredFields())
		{
			Block block = (Block) field.get(null);
			Item itemBlock = new ItemBlock(block).setRegistryName(block.getRegistryName()).setTranslationKey(block.getTranslationKey());
			event.getRegistry().register(itemBlock);
		}

	}

	@SubscribeEvent
	public static void registerSounds(RegistryEvent.Register<SoundEvent> event)
	{
		Sounds.shoot = new SoundEvent(new ResourceLocation(MODID, "shoot")).setRegistryName(MODID, "shoot");
		event.getRegistry().register(Sounds.shoot);
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		proxy.preInit(event);
		MinecraftForge.EVENT_BUS.register(proxy);

		simpleNetworkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
		simpleNetworkWrapper.registerMessage(MessageChangeVoxel.Handler.class, MessageChangeVoxel.class, 0, Side.SERVER);
		simpleNetworkWrapper.registerMessage(MessageMovePlayer.Handler.class, MessageMovePlayer.class, 1, Side.CLIENT);
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		GameRegistry.registerTileEntity(BlockBlueprintCreator.TileEntityBlueprintCreator.class, new ResourceLocation("star-tech:blueprint_creator"));
		GameRegistry.registerTileEntity(BlockPrinter.TileEntityPrinter.class, new ResourceLocation("star-tech:printer"));
		proxy.init(event);
	}

	@EventHandler
	public void init(FMLPostInitializationEvent event)
	{
		proxy.postInit(event);
	}

	@Retention(value = RUNTIME)
	@Target(value = FIELD)
	public @interface OBJ
	{
	}


	@GameRegistry.ObjectHolder(MODID)
	public static class Blocks
	{
		@GameRegistry.ObjectHolder("blueprint_creator")
		@OBJ
		public static final Block blueprintCreator = null;

		public static final Block hologram = null;

		@OBJ
		public static final Block printer = null;
	}

	@GameRegistry.ObjectHolder(MODID)
	public static class Items
	{
		@GameRegistry.ObjectHolder("printed_gun")
		public static final Item printedGun = null;

		@GameRegistry.ObjectHolder("blueprint_creator")
		public static final Item blueprintCreator = null;

		public static final Item blueprint = null;

		@GameRegistry.ObjectHolder("power_stone")
		public static final Item powerStone = null;

		@GameRegistry.ObjectHolder("cosmi_rod")
		public static final Item cosmiRod = null;
	}

	public static class Sounds
	{
		public static SoundEvent shoot = null;
	}

}
