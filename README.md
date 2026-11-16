# Workbench Wood

NeoForge 1.21.1 add-on (`workbenchwood`) that gives Visual Workbench's 3D crafting layer to every block whose name contains "crafting table". It does not add crafting tables of its own. Wood variants come from other mods, such as More Crafting Tables or Every Compat.

Requires [Visual Workbench](https://modrinth.com/mod/visual-workbench) 21.1.0 or newer (tested against 21.1.2).

## What Visual Workbench is hooked to

Visual Workbench wraps each `CraftingTableBlock` as `visualworkbench:<namespace>/<path>`, copies that block's model, and draws the crafting grid with `CraftingTableBlockEntityRenderer`. The renderer only runs when the substitute is a valid block for Visual Workbench's block entity. Tables added by another mod were wrapped, so they showed up as Visual Workbench blocks, but they were missing from that valid set, so the 3D layer never attached.

This add-on:

1. Leaves `CraftingTableBlock` to Visual Workbench, including More Crafting Tables.
2. Wraps any other block whose id contains "crafting table" through `BlockConversionHandler.onRegistryEntryAdded`, using `CraftingTableWithInventoryBlock`. Underscores and hyphens count as spaces, so `spruce_crafting_table` and `q/mod/fir_crafting_table` match. `visualworkbench` substitutes are not wrapped again.
3. Adds every substitute to Visual Workbench's crafting-table block entity in `BlockEntityTypeAddBlocksEvent`, which is what lets the 3D layer construct.
4. On the client, copies the original wood model onto a substitute only when that substitute is still the missing model.

No mixin. The wood blocks and their recipes stay with the mod that registered them.

## Build

From this directory:

```bash
bash ./gradlew test build
```

`build` writes `workbench-wood.jar` here. The NeoForge dependency in `src/main/templates/META-INF/neoforge.mods.toml` is the literal `versionRange="[21.1.1,)"`.

Java 21. NeoForge 21.1.265, Minecraft 1.21.1, Parchment 2024.11.17. Visual Workbench and Puzzles Lib are `compileOnly`; they are not inside the jar.
