package playerWeight.plugins;

import java.util.function.Function;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import playerWeight.api.WeightRegistry;
import playerWeight.api.plugins.IWeightPlugin;
import playerWeight.api.plugins.WeightPluginListener;
import ru.poopycoders.improvedbackpacks.init.ModItems;
import ru.poopycoders.improvedbackpacks.inventory.InventoryBackpack;

@WeightPluginListener(name = "Improved Backpacks Compat", version = "1.0")
public class ImprovedBackPacksPlugin implements IWeightPlugin
{
	@Override
	public boolean canLoad()
	{
		return Loader.isModLoaded("improvedbackpacks");
	}
	
	@Override
	public void onLoad()
	{
		WeightRegistry.INSTANCE.registerItemHandler(new Function<ItemStack, IItemHandler>(){
			@Override
			public IItemHandler apply(ItemStack t)
			{
				return new InvWrapper(InventoryBackpack.loadFromBackpack(t));
			}
		}, ModItems.BACKPACK);
	}
	
}
