package dev.tyty.workbenchwood;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import fuzs.puzzleslib.api.event.v1.RegistryEntryAddedCallback;
import fuzs.visualworkbench.handler.BlockConversionHandler;
import fuzs.visualworkbench.init.ModRegistry;
import fuzs.visualworkbench.world.level.block.CraftingTableWithInventoryBlock;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CraftingTableBlock;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.event.BlockEntityTypeAddBlocksEvent;

@Mod(WorkbenchWood.MOD_ID)
public class WorkbenchWood {
    public static final String MOD_ID = "workbenchwood";
    public static final Logger LOGGER = LogUtils.getLogger();

    public WorkbenchWood(IEventBus modBus, ModContainer container) {
        RegistryEntryAddedCallback.registryEntryAdded(Registries.BLOCK).register(WorkbenchWood::onBlockAdded);
        modBus.addListener(EventPriority.HIGHEST, WorkbenchWood::addBlockEntities);
        modBus.addListener(WorkbenchWood::onCommonSetup);
        if (FMLEnvironment.dist.isClient()) {
            dev.tyty.workbenchwood.client.WoodModels.register(modBus);
        }
    }

    private static void onBlockAdded(net.minecraft.core.Registry<Block> registry, ResourceLocation id, Block block,
            java.util.function.BiConsumer<ResourceLocation, java.util.function.Supplier<Block>> registrar) {
        if (!CraftingNames.matches(id.getNamespace(), id.getPath())) {
            return;
        }
        if (block instanceof CraftingTableBlock || block instanceof CraftingTableWithInventoryBlock) {
            return;
        }
        if (BlockConversionHandler.getBlockConversions().containsKey(block)) {
            return;
        }
        BlockConversionHandler.onRegistryEntryAdded(candidate -> candidate == block, CraftingTableWithInventoryBlock::new, MOD_ID)
                .onRegistryEntryAdded(registry, id, block, registrar);
    }

    private static void addBlockEntities(BlockEntityTypeAddBlocksEvent event) {
        List<Block> substitutes = new ArrayList<>(BlockConversionHandler.getBlockConversions().values());
        if (substitutes.isEmpty()) {
            return;
        }
        var type = ModRegistry.CRAFTING_TABLE_BLOCK_ENTITY_TYPE.value();
        Block[] blocks = substitutes.toArray(Block[]::new);
        try {
            event.modify(type, blocks);
        } catch (RuntimeException exception) {
            LOGGER.warn("Could not attach crafting tables to Visual Workbench together", exception);
            for (Block block : blocks) {
                try {
                    event.modify(type, block);
                } catch (RuntimeException one) {
                    LOGGER.warn("Visual Workbench rejected {}", BuiltInRegistries.BLOCK.getKey(block), one);
                }
            }
        }
    }

    private static void onCommonSetup(FMLCommonSetupEvent event) {
        var type = ModRegistry.CRAFTING_TABLE_BLOCK_ENTITY_TYPE.value();
        int ready = 0;
        for (var entry : BlockConversionHandler.getBlockConversions().entrySet()) {
            ResourceLocation id = BuiltInRegistries.BLOCK.getKey(entry.getKey());
            if (id == null || !CraftingNames.matches(id.getNamespace(), id.getPath())) {
                continue;
            }
            if (type.isValid(entry.getValue().defaultBlockState())) {
                ready++;
                LOGGER.info("{} uses {} for the 3D crafting layer", id, BuiltInRegistries.BLOCK.getKey(entry.getValue()));
            } else {
                LOGGER.warn("3D crafting layer is missing for {}", id);
            }
        }
        LOGGER.info("Crafting tables with Visual Workbench's 3D layer: {}", ready);
    }
}
