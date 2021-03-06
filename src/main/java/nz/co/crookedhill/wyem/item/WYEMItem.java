package nz.co.crookedhill.wyem.item;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import nz.co.crookedhill.wyem.WYEM;

public class WYEMItem 
{
	public static Item skeletonCrown;
	public static Item creeperCrown;
	public static Item zombieCrown;
	public static Item witherCrown;
	public static Item enderChestplate;
	public static Item spiderTreads;
	public static Item headCollector;
	
	public static void init()
	{		
		/* Register Items */
		GameRegistry.registerItem(skeletonCrown = new WYEMItemCrown("skeleton_crown", WYEM.MATERIAL, "skeletonCrown", 0), "skeleton_helmet"); //0 for helmet
		GameRegistry.registerItem(creeperCrown = new WYEMItemCrown("creeper_crown", WYEM.MATERIAL, "creeperCrown", 0), "creeper_helmet"); //0 for helmet
		GameRegistry.registerItem(zombieCrown = new WYEMItemCrown("zombie_crown", WYEM.MATERIAL, "zombieCrown", 0), "zombie_helmet"); //0 for helmet
		GameRegistry.registerItem(witherCrown = new WYEMItemCrown("wither_crown", WYEM.MATERIAL, "witherCrown", 0), "wither_helmet"); //0 for helmet
		
		GameRegistry.registerItem(headCollector = new WYEMItemHeadCollector("head_collector"), "head_collector");


		GameRegistry.registerItem(enderChestplate = new WYEMItemArmor("ender_chestplate", WYEM.MATERIAL, "enderChestplate", 1), "ender_chestplate"); // 1 for chestplate
		GameRegistry.registerItem(spiderTreads = new WYEMItemArmor("spider_treads", WYEM.MATERIAL, "spiderTreads", 3), "spider_boots"); // 3 for boots
		
		/* Register Recipes */
		GameRegistry.addRecipe(new ItemStack(WYEMItem.spiderTreads), "i i","ses",'i', Items.iron_ingot, 's', Items.string, 'e', Items.spider_eye);
		GameRegistry.addRecipe(new ItemStack(WYEMItem.enderChestplate), "i i", "gpg", "igi", 'i', Items.iron_ingot, 'p', Items.ender_pearl, 'g', Items.gold_ingot);
		
		GameRegistry.addRecipe(new ItemStack(WYEMItem.creeperCrown), "g g", "ihi", 'g', Items.gold_ingot, 'i', Items.iron_ingot, 'h', new ItemStack(Items.skull, 1, 4));
		GameRegistry.addRecipe(new ItemStack(WYEMItem.zombieCrown), "g g", "ihi", 'g', Items.gold_ingot, 'i', Items.iron_ingot, 'h', new ItemStack(Items.skull, 1, 2));
		GameRegistry.addRecipe(new ItemStack(WYEMItem.skeletonCrown), "g g", "ihi", 'g', Items.gold_ingot, 'i', Items.iron_ingot, 'h', new ItemStack(Items.skull, 1, 0));
		GameRegistry.addRecipe(new ItemStack(WYEMItem.witherCrown), "g g", "ihi", 'g', Items.gold_ingot, 'i', Items.iron_ingot, 'h', new ItemStack(Items.skull, 1, 1));
		
		GameRegistry.addRecipe(new ItemStack(WYEMItem.headCollector), "sss", "sis", "isi", 's', Items.stick, 'i', Items.iron_ingot);
	}
}
