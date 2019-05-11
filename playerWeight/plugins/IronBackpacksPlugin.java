package playerWeight.plugins;

import java.util.function.Function;

import gr8pefish.ironbackpacks.api.backpack.BackpackInfo;
import gr8pefish.ironbackpacks.core.RegistrarIronBackpacks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.items.IItemHandler;
import playerWeight.api.WeightRegistry;
import playerWeight.api.plugins.IWeightPlugin;
import playerWeight.api.plugins.WeightPluginListener;

@WeightPluginListener(name = "Iron Backpacks plugin", version = "1.0")
public class IronBackpacksPlugin implements IWeightPlugin
{
	
	@Override
	public boolean canLoad()
	{
		return Loader.isModLoaded("ironbackpacks");
	}
	
	@Override
	public void onLoad()
	{
		WeightRegistry.INSTANCE.registerItemHandler(new Function<ItemStack, IItemHandler>(){
			@Override
			public IItemHandler apply(ItemStack t)
			{
				return BackpackInfo.fromStack(t).getInventory();
			}			
		}, RegistrarIronBackpacks.BACKPACK);
	}
	
}
