package playerWeight.api;

import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;

public interface IWeightEffect
{

	public void applyToPlayer(EntityPlayer player, double weight, double maxWeight, IAttributeInstance maxWeightInstance);
	
	public void onPlayerUnloaded(EntityPlayer player);
	
	public void clearEffects(EntityPlayer player);
	
	public double minWeight();
	
	public double maxWeight();
	
	public boolean isPassive();
	
}
