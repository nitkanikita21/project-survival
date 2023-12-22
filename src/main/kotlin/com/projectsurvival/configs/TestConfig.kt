package com.projectsurvival.configs

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.projectsurvival.serializing.CodecSerializable
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
    val blockState: BlockState = Blocks.AIR.defaultState,
    val map: Map<String, String> = mapOf(
        "1" to "hello",
        "2" to "world"
    )
): CodecSerializable<TestConfig> {
    companion object: CodecSerializable.CodecProvider<TestConfig> {
        override val CODEC: Codec<TestConfig> = RecordCodecBuilder.create { instance ->
            return@create instance.group(
                Codec.INT.fieldOf("number").forGetter(TestConfig::number),
                Uuids.STRING_CODEC.fieldOf("uuid").forGetter(TestConfig::uuid),
                ItemStack.CODEC.fieldOf("itemStack").forGetter(TestConfig::itemStack),
                BlockState.CODEC.fieldOf("block").forGetter(TestConfig::blockState),
                Codec.unboundedMap(Codec.STRING, Codec.STRING).fieldOf("map").forGetter(TestConfig::map)
            ).apply(instance, TestConfig::class.primaryConstructor!!::call)
        }
    }
}