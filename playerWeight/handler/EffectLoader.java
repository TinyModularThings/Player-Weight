package playerWeight.handler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraftforge.fml.common.FMLLog;
import playerWeight.api.IWeightEffect;
import playerWeight.api.WeightRegistry;
import playerWeight.misc.JsonHelper;

public class EffectLoader
{
	JsonParser json = new JsonParser();
	List<String> errors = new ArrayList<String>();
	
	public void loadEffects(File file)
	{
		JsonElement element = readText(toText(file));
		if(element == null || !element.isJsonObject())
		{
			errors.add("Starting Element is not a Object. A File has to start as a Object");
			return;
		}
		JsonHelper.convertToObject(element.getAsJsonObject().get("effects"), new Consumer<JsonObject>(){
			@Override
			public void accept(JsonObject t)
			{
				handleElements(t);
			}
		});
	}
	
	private void handleElements(JsonObject obj)
	{
		Function<JsonObject, IWeightEffect> builder = WeightRegistry.INSTANCE.getWeightEffect(obj.get("type").getAsString());
		if(builder == null)
		{
			errors.add("Object ["+obj+"] results into a Effect that doesnt Exists!");
			return;
		}
		try
		{
			IWeightEffect effect = builder.apply(obj);
			if(effect == null)
			{
				errors.add("Obj ["+obj+"] causes a null Object please report to the dev");
				return;
			}
			PlayerHandler.INSTANCE.addEffect(effect);
		}
		catch(Exception e)
		{
			errors.add("Error happend during creation of effect in this Object ["+obj+"] error: "+e.getMessage());
			e.printStackTrace();
		}
	}
	
	private JsonElement readText(String s)
	{
		try
		{
			return json.parse(s);
		}
		catch(Exception e)
		{
			errors.add(e.getMessage());
		}
		return null;
	}
	
	public String toText(File file)
	{
		try
		{
			return Files.toString(file, Charsets.UTF_8);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return "";
	}
	
	public List<String> getErrors()
	{
		List<String> newList = new ArrayList<String>(errors);
		errors.clear();
		return newList;
	}
	
	public boolean hasErrors()
	{
		return errors.size() > 0;
	}
}
