package dev.latvian.kubejs.player;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.entity.LivingEntityAttackEventJS;
import dev.latvian.kubejs.entity.LivingEntityDeathEventJS;
import dev.latvian.kubejs.event.EventsJS;
import dev.latvian.kubejs.script.ScriptFile;
import dev.latvian.kubejs.script.ScriptManager;
import dev.latvian.kubejs.server.ServerJS;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = KubeJS.MOD_ID)
public class KubeJSPlayerEventHandler
{
	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
	{
		if (event.player instanceof EntityPlayerMP && (((EntityPlayerMP) event.player).server.isSinglePlayer() || event.player.canUseCommand(1, "kubejs.errors")))
		{
			for (ScriptFile file : ScriptManager.instance.scripts.values())
			{
				ITextComponent component = file.getErrorTextComponent();

				if (component != null)
				{
					event.player.sendMessage(component);
				}
			}
		}

		if (ServerJS.instance != null && event.player instanceof EntityPlayerMP)
		{
			ServerPlayerDataJS p = new ServerPlayerDataJS(ServerJS.instance, event.player.getUniqueID(), event.player.getName());
			p.server.playerMap.put(p.id, p);
			MinecraftForge.EVENT_BUS.post(new AttachPlayerDataEvent(p, p.data));
			EventsJS.post(KubeJSEvents.PLAYER_LOGGED_IN, new SimplePlayerEventJS(event.player));
		}
	}

	@SubscribeEvent
	public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event)
	{
		if (ServerJS.instance == null || !ServerJS.instance.playerMap.containsKey(event.player.getUniqueID()))
		{
			return;
		}

		EventsJS.post(KubeJSEvents.PLAYER_LOGGED_OUT, new SimplePlayerEventJS(event.player));
		ServerJS.instance.playerMap.remove(event.player.getUniqueID());
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onPlayerTick(TickEvent.PlayerTickEvent event)
	{
		if (ServerJS.instance != null && event.phase == TickEvent.Phase.END)
		{
			EventsJS.post(KubeJSEvents.PLAYER_TICK, new SimplePlayerEventJS(event.player));
		}
	}

	@SubscribeEvent
	public static void onPlayerChat(ServerChatEvent event)
	{
		if (EventsJS.post(KubeJSEvents.PLAYER_CHAT, new PlayerChatEventJS(event)))
		{
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onLivingDeath(LivingDeathEvent event)
	{
		if (EventsJS.post(KubeJSEvents.ENTITY_DEATH, new LivingEntityDeathEventJS(event)))
		{
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onLivingAttack(LivingAttackEvent event)
	{
		if (event.getAmount() > 0F && EventsJS.post(KubeJSEvents.ENTITY_ATTACK, new LivingEntityAttackEventJS(event)))
		{
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onAdvancement(AdvancementEvent event)
	{
		EventsJS.post(KubeJSEvents.PLAYER_ADVANCEMENT, new PlayerAdvancementEventJS(event));
	}
}