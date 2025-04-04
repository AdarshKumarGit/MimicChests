package org.chubby.github.mmimicchests.events;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.chubby.github.mmimicchests.Config;
import org.chubby.github.mmimicchests.Constants;
import org.chubby.github.mmimicchests.entity.AbstractChestEntity;
import org.chubby.github.mmimicchests.registry.ModEntities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonEvents {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide()) {
            return;
        }

        Player player = event.getEntity();
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState blockState = level.getBlockState(pos);
        Block block = blockState.getBlock();
        RandomSource random = level.getRandom();

        if (block instanceof ChestBlock) {
            BlockEntity blockEntity = level.getBlockEntity(pos);

            if (blockEntity instanceof ChestBlockEntity chestBlockEntity) {
                if (random.nextDouble() < Config.COMMON.mimicChestSpawnChance.get()) {
                    event.setCanceled(true);

                    List<ItemStack> chestItems = new ArrayList<>();
                    for (int i = 0; i < chestBlockEntity.getContainerSize(); i++) {
                        ItemStack stack = chestBlockEntity.getItem(i);
                        if (!stack.isEmpty()) {
                            chestItems.add(stack.copy());
                        }
                    }

                    convertChestToMimic(player, (ServerLevel) level, pos, chestItems);
                }
            }
        }
    }

    private static void convertChestToMimic(Player player, ServerLevel level, BlockPos pos, List<ItemStack> items) {
        level.removeBlock(pos, false);

        EntityType<?> mimicEntityType = Objects.requireNonNull(ModEntities.MIMIC_CHEST.get().create(level)).getType();

        Entity mimicEntity = mimicEntityType.spawn(
                level,
                ItemStack.EMPTY,
                null,
                pos,
                MobSpawnType.CONVERSION,
                true,
                false
        );

        if (mimicEntity instanceof AbstractChestEntity mimicChest) {
            mimicChest.setStoredItems(items);
            mimicChest.openChest(player);
        }
    }
}