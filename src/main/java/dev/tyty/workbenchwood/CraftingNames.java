package dev.tyty.workbenchwood;

import java.util.Locale;

/**
 * A crafting table is any block whose id path contains "crafting table"
 * once underscores and hyphens are read as spaces. Visual Workbench's own
 * substitutes, and blocks this mod registers, are not matched again.
 */
public final class CraftingNames {
    private CraftingNames() {
    }

    public static boolean matches(String namespace, String path) {
        if (namespace == null || path == null) {
            return false;
        }
        if ("visualworkbench".equals(namespace) || "workbenchwood".equals(namespace)) {
            return false;
        }
        String normalized = path.toLowerCase(Locale.ROOT).replace('_', ' ').replace('-', ' ');
        return normalized.contains("crafting table");
    }
}
