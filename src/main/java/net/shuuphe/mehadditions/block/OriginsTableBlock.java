package net.shuuphe.mehadditions.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shuuphe.mehadditions.screen.OriginsTableScreenHandler;

public class OriginsTableBlock extends Block {

    public OriginsTableBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient()) {
            player.openHandledScreen(new SimpleNamedScreenHandlerFactory(
                    (syncId, inventory, p) -> new OriginsTableScreenHandler(
                            syncId, inventory,
                            ScreenHandlerContext.create(world, pos)),
                    Text.literal("Meh Origins Table")
            ));
        }
        return ActionResult.SUCCESS;
    }
}