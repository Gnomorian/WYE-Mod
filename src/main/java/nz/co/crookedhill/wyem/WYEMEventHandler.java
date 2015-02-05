package nz.co.crookedhill.wyem;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import nz.co.crookedhill.wyem.item.WYEMItem;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

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
			for(ItemStack item : player.inventory.armorInventory)
			{
				if(item != null && item.getItem() == WYEMItem.enderChestplate && rand.nextFloat() < WYEMConfigHelper.enderTeleportChance) // 10% chance to cancel and teleport.
				{
					event.setCanceled(true);
					event.source.getSourceOfDamage().setDead();
					/* this line exists because .setDead doesnt hapen streat away, so the player can still get damaged */
					event.source.getSourceOfDamage().setPosition(event.source.getSourceOfDamage().posX, event.source.getSourceOfDamage().posY+1000.0d, event.source.getSourceOfDamage().posZ);
					teleportFromDamage((EntityPlayer)event.entity);
					item.attemptDamageItem(3, rand);
					break;
				}
			}
		}	
	}
	
	@SubscribeEvent
	public void onJoinEvent(EntityJoinWorldEvent event)
	{
		if(event.entity instanceof EntityZombie)
		{
			System.out.println("a zombie joined the world");
			EntityZombie zomb = (EntityZombie)event.entity;
			List list = zomb.targetTasks.taskEntries;
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
							System.out.println("removing old ai");
							((EntityAITaskEntry)list.get(i)).action = new EntityAINearestModified(zomb, EntityPlayer.class, 0, true, "zombie_crown");
						}
					}
				}
			}
		}
		if(event.entity instanceof EntityCreeper)
		{
			System.out.println("a zombie joined the world");
			EntityCreeper crep = (EntityCreeper)event.entity;
			List list = crep.targetTasks.taskEntries;
			for(int i = 0; i < crep.targetTasks.taskEntries.size(); i++)
			{
				if(list.get(i) instanceof EntityAITaskEntry)
				{
					if(((EntityAITaskEntry)list.get(i)).action instanceof EntityAINearestAttackableTarget)
					{
						System.out.println("removing old ai");
						((EntityAITaskEntry)list.get(i)).action = new EntityAINearestModified(crep, EntityPlayer.class, 0, true, "creeper_crown");
					}
				}
			}
		}
		/* if wither skeleton */
		if(event.entity instanceof EntitySkeleton && ((EntitySkeleton)event.entity).getSkeletonType() == 1)
		{
			System.out.println("a zombie joined the world");
			EntitySkeleton skel = (EntitySkeleton)event.entity;
			List list = skel.targetTasks.taskEntries;
			for(int i = 0; i < skel.targetTasks.taskEntries.size(); i++)
			{
				if(list.get(i) instanceof EntityAITaskEntry)
				{
					if(((EntityAITaskEntry)list.get(i)).action instanceof EntityAINearestAttackableTarget)
					{
						System.out.println("removing old ai");
						((EntityAITaskEntry)list.get(i)).action = new EntityAINearestModified(skel, EntityPlayer.class, 0, true, "wither_crown");
					}
				}
			}
		}
		/* if regular skeleton */
		if(event.entity instanceof EntitySkeleton && ((EntitySkeleton)event.entity).getSkeletonType() == 0)
		{
			System.out.println("a zombie joined the world");
			EntitySkeleton skel = (EntitySkeleton)event.entity;
			List list = skel.targetTasks.taskEntries;
			for(int i = 0; i < skel.targetTasks.taskEntries.size(); i++)
			{
				if(list.get(i) instanceof EntityAITaskEntry)
				{
					if(((EntityAITaskEntry)list.get(i)).action instanceof EntityAINearestAttackableTarget)
					{
						System.out.println("removing old ai");
						((EntityAITaskEntry)list.get(i)).action = new EntityAINearestModified(skel, EntityPlayer.class, 0, true, "skeleton_crown");
					}
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onHurtEvent(LivingHurtEvent event)
	{
		if(!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayer && event.entity.handleLavaMovement())
		{
			for(ItemStack item : ((EntityPlayer) event.entity).inventory.armorInventory)
			{
				if(item != null && item.getItem() == WYEMItem.enderChestplate)
				{
					teleportFromDamage((EntityPlayer)event.entity);
					item.attemptDamageItem(3, rand);
					break;
				}
			}
		}
		else if(event.entity instanceof EntityPlayer && event.source.getDamageType() == "fall")
		{
			for(ItemStack item : ((EntityPlayer)event.entity).inventory.armorInventory)
			{
				if(item.getItem() == WYEMItem.spiderTreads)
				{
					event.ammount -= (event.ammount * WYEMConfigHelper.spiderDamageReduction);
					item.attemptDamageItem(3, rand);
					break;
				}
			}
		}
	}

	@SubscribeEvent
	public void onDeathEvent(LivingDeathEvent event)
	{
		if(event.source.getEntity() instanceof EntityPlayer && ((EntityPlayer)event.source.getEntity()).getHeldItem().getItem() == WYEMItem.headCollector)
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
				}
				if(rand.nextDouble() <= WYEMConfigHelper.headCollectorChance)
				{
					if(event.entity instanceof EntitySkeleton)
					{
						EntitySkeleton skele = (EntitySkeleton)event.entity;
						/* is normal skeleton */
						if(((EntitySkeleton)event.entity).getSkeletonType() == 0)
						{
							EntityItem item = new EntityItem(event.entity.worldObj, event.entity.posX, event.entity.posY, event.entity.posZ, new ItemStack(Items.skull, 1, 0));
							event.entity.worldObj.spawnEntityInWorld(item);
						}
						/* is wither skeleton */
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
					else if(event.entity instanceof EntityPlayer)
					{
						EntityItem item = new EntityItem(event.entity.worldObj, event.entity.posX, event.entity.posY, event.entity.posZ, new ItemStack(Items.skull, 1, 3));
						if(!item.getEntityItem().hasTagCompound())
						{
							item.getEntityItem().stackTagCompound = new NBTTagCompound();
						}
						item.getEntityItem().getTagCompound().setString("SkullOwner", ((EntityPlayer)event.source.getEntity()).getDisplayName());
						event.entity.worldObj.spawnEntityInWorld(item);
					}
				}
			}

		}
	}
	@SubscribeEvent
	public void onLivingEvent(LivingUpdateEvent event)
	{
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
						skel.setTarget(null);
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
						skel.setTarget(null);
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
						zomb.setTarget(null);
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
					crep.setTarget(null);
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
		while(!player.worldObj.isAirBlock((int)xpos, (int)ypos, (int)zpos))
		{
			ypos += 1.0d;
		}
		player.setPositionAndUpdate(xpos, ypos, zpos);
	}
}
