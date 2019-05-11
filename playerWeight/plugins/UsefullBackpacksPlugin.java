package playerWeight.plugins;

import java.util.function.Function;

import info.u_team.useful_backpacks.enums.EnumBackPacks;
import info.u_team.useful_backpacks.init.UsefulBackPacksItems;
import info.u_team.useful_backpacks.inventory.InventoryBackPack;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import playerWeight.api.WeightRegistry;
import playerWeight.api.plugins.IWeightPlugin;
import playerWeight.api.plugins.WeightPluginListener;

@WeightPluginListener(name = "UsefulBackpacks Plugin", version = "1.0")
public class UsefullBackpacksPlugin implements IWeightPlugin
{
	
	@Override
	public boolean canLoad()
	{
		return Loader.isModLoaded("usefulbackpacks");
	}
	
	@Override
	public void onLoad()
	{
		WeightRegistry.INSTANCE.registerItemHandler(new Function<ItemStack, IItemHandler>(){
			@Override
			public IItemHandler apply(ItemStack t)
			{
				return new InvWrapper(new InventoryBackPack(t, null, EnumBackPacks.byMetadata(t.getMetadata()).getCount()));
			}
		}, UsefulBackPacksItems.backpack);
	}
	
}
