package nz.co.crookedhill.wyem;

import java.util.List;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAICreeperSwell;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import nz.co.crookedhill.wyem.ai.WYEMEntityAICreeperSwell;
import nz.co.crookedhill.wyem.ai.WYEMEntityAINearestAttackableTarget;
import nz.co.crookedhill.wyem.item.WYEMItem;

public class WYEMEventHandler 
{
	/* static so its not created over and over again */
	static Random rand = new Random(System.currentTimeMillis());

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onAttackedEvent(LivingAttackEvent event)
	{
		if(!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayer && event.source.isProjectile())
		{
			EntityPlayer player = (EntityPlayer)event.entity;

			ItemStack[] inv = player.inventory.armorInventory;
			if(inv[2] != null && inv[2].getItem() == WYEMItem.enderChestplate && rand.nextFloat() < WYEMConfigHelper.enderTeleportChance) // 10% chance to cancel and teleport.
			{
				event.setCanceled(true);
				event.source.getSourceOfDamage().setDead();
				/* this line exists because .setDead doesnt hapen streat away, so the player can still get damaged */
				event.source.getSourceOfDamage().setPosition(event.source.getSourceOfDamage().posX, event.source.getSourceOfDamage().posY+1000.0d, event.source.getSourceOfDamage().posZ);
				teleportFromDamage((EntityPlayer)event.entity);
				inv[2].attemptDamageItem(3, rand);
			}
		}
	}

