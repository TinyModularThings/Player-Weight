package playerWeight.handler;

import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import playerWeight.api.WeightRegistry;

public final class ClientHandler
{
	public static final ClientHandler INSTANCE = new ClientHandler();
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onPlayerConstruct(EntityConstructing event)
	{
		Entity entity = event.getEntity();
		if(entity instanceof EntityPlayerSP)
		{
			AbstractAttributeMap map = ((EntityPlayerSP)entity).getAttributeMap();
			map.registerAttribute(WeightRegistry.WEIGHT);
			map.registerAttribute(WeightRegistry.MAX_WEIGHT);
		}
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onToolTipEvent(ItemTooltipEvent event)
	{
		ItemStack stack = event.getItemStack();
		if(stack.isEmpty())
		{
			return;
		}
		double weight = WeightRegistry.INSTANCE.getWeight(stack);
		FluidStack fluid = FluidUtil.getFluidContained(stack);
		if(fluid != null)
		{
			weight += WeightRegistry.INSTANCE.getWeight(fluid);
		}
		List<String> tooltips = event.getToolTip();
		tooltips.add("Item Weight: " + createToolTip(weight));
		weight *= (double)stack.getCount();
		tooltips.add("Stack Weight: " + createToolTip(weight));
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onRenderEvent(RenderGameOverlayEvent.Post event)
	{
		if(event.getType() != ElementType.ALL)
		{
			return;
		}
		GlStateManager.disableBlend();
		FontRenderer fontrenderer = Minecraft.getMinecraft().fontRenderer;
		ScaledResolution scaledresolution = event.getResolution();
		AbstractAttributeMap map = Minecraft.getMinecraft().player.getAttributeMap();
		String s = createToolTip(map.getAttributeInstance(WeightRegistry.WEIGHT).getBaseValue()) + "/" + createToolTip(map.getAttributeInstance(WeightRegistry.MAX_WEIGHT).getAttributeValue());
		int i = (scaledresolution.getScaledWidth() / 2) + 90 - fontrenderer.getStringWidth(s);
		int b0 = scaledresolution.getScaledHeight() - 55;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		
		fontrenderer.drawStringWithShadow(s, i, b0 - 2, 16777215);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableBlend();
	}
	
	private String createToolTip(double weight)
	{
		if(weight >= 1000D)
		{
			weight /= 1000;
			return ItemStack.DECIMALFORMAT.format(weight) + "T";
		}
		else if(weight >= 1D)
		{
			return ItemStack.DECIMALFORMAT.format(weight) + "Kg";
		}
		else if(weight >= 0.001)
		{
			weight *= 1000;
			return ItemStack.DECIMALFORMAT.format(weight) + "g";
		}
		else
		{
			weight *= 1000000;
			return ItemStack.DECIMALFORMAT.format(weight) + "mg";
		}
	}
}
