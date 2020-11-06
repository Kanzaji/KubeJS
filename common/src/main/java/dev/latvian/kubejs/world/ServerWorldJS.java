package dev.latvian.kubejs.world;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.latvian.kubejs.player.AttachPlayerDataEvent;
import dev.latvian.kubejs.player.EntityArrayList;
import dev.latvian.kubejs.player.FakeServerPlayerDataJS;
import dev.latvian.kubejs.player.ServerPlayerDataJS;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.server.ServerJS;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraftforge.common.MinecraftForge;

import java.util.stream.Collectors;

/**
 * @author LatvianModder
 */
public class ServerWorldJS extends WorldJS
{
	private final ServerJS server;

	public ServerWorldJS(ServerJS s, ServerLevel w)
	{
		super(w);
		server = s;
	}

	@Override
	public ScriptType getSide()
	{
		return ScriptType.SERVER;
	}

	@Override
	public ServerJS getServer()
	{
		return server;
	}

	public long getSeed()
	{
		return ((ServerLevel) minecraftWorld).getSeed();
	}

	public void setTime(long time)
	{
		((ServerLevelData) minecraftWorld.getLevelData()).setGameTime(time);
	}

	public void setLocalTime(long time)
	{
		((ServerLevelData) minecraftWorld.getLevelData()).setDayTime(time);
	}

	@Override
	public ServerPlayerDataJS getPlayerData(Player player)
	{
		ServerPlayerDataJS data = server.playerMap.get(player.getUUID());

		if (data != null)
		{
			return data;
		}

		FakeServerPlayerDataJS fakeData = server.fakePlayerMap.get(player.getUUID());

		if (fakeData == null)
		{
			fakeData = new FakeServerPlayerDataJS(server, (ServerPlayer) player);
			MinecraftForge.EVENT_BUS.post(new AttachPlayerDataEvent(fakeData));
		}

		fakeData.player = (ServerPlayer) player;
		return fakeData;
	}

	@Override
	public String toString()
	{
		return "ServerWorld:" + getDimension();
	}

	@Override
	public EntityArrayList getEntities()
	{
		return new EntityArrayList(this, ((ServerLevel) minecraftWorld).getEntities().collect(Collectors.toList()));
	}

	public EntityArrayList getEntities(String filter)
	{
		try
		{
			return createEntityList(new EntitySelectorParser(new StringReader(filter), true).getSelector().findEntities(new WorldCommandSender(this)));
		}
		catch (CommandSyntaxException e)
		{
			return new EntityArrayList(this, 0);
		}
	}
}