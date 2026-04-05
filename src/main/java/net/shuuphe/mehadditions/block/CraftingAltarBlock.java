package net.shuuphe.mehadditions.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shuuphe.mehadditions.screen.CraftingAltarScreenHandler;

public class CraftingAltarBlock extends HorizontalFacingBlock {

    public static final MapCodec<CraftingAltarBlock> CODEC = createCodec(CraftingAltarBlock::new);

    private static final Text TITLE = Text.literal("Crafting Altar");

    public CraftingAltarBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState()
                .with(Properties.HORIZONTAL_FACING, net.minecraft.util.math.Direction.NORTH));
    }

    @Override
    protected MapCodec<CraftingAltarBlock> getCodec() { return CODEC; }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.HORIZONTAL_FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(Properties.HORIZONTAL_FACING,
                ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos,
                                 PlayerEntity player, BlockHitResult hit) {
        if (world instanceof ServerWorld) {
            var ctx = net.minecraft.screen.ScreenHandlerContext.create(world, pos);
            player.openHandledScreen(new SimpleNamedScreenHandlerFactory(
                    (syncId, inv, p) -> new CraftingAltarScreenHandler(syncId, inv, ctx),
                    TITLE
            ));
        }
        return ActionResult.SUCCESS;
    }
}
