package net.shuuphe.mehadditions;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.shuuphe.mehadditions.block.OriginsTableBlock;

public class ModBlocks {

    private static RegistryKey<Block> blockKey(String path) {
        return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(MehAdditions.MOD_ID, path));
    }

    private static RegistryKey<Item> itemKey(String path) {
        return RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MehAdditions.MOD_ID, path));
    }

    public static final OriginsTableBlock ORIGINS_TABLE = new OriginsTableBlock(
            AbstractBlock.Settings.create()
                    .strength(5.0f, 1200.0f)
                    .requiresTool()
                    .registryKey(blockKey("origins_table"))
    );

    public static void register() {
        Registry.register(Registries.BLOCK,
                Identifier.of(MehAdditions.MOD_ID, "origins_table"), ORIGINS_TABLE);
        Registry.register(Registries.ITEM,
                Identifier.of(MehAdditions.MOD_ID, "origins_table"),
                new BlockItem(ORIGINS_TABLE,
                        new Item.Settings().registryKey(itemKey("origins_table"))));
    }
}