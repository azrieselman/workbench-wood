package dev.tyty.workbenchwood;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class CraftingNamesTest {
    @Test
    void matchesCraftingTableNamesOnly() {
        assertTrue(CraftingNames.matches("minecraft", "crafting_table"));
        assertTrue(CraftingNames.matches("lolmct", "spruce_crafting_table"));
        assertTrue(CraftingNames.matches("everycomp", "q/biomesoplenty/fir_crafting_table"));
        assertTrue(CraftingNames.matches("example", "palm-crafting-table"));
        assertFalse(CraftingNames.matches("minecraft", "smithing_table"));
        assertFalse(CraftingNames.matches("minecraft", "fletching_table"));
        assertFalse(CraftingNames.matches("minecraft", "oak_planks"));
        assertFalse(CraftingNames.matches("visualworkbench", "lolmct/spruce_crafting_table"));
        assertFalse(CraftingNames.matches("workbenchwood", "spruce_crafting_table"));
    }

    @Test
    void modsTomlPinsNeoForgeRange() throws Exception {
        String toml = Files.readString(Path.of("src/main/templates/META-INF/neoforge.mods.toml"));
        assertTrue(toml.contains("versionRange=\"[21.1.1,)\""));
        assertFalse(toml.contains("[${neo_version},)"));
    }
}
