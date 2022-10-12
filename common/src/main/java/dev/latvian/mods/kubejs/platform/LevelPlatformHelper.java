package dev.latvian.mods.kubejs.platform;

import com.google.common.base.Suppliers;
import dev.latvian.mods.kubejs.core.InventoryKJS;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ServiceLoader;
import java.util.function.Supplier;

public interface LevelPlatformHelper {

	Supplier<LevelPlatformHelper> INSTANCE = Suppliers.memoize(() -> {
		var serviceLoader = ServiceLoader.load(LevelPlatformHelper.class);
		return serviceLoader.findFirst().orElseThrow(() -> new RuntimeException("Could not find platform implementation for LevelPlatformHelper!"));
	});

	static LevelPlatformHelper get() {
		return INSTANCE.get();
	}

	@Nullable
	InventoryKJS getInventoryFromBlockEntity(BlockEntity tileEntity, Direction facing);

	boolean areCapsCompatible(ItemStack a, ItemStack b);

	double getReachDistance(LivingEntity livingEntity);
}