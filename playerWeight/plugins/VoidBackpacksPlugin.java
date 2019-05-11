package playerWeight.plugins;

import java.util.function.Function;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.items.IItemHandler;
import playerWeight.api.WeightRegistry;
import playerWeight.api.plugins.IWeightPlugin;
import playerWeight.api.plugins.WeightPluginListener;
import v0id.api.vsb.capability.IBackpack;
import v0id.api.vsb.data.VSBRegistryNames;

@WeightPluginListener(name = "VoidBackpacks Plugin", version = "1.0")
public class VoidBackpacksPlugin implements IWeightPlugin
{
	
	@Override
	public boolean canLoad()
	{
		return Loader.isModLoaded("v0idssmartbackpacks");
	}
	
	@Override
	public void onLoad()
	{
		Item[] items = new Item[4];
		items[0] = Item.REGISTRY.getObject(VSBRegistryNames.asLocation(VSBRegistryNames.itemBackpack));
		items[1] = Item.REGISTRY.getObject(VSBRegistryNames.asLocation(VSBRegistryNames.itemReinforcedBackpack));
		items[2] = Item.REGISTRY.getObject(VSBRegistryNames.asLocation(VSBRegistryNames.itemAdvancedBackpack));
		items[3] = Item.REGISTRY.getObject(VSBRegistryNames.asLocation(VSBRegistryNames.itemUltimateBackpack));
		WeightRegistry.INSTANCE.registerItemHandler(new Function<ItemStack, IItemHandler>(){
			@Override
			public IItemHandler apply(ItemStack t)
			{
				return IBackpack.of(t).getInventory();
			}
			
		}, items);
	}
	
}
