package playerWeight.effects;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import playerWeight.api.IWeightEffect;

public abstract class BaseEffect implements IWeightEffect
{
	boolean passive;
	double min;
	double max;
	boolean effectRidden;
	
	public BaseEffect(double min, double max, boolean passive, boolean effectRidden)
	{
		this(min, max, passive);
		this.effectRidden = effectRidden;
	}
	
	public BaseEffect(double min, double max, boolean passive)
	{
		this.passive = passive;
		this.min = min;
		this.max = max;
		effectRidden = false;
	}
	
	@Override
	public void onPlayerUnloaded(EntityPlayer player)
	{
		
	}
	
	@Override
	public void clearEffects(EntityPlayer player)
	{
		
	}
	
	@Override
	public void onServerStop()
	{
		
	}
	
	@Override
	public double minWeight()
	{
		return min;
	}
	
	@Override
	public double maxWeight()
	{
		return max;
	}
	
	@Override
	public boolean isPassive()
	{
		return passive;
	}
	
	public EntityLivingBase getLowestEntity(EntityPlayer player)
	{
		EntityLivingBase result = player;
		while(result.isRiding() && effectRidden)
		{
			Entity next = result.getRidingEntity();
			if(next instanceof EntityLivingBase)
			{
				result = (EntityLivingBase)next;
				continue;
			}
			break;
		}
		return result;
	}
	
	public Entity getLowest(EntityPlayer player)
	{
		Entity result = player;
		while(result.isRiding())
		{
			result = result.getRidingEntity();
		}
		return result;
	}
	
}
