package playerWeight.misc;

import java.util.function.Consumer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

public class JsonHelper
{
	public static void convertToObject(JsonElement el, Consumer<JsonObject> receiver)
	{
		if(el == null)
		{
			return;
		}
		if(el.isJsonArray())
		{
			for(JsonElement element : el.getAsJsonArray())
			{
				if(element.isJsonObject())
				{
					receiver.accept(element.getAsJsonObject());
				}
			}
		}
		else if(el.isJsonObject())
		{
			receiver.accept(el.getAsJsonObject());
		}
	}
	
	public static void convertToPrimitive(JsonElement el, Consumer<JsonPrimitive> receiver)
	{
		if(el == null)
		{
			return;
		}
		if(el.isJsonArray())
		{
			for(JsonElement element : el.getAsJsonArray())
			{
				if(element.isJsonPrimitive())
				{
					receiver.accept(element.getAsJsonPrimitive());
				}
			}
		}
		else if(el.isJsonPrimitive())
		{
			receiver.accept(el.getAsJsonPrimitive());
		}
	}
	
	public static boolean getOrDefault(JsonObject obj, String id, boolean defaultValue)
	{
		if(obj.has(id))
		{
			return obj.get(id).getAsBoolean();
		}
		return defaultValue;
	}
	
	public static int getOrDefault(JsonObject obj, String id, int defaultValue)
	{
		if(obj.has(id))
		{
			return obj.get(id).getAsInt();
		}
		return defaultValue;
	}
	
	public static long getOrDefault(JsonObject obj, String id, long defaultValue)
	{
		if(obj.has(id))
		{
			return obj.get(id).getAsLong();
		}
		return defaultValue;
	}
	
	public static float getOrDefault(JsonObject obj, String id, float defaultValue)
	{
		if(obj.has(id))
		{
			return obj.get(id).getAsFloat();
		}
		return defaultValue;
	}
	
	public static double getOrDefault(JsonObject obj, String id, double defaultValue)
	{
		if(obj.has(id))
		{
			return obj.get(id).getAsDouble();
		}
		return defaultValue;
	}
	
	public static String getOrDefault(JsonObject obj, String id, String defaultValue)
	{
		if(obj.has(id))
		{
			return obj.get(id).getAsString();
		}
		return defaultValue;
	}
	
	public static PotionEffect createPotionEffect(JsonObject obj)
	{
		Potion potion = Potion.REGISTRY.getObject(new ResourceLocation(obj.get("name").getAsString()));
		if(potion == null)
		{
			return null;
		}
		int duration = obj.get("duration").getAsInt();
		int amplifier = getOrDefault(obj, "amplifier", 0);
		boolean showParticals = getOrDefault(obj, "particles", true);
		return new PotionEffect(potion, duration, amplifier, true, showParticals);
	}
}
