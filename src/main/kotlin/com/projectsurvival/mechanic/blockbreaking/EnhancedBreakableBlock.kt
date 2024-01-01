package com.projectsurvival.mechanic.blockbreaking

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.projectsurvival.BlocksRepository
import com.projectsurvival.extensions.getBlockEntity
import eu.pb4.polymer.core.api.block.PolymerBlock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.WorldAccess
import net.minecraft.world.explosion.Explosion
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.random.Random
import kotlin.reflect.full.primaryConstructor


class EnhancedBreakableBlock(
    private val ref: Block,
    val breakingSettings: Settings
) : Block(ref.settings), PolymerBlock, BlockEntityProvider {
    val coroutine = CoroutineScope(EmptyCoroutineContext)
    data class Settings(
        val maxHp: Int
    ) {
        companion object {
            val CODEC: Codec<Settings> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.INT.fieldOf("maxHp").forGetter(Settings::maxHp)
                ).apply(instance, Settings::class.primaryConstructor!!::call)
            }
        }
    }

    override fun getPolymerBlock(state: BlockState?): Block {
        return ref
    }

    override fun onPolymerBlockSend(blockState: BlockState, pos: BlockPos.Mutable, player: ServerPlayerEntity) {
        val blockEntity = player.serverWorld.getBlockEntity<EnhancedBreakableBlockEntity>(pos)!!
        val data = blockEntity.data ?: EnhancedBreakableBlockEntity.Data(300)
        val mapHp = mapHp(data.hp, breakingSettings.maxHp)
        player.networkHandler.sendPacket(
            BlockBreakingProgressS2CPacket(Random.nextInt(), pos, mapHp)
        )
    }

    private fun mapHp(hp: Int, maxHp: Int): Int {
        return if (maxHp > 0) (9 - hp * 10 / maxHp).coerceIn(-1, 9) else -1
    }

    override fun createBlockEntity(pos: BlockPos?, state: BlockState?): BlockEntity {
        return EnhancedBreakableBlockEntity(
            BlocksRepository.BlockEntitiesRepository.TEST_BRICL_BLOCK_ENTITY, pos, state
        )
    }

    override fun afterBreak(
        world: World,
        player: PlayerEntity,
        pos: BlockPos,
        state: BlockState,
        blockEntity: BlockEntity?,
        tool: ItemStack
    ) {
        val data = (blockEntity as? EnhancedBreakableBlockEntity)?.data ?: EnhancedBreakableBlockEntity.Data(300)
        data.hp -= 100

        if (data.hp <= 0) return

//        world.setBlockState(pos, state, NOTIFY_ALL)
        world.addBlockEntity(blockEntity)
        (world.getBlockEntity(pos) as? EnhancedBreakableBlockEntity)?.let {
            it.data = data
        }
    }
}