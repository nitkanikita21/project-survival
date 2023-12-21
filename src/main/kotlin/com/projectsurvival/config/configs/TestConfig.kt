package com.projectsurvival.config.configs

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.projectsurvival.config.core.Config
import com.projectsurvival.leveling.PlayerSkill
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.client.sound.Sound
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryOps
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Uuids
import net.minecraft.world.biome.Biome
import java.util.UUID
import kotlin.reflect.full.primaryConstructor

data class TestConfig(
    val number: Int = 1,
    val uuid: UUID = UUID.randomUUID(),

    val itemStack: ItemStack = ItemStack.EMPTY,
    val blockState: BlockState = Blocks.AIR.defaultState,
    val skill: PlayerSkill = PlayerSkill.SPRINTING_SKILL,
    val soundEvent: SoundEvent = SoundEvents.BLOCK_CHAIN_HIT
): Config<TestConfig> {
    companion object: Config.CodecProvider<TestConfig> {
        override val CODEC: Codec<TestConfig> = RecordCodecBuilder.create { instance ->
            return@create instance.group(
                Codec.INT.fieldOf("number").forGetter(TestConfig::number),
                Uuids.STRING_CODEC.fieldOf("uuid").forGetter(TestConfig::uuid),
                ItemStack.CODEC.fieldOf("itemStack").forGetter(TestConfig::itemStack),
                BlockState.CODEC.fieldOf("blockState").forGetter(TestConfig::blockState),
                PlayerSkill.CODEC.fieldOf("skill").forGetter(TestConfig::skill),
                Registries.SOUND_EVENT.codec.fieldOf("soundEvent").forGetter(TestConfig::soundEvent)
            ).apply(instance, TestConfig::class.primaryConstructor!!::call)
        }
    }
}