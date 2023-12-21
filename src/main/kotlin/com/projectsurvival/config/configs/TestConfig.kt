package com.projectsurvival.config.configs

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.projectsurvival.config.core.Config
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.util.Uuids
import java.util.UUID
import kotlin.reflect.full.primaryConstructor

data class TestConfig(
    val number: Int = 1,
    val uuid: UUID = UUID.randomUUID(),
    val itemStack: ItemStack = ItemStack.EMPTY,
    val blockState: BlockState = Blocks.AIR.defaultState
): Config<TestConfig> {
    companion object: Config.CodecProvider<TestConfig> {
        override val CODEC: Codec<TestConfig> = RecordCodecBuilder.create { instance ->
            return@create instance.group(
                Codec.INT.fieldOf("number").forGetter(TestConfig::number),
                Uuids.STRING_CODEC.fieldOf("uuid").forGetter(TestConfig::uuid),
                ItemStack.CODEC.fieldOf("itemStack").forGetter(TestConfig::itemStack),
                BlockState.CODEC.fieldOf("block").forGetter(TestConfig::blockState)
            ).apply(instance, TestConfig::class.primaryConstructor!!::call)
        }
    }
}