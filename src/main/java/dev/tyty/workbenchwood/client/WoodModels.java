package dev.tyty.workbenchwood.client;

import dev.tyty.workbenchwood.CraftingNames;

import fuzs.visualworkbench.handler.BlockConversionHandler;

import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ModelEvent;

/**
 * Visual Workbench copies a crafting table's model onto its substitute once, when its
 * own client hook runs. Tables registered under another name can miss that copy and
 * keep the missing-model placeholder, so the 3D block never appears. This fills only
 * those gaps from the original wood model.
 */
public final class WoodModels {
    private WoodModels() {
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(WoodModels::onModifyBaking);
    }

    private static void onModifyBaking(ModelEvent.ModifyBakingResult event) {
        var models = event.getModels();
        BakedModel missing = models.get(ModelBakery.MISSING_MODEL_VARIANT);
        for (var entry : BlockConversionHandler.getBlockConversions().entrySet()) {
            ResourceLocation id = BuiltInRegistries.BLOCK.getKey(entry.getKey());
            if (id == null || !CraftingNames.matches(id.getNamespace(), id.getPath())) {
                continue;
            }
            copy(models, missing, entry.getKey(), entry.getValue());
        }
    }

    private static void copy(java.util.Map<ModelResourceLocation, BakedModel> models, BakedModel missing, Block original,
            Block substitute) {
        for (BlockState originalState : original.getStateDefinition().getPossibleStates()) {
            BlockState substituteState = substitute.withPropertiesOf(originalState);
            ModelResourceLocation from = BlockModelShaper.stateToModelLocation(originalState);
            ModelResourceLocation to = BlockModelShaper.stateToModelLocation(substituteState);
            BakedModel current = models.get(to);
            if (current != null && current != missing) {
                continue;
            }
            BakedModel model = models.get(from);
            if (model != null && model != missing) {
                models.put(to, model);
            }
        }
    }
}
