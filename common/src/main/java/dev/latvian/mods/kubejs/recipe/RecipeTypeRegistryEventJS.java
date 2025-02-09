package dev.latvian.mods.kubejs.recipe;

import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.Registries;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSRegistries;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.recipe.minecraft.ShapedRecipeJS;
import dev.latvian.mods.kubejs.recipe.minecraft.ShapelessRecipeJS;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public class RecipeTypeRegistryEventJS extends EventJS {
	private final Map<ResourceLocation, RecipeTypeJS> map;

	public RecipeTypeRegistryEventJS(Map<ResourceLocation, RecipeTypeJS> m) {
		map = m;
	}

	public void register(RecipeTypeJS type) {
		map.put(Registries.getId(type.serializer, Registry.RECIPE_SERIALIZER_REGISTRY), type);
		KubeJS.LOGGER.info("Registered custom recipe handler for type " + type);
	}

	public void register(ResourceLocation id, Supplier<RecipeJS> f) {
		if (!Platform.isModLoaded(id.getNamespace())) {
			return;
		}

		register(new RecipeTypeJS(Objects.requireNonNull(KubeJSRegistries.recipeSerializers().get(id), "Cannot find recipe serializer: " + id), f));
	}

	public void ignore(ResourceLocation id) {
		if (!Platform.isModLoaded(id.getNamespace())) {
			return;
		}

		register(new IgnoredRecipeTypeJS(Objects.requireNonNull(KubeJSRegistries.recipeSerializers().get(id), "Cannot find recipe serializer: " + id)));
	}

	public void registerShaped(ResourceLocation id) {
		register(id, ShapedRecipeJS::new);
	}

	public void registerShapeless(ResourceLocation id) {
		register(id, ShapelessRecipeJS::new);
	}
}