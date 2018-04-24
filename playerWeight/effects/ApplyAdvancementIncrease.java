package playerWeight.effects;

import java.util.function.Function;

import com.google.gson.JsonObject;

import net.minecraft.advancements.Advancement;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import playerWeight.api.IWeightEffect;
import playerWeight.api.WeightRegistry;

public class ApplyAdvancementIncrease extends BaseEffect
{
	ResourceLocation location;
	AttributeModifier mods;
	Advancement adv;
	
	public ApplyAdvancementIncrease(JsonObject obj)
	{
		super(0, Double.MAX_VALUE, true);
		location = new ResourceLocation(obj.get("name").getAsString());
		mods = new AttributeModifier(obj.get("effectName").getAsString(), obj.get("amount").getAsDouble(), obj.get("effectType").getAsInt());
	}
	
	@Override
	public void applyToPlayer(EntityPlayer player, double weight, double maxWeight, IAttributeInstance maxWeightInstance)
	{
		if(player instanceof EntityPlayerMP && ((EntityPlayerMP)player).getAdvancements().getProgress(getAdv()).isDone())
		{
			if(!maxWeightInstance.hasModifier(mods))
			{
				maxWeightInstance.applyModifier(mods);
			}
		}
	}
	
	@Override
	public void onServerStop()
	{
		adv = null;
	}	
	public Advancement getAdv()
	{
		if(adv == null)
		{
			adv = FMLCommonHandler.instance().getMinecraftServerInstance().getAdvancementManager().getAdvancement(location);
		}
		return adv;
	}
	
	public static void register()
	{
		WeightRegistry.INSTANCE.registerWeightEffect("advancment", new Function<JsonObject, IWeightEffect>(){
			@Override
			public IWeightEffect apply(JsonObject t)
			{
				return new ApplyAdvancementIncrease(t);
			}
		});
	}
}
