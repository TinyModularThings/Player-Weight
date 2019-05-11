package playerWeight.plugins;

import java.util.function.Function;

import net.mcft.copy.backpacks.api.BackpackHelper;
import net.mcft.copy.backpacks.api.IBackpack;
import net.mcft.copy.backpacks.api.IBackpackData;
import net.mcft.copy.backpacks.misc.BackpackDataItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.items.IItemHandler;
import playerWeight.api.WeightRegistry;
import playerWeight.api.plugins.IWeightPlugin;
import playerWeight.api.plugins.WeightPluginListener;

@WeightPluginListener(name = "Wearable Backpacks plugin", version = "1.0")
public class WearableBackpacksPlugin implements IWeightPlugin
{
	
	@Override
	public boolean canLoad()
	{
		return Loader.isModLoaded("wearablebackpacks");
	}
	
	@Override
	public void onLoad()
	{
		WeightRegistry.INSTANCE.registerExtraPlayerInventory(new Function<EntityPlayer, IItemHandler>(){
			@Override
			public IItemHandler apply(EntityPlayer t)
			{
				IBackpack pack = BackpackHelper.getBackpack(t);
				if(pack == null)
				{
					return null;
				}
				IBackpackData data = pack.getData();
				if(data == null)
				{
					return null;
				}
				return ((BackpackDataItems)data).getItems();
			}
		});
	}
	
}