	@SubscribeEvent
	public void onJoinEvent(EntityJoinWorldEvent event)
	{
		if(event.entity instanceof EntityZombie)
		{
			EntityZombie zomb = (EntityZombie)event.entity;
			List list = zomb.targetTasks.taskEntries;

			//zomb.targetTasks.addTask(1, new WYEMEntityAIOwnerHurtTarget(zomb, WYEMItem.zombieCrown));
			/*
			 * this exists because there are 2 instances of the object we want to replace, but the 
			 * one we want to replace was added first, this is used to stop the replacement of the second.
			 */
			//TODO: marker here so i know where the trouble is if the list isnt sorted the way i like.
			int foundClasses = 0;
			for(int i = 0; i < zomb.targetTasks.taskEntries.size(); i++)
			{
				if(list.get(i) instanceof EntityAITaskEntry)
				{
					if(((EntityAITaskEntry)list.get(i)).action instanceof EntityAINearestAttackableTarget)
					{
						foundClasses++;
						if(foundClasses < 2 )
						{
							((EntityAITaskEntry)list.get(i)).action = new WYEMEntityAINearestAttackableTarget(zomb, EntityPlayer.class, 0, true, WYEMItem.zombieCrown);
						}
					}
				}
			}
		}
		if(event.entity instanceof EntityCreeper)
		{
			EntityCreeper crep = (EntityCreeper)event.entity;
			//crep.targetTasks.addTask(1, new WYEMEntityAIOwnerHurtTarget(crep, WYEMItem.creeperCrown));
			List list = crep.targetTasks.taskEntries;
			for(int i = 0; i < crep.targetTasks.taskEntries.size(); i++)
			{
				if(list.get(i) instanceof EntityAITaskEntry)
				{
					if(((EntityAITaskEntry)list.get(i)).action instanceof EntityAINearestAttackableTarget)
					{
						((EntityAITaskEntry)list.get(i)).action = new WYEMEntityAINearestAttackableTarget(crep, EntityPlayer.class, 0, true, WYEMItem.creeperCrown);
					}
				}
				if(list.get(i) instanceof EntityAICreeperSwell)
				{
					if(((EntityAITaskEntry)list.get(i)).action instanceof EntityAICreeperSwell)
					{
						((EntityAITaskEntry)list.get(i)).action = new WYEMEntityAICreeperSwell(crep);
					}
				}
			}
		}
		/* if wither skeleton */
		if(event.entity instanceof EntitySkeleton && ((EntitySkeleton)event.entity).getSkeletonType() == 1)
		{
			EntitySkeleton skel = (EntitySkeleton)event.entity;
			List list = skel.targetTasks.taskEntries;
			for(int i = 0; i < skel.targetTasks.taskEntries.size(); i++)
			{
				if(list.get(i) instanceof EntityAITaskEntry)
				{
					if(((EntityAITaskEntry)list.get(i)).action instanceof EntityAINearestAttackableTarget)
					{
						((EntityAITaskEntry)list.get(i)).action = new WYEMEntityAINearestAttackableTarget(skel, EntityPlayer.class, 0, true, WYEMItem.witherCrown);
					}
				}
			}
		}
		/* if regular skeleton */
		if(event.entity instanceof EntitySkeleton && ((EntitySkeleton)event.entity).getSkeletonType() == 0)
		{
			EntitySkeleton skel = (EntitySkeleton)event.entity;
			//skel.targetTasks.addTask(1, new WYEMEntityAIOwnerHurtTarget(skel, WYEMItem.skeletonCrown));
			List list = skel.targetTasks.taskEntries;
			for(int i = 0; i < skel.targetTasks.taskEntries.size(); i++)
			{
				if(list.get(i) instanceof EntityAITaskEntry)
				{
					if(((EntityAITaskEntry)list.get(i)).action instanceof EntityAINearestAttackableTarget)
					{
						((EntityAITaskEntry)list.get(i)).action = new WYEMEntityAINearestAttackableTarget(skel, EntityPlayer.class, 0, true, WYEMItem.skeletonCrown);
					}
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onHurtEvent(LivingHurtEvent event)
	{
		if(!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)event.entity;

			//TODO remove this on release
			//event.setCanceled(true);

			if(event.source.getEntity() != null && event.source.getEntity() instanceof EntityLivingBase)
				player.setLastAttacker(event.source.getEntity());
			ItemStack[] armorInventory = player.inventory.armorInventory;
			/*if the player is touching lava and is wearing an ender chestplate*/
			if(playerIsInLava(player) && armorInventory[2] != null && armorInventory[2].getItem() == WYEMItem.enderChestplate)
			{
				teleportFromDamage((EntityPlayer)event.entity);
				armorInventory[2].attemptDamageItem(3, rand);
			}
			/* if the player has been hurt by fall damage and is wearing spider treads */
			else if(event.source.getDamageType() == "fall" && armorInventory[0] != null && armorInventory[0].getItem() == WYEMItem.spiderTreads)
			{
				event.ammount -= (event.ammount * WYEMConfigHelper.spiderDamageReduction);
				armorInventory[0].attemptDamageItem(3, rand);
			}
		}
	}
	
	private boolean playerIsInLava(EntityPlayer player)
	{
		return player.worldObj.handleMaterialAcceleration(player.getEntityBoundingBox().expand(0.0D, -0.4000000059604645D, 0.0D).contract(0.001D, 0.001D, 0.001D), Material.water, player);
	}

	@SubscribeEvent
	public void onDeathEvent(LivingDeathEvent event)
	{
		if(event.source.getEntity() instanceof EntityPlayer && ((EntityPlayer)event.source.getEntity()).getHeldItem() != null && ((EntityPlayer)event.source.getEntity()).getHeldItem().getItem() == WYEMItem.headCollector)
		{
			double chance = rand.nextDouble();
			if(chance <= WYEMConfigHelper.headCollectorChance - 0.025D)
			{
				/* Check for wither skeleton */
				if(event.entity instanceof EntitySkeleton)
				{
					if(((EntitySkeleton)event.entity).getSkeletonType() == 1)
					{
						EntityItem item = new EntityItem(event.entity.worldObj, event.entity.posX, event.entity.posY, event.entity.posZ, new ItemStack(Items.skull, 1, 1));
						event.entity.worldObj.spawnEntityInWorld(item);
					}
					else
					{
						EntityItem item = new EntityItem(event.entity.worldObj, event.entity.posX, event.entity.posY, event.entity.posZ, new ItemStack(Items.skull, 1, 0));
						event.entity.worldObj.spawnEntityInWorld(item);
					}
				}
				else if(event.entity instanceof EntityZombie)
				{
					EntityItem item = new EntityItem(event.entity.worldObj, event.entity.posX, event.entity.posY, event.entity.posZ, new ItemStack(Items.skull, 1, 2));
					event.entity.worldObj.spawnEntityInWorld(item);
				}
				else if(event.entity instanceof EntityCreeper)
				{
					EntityItem item = new EntityItem(event.entity.worldObj, event.entity.posX, event.entity.posY, event.entity.posZ, new ItemStack(Items.skull, 1, 4));
					event.entity.worldObj.spawnEntityInWorld(item);
				}
				
				
				else if(event.entity instanceof EntityBlaze)
				{
					event.entity.worldObj.spawnEntityInWorld(getCustomHead(event, "MHF_Blaze"));
				}
				else if(event.entity instanceof EntityCaveSpider)
				{
					event.entity.worldObj.spawnEntityInWorld(getCustomHead(event, "MHF_CaveSpider"));
				}
				else if(event.entity instanceof EntityChicken)
				{
					event.entity.worldObj.spawnEntityInWorld(getCustomHead(event, "MHF_Chicken"));
				}
				else if(event.entity instanceof EntityCow)
				{
					event.entity.worldObj.spawnEntityInWorld(getCustomHead(event, "MHF_Cow"));
				}
				else if(event.entity instanceof EntityEnderman)
				{
					event.entity.worldObj.spawnEntityInWorld(getCustomHead(event, "MHF_Enderman"));
				}
				else if(event.entity instanceof EntityGhast)
				{
					event.entity.worldObj.spawnEntityInWorld(getCustomHead(event, "MHF_Ghast"));
				}
				else if(event.entity instanceof EntityIronGolem)
				{
					event.entity.worldObj.spawnEntityInWorld(getCustomHead(event, "MHF_Golem"));
				}
				else if(event.entity instanceof EntityMagmaCube)
				{
					event.entity.worldObj.spawnEntityInWorld(getCustomHead(event, "MHF_LavaSlime"));
				}
				else if(event.entity instanceof EntityOcelot)
				{
					event.entity.worldObj.spawnEntityInWorld(getCustomHead(event, "MHF_Ocelot"));
				}
				else if(event.entity instanceof EntityPig)
				{
					event.entity.worldObj.spawnEntityInWorld(getCustomHead(event, "MHF_Pig"));
				}
				else if(event.entity instanceof EntitySheep)
				{
					event.entity.worldObj.spawnEntityInWorld(getCustomHead(event, "MHF_Sheep"));
				}
				else if(event.entity instanceof EntitySlime)
				{
					event.entity.worldObj.spawnEntityInWorld(getCustomHead(event, "MHF_Slime"));
				}
				else if(event.entity instanceof EntitySpider)
				{
					event.entity.worldObj.spawnEntityInWorld(getCustomHead(event, "MHF_Spider"));
				}
				else if(event.entity instanceof EntitySquid)
				{
					event.entity.worldObj.spawnEntityInWorld(getCustomHead(event, "MHF_Squid"));
				}
				else if(event.entity instanceof EntityVillager)
				{
					event.entity.worldObj.spawnEntityInWorld(getCustomHead(event, "MHF_Villager"));
				}				
				else if(event.entity instanceof EntityPlayer)
				{
					event.entity.worldObj.spawnEntityInWorld(getCustomHead(event, ((EntityPlayer)event.source.getEntity()).getDisplayName().getUnformattedText()));
				}
			}

		}
	}
	/**
	 * returns an EntityItem of a skull with the skin of the string specified.
	 * @param event
	 * @param skinName
	 * @return
	 */
	private EntityItem getCustomHead(LivingDeathEvent event, String skinName)
	{
		EntityItem item = new EntityItem(event.entity.worldObj, event.entity.posX, event.entity.posY, event.entity.posZ, new ItemStack(Items.skull, 1, 3));
		if(!item.getEntityItem().hasTagCompound())
		{
			item.getEntityItem().setTagCompound(new NBTTagCompound());
		}
		item.getEntityItem().getTagCompound().setString("SkullOwner", skinName);
		return item;
	}
	
	@SubscribeEvent
	public void onLivingEvent(LivingUpdateEvent event)
	{
		if(event.entity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)event.entity;
			if(player.inventory.armorInventory[2] != null && player.inventory.armorInventory[2].getItem() == WYEMItem.enderChestplate)
			{
				/* Teleport when in water */
				if(player.handleWaterMovement())
				{
					teleportFromDamage((EntityPlayer)event.entity);
				}
				/* Spawn particles around player when wearing ender chestplate */
				for (int k = 0; k < 2; ++k)
				{
					player.worldObj.spawnParticle(EnumParticleTypes.PORTAL, player.posX + (this.rand.nextDouble() - 0.5D) * (double)player.width, player.posY + this.rand.nextDouble() * (double)player.height - 0.25D, player.posZ + (this.rand.nextDouble() - 0.5D) * (double)player.width, (this.rand.nextDouble() - 0.5D) * 2.0D, -this.rand.nextDouble(), (this.rand.nextDouble() - 0.5D) * 2.0D);
				}
			}
		}
		if(event.entity instanceof EntitySkeleton)
		{
			EntitySkeleton skel = (EntitySkeleton)event.entity;
			//0= skeleton, 1= wither skeleton 
			if(skel.getSkeletonType() == 0)
			{
				Entity target = skel.getAITarget();
				if(target instanceof EntityPlayer)
				{
					ItemStack item = ((EntityPlayer) target).inventory.armorInventory[3];
					if(item != null && item.getItem().getUnlocalizedName().contains("skeleton_crown"));
					{
						skel.setAttackTarget(null);
						skel.setRevengeTarget(null);
					}
				}
			}
			else
			{
				//If entity is a wither skeleton 
				Entity target = skel.getAITarget();
				if(target instanceof EntityPlayer)
				{
					ItemStack item = ((EntityPlayer) target).inventory.armorInventory[3];
					if(item != null && item.getItem().getUnlocalizedName().contains("wither_crown"));
					{
						skel.setAttackTarget(null);
						skel.setRevengeTarget(null);
					}
				}
			}
		}
		else if(event.entity instanceof EntityZombie)
		{
			EntityZombie zomb = (EntityZombie)event.entity;
			Entity target = zomb.getAITarget();
			if(target instanceof EntityPlayer)
			{
				if(target instanceof EntityPlayer)
				{
					ItemStack item = ((EntityPlayer) target).inventory.armorInventory[3];
					if(item != null && item.getItem().getUnlocalizedName().contains("zombie_crown"));
					{
						zomb.setAttackTarget(null);
						zomb.setRevengeTarget(null);
					}
				}
			}
		}
		else if (event.entity instanceof EntityCreeper)
		{
			EntityCreeper crep = (EntityCreeper)event.entity;
			Entity target = crep.getAITarget();
			if(target instanceof EntityPlayer)
			{
				ItemStack item = ((EntityPlayer) target).inventory.armorInventory[3];
				if(item != null && item.getItem().getUnlocalizedName().contains("creeper_crown"));
				{
					crep.setAttackTarget(null);
					crep.setRevengeTarget(null);
				}
			}
		}
	}

	private static void teleportFromDamage(EntityPlayer player)
	{
		double xpos = ((player.posX-5.0d) + (double)rand.nextInt(10));
		double ypos = ((player.posY-5.0d) + (double)rand.nextInt(10));
		double zpos = ((player.posZ-5.0d) + (double)rand.nextInt(10));
		while(!player.worldObj.isAirBlock(new BlockPos(xpos, ypos, zpos)))
		{
			ypos += 1.0d;
		}
		player.setPositionAndUpdate(xpos, ypos, zpos);
		player.worldObj.playSoundEffect(xpos, ypos, zpos, "mob.endermen.portal", 1.0F, 1.0F);
	}
}
