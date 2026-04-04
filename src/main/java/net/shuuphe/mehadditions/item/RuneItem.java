package net.shuuphe.mehadditions.item;

import net.minecraft.item.Item;
import net.shuuphe.mehadditions.util.RuneType;

public class RuneItem extends Item {
    private final RuneType runeType;

    public RuneItem(Settings settings, RuneType runeType) {
        super(settings);
        this.runeType = runeType;
    }

    public RuneType getRuneType() {
        return runeType;
    }
}