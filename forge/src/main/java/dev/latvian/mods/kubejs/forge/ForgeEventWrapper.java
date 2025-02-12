package dev.latvian.mods.kubejs.forge;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;

public class ForgeEventWrapper {
	public static Object onEvent(Object eventClass, ForgeEventConsumer consumer) {
		if (!(eventClass instanceof CharSequence || eventClass instanceof Class)) {
			throw new RuntimeException("Invalid syntax! ForgeEvents.onEvent(eventType, function) requires event class and handler");
		} else if (!KubeJS.getStartupScriptManager().firstLoad) {
			ConsoleJS.STARTUP.warn("ForgeEvents.onEvent() can't be reloaded! You will have to restart the game for changes to take effect.");
			return null;
		}

		try {
			Class type = eventClass instanceof Class<?> c ? c : Class.forName(eventClass.toString());
			MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, type, consumer);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		return null;
	}

	public static Object onGenericEvent(Object eventClass, Object genericClass, GenericForgeEventConsumer consumer) {
		if (!(eventClass instanceof CharSequence || eventClass instanceof Class) || !(genericClass instanceof CharSequence || genericClass instanceof Class)) {
			throw new RuntimeException("Invalid syntax! ForgeEvents.onGenericEvent(eventType, genericType, function) requires event class, generic class and handler");
		} else if (!KubeJS.getStartupScriptManager().firstLoad) {
			ConsoleJS.STARTUP.warn("ForgeEvents.onGenericEvent() can't be reloaded! You will have to restart the game for changes to take effect.");
			return null;
		}

		try {
			Class type = eventClass instanceof Class<?> c ? c : Class.forName(eventClass.toString());
			Class genericType = genericClass instanceof Class<?> c ? c : Class.forName(genericClass.toString());
			MinecraftForge.EVENT_BUS.addGenericListener(genericType, EventPriority.NORMAL, false, type, consumer);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		return null;
	}
}
