package com.projectsurvival

import com.projectsurvival.mechanic.blockbreaking.EnhancedBreakableBlock
import com.projectsurvival.mechanic.blockbreaking.EnhancedBreakableBlockEntity
import eu.pb4.polymer.core.api.block.PolymerBlockUtils
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

object BlocksRepository {

    lateinit var TEST_BRICK: Block

    object BlockEntitiesRepository {
        lateinit var TEST_BRICL_BLOCK_ENTITY: BlockEntityType<EnhancedBreakableBlockEntity>
        fun register() {
            TEST_BRICL_BLOCK_ENTITY = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                Registries.BLOCK.getId(TEST_BRICK),
                FabricBlockEntityTypeBuilder.create(
                    { a, b -> EnhancedBreakableBlockEntity(TEST_BRICL_BLOCK_ENTITY, a, b) },
                    TEST_BRICK
                ).build()
            )
            PolymerBlockUtils.registerBlockEntity(TEST_BRICL_BLOCK_ENTITY)
        }
    }

    fun register () {
        TEST_BRICK = registerBreakableByDamageBlock(Blocks.BRICKS)
        BlockEntitiesRepository.register()
    }

    private fun registerBreakableByDamageBlock(block: Block): Block {
        val registeredBlock = Registry.register(
            Registries.BLOCK,
            Identifier(Projectsurvival.ID, Registries.BLOCK.getId(block).path + "_with_enhanced_breaking"),
            EnhancedBreakableBlock(block, EnhancedBreakableBlock.Settings(300))
        );

        return registeredBlock
    }
}