package com.projectsurvival.leveling

import com.mojang.serialization.Codec
import com.mojang.serialization.Lifecycle
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.projectsurvival.Projectsurvival
import com.projectsurvival.extensions.getData
import com.projectsurvival.extensions.getNbtCompoundData
import com.projectsurvival.extensions.setData
import com.projectsurvival.extensions.setNbtData
import com.projectsurvival.leveling.PlayerSkill.Events.SkillLevelUpEvent
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.SimpleRegistry
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import kotlin.math.abs
import kotlin.reflect.full.primaryConstructor


class PlayerSkill(
    val properties: Properties
) {
    object Events {
        fun interface SkillLevelUpEvent {
            companion object {
                val EVENT: Event<SkillLevelUpEvent> = EventFactory.createArrayBacked(
                    SkillLevelUpEvent::class.java
                ) l@{ listeners ->
                    SkillLevelUpEvent { serverPlayerEntity, playerSkill, data ->
                        listeners.forEach {
                            it.callback(serverPlayerEntity, playerSkill, data)
                        }
                    }
                }
            }

            fun callback(player: ServerPlayerEntity, skill: PlayerSkill, data: Data)
        }
    }

    companion object {
        private val NBT_KEY_SKILLS_INFO = "skills"

        val REGISTRY: Registry<PlayerSkill> = SimpleRegistry(
            RegistryKey.ofRegistry(Identifier(Projectsurvival.ID, "skills")),
            Lifecycle.stable()
        )

        val CODEC: Codec<PlayerSkill> = REGISTRY.codec.stable()

        val TEST_SKILL = Registry.register(
            REGISTRY,
            Identifier(Projectsurvival.ID, "test"),
            PlayerSkill(
                Properties(
                    maxLevel = 12,
                    baseExpGrowth = 100.0,
                    expGrowthModifier = 1.0,
                    baseMaxExpAmount = 200.0,
                    maxExpAmountModifier = 1.0
                )
            )
        )
    }

    data class Properties(
        val maxLevel: Int,
        val baseExpGrowth: Double,
        val expGrowthModifier: Double,
        val baseMaxExpAmount: Double,
        val maxExpAmountModifier: Double,
    ) {
        companion object {
            val CODEC: Codec<PlayerSkill.Properties> = RecordCodecBuilder.create { instance ->
                return@create instance.group(
                    Codec.INT.fieldOf("maxLevel").forGetter(Properties::maxLevel),
                    Codec.DOUBLE.fieldOf("baseExpGrowth").forGetter(Properties::baseExpGrowth),
                    Codec.DOUBLE.fieldOf("expGrowthModifier").forGetter(Properties::expGrowthModifier),
                    Codec.DOUBLE.fieldOf("baseMaxExpAmount").forGetter(Properties::baseMaxExpAmount),
                    Codec.DOUBLE.fieldOf("maxExpAmountModifier").forGetter(Properties::maxExpAmountModifier)
                ).apply(instance, Properties::class.primaryConstructor!!::call)
            }
        }
    }

    data class Data(
        var currentLevel: Int = 0,
        var currentExpAmount: Double = 0.0
    ) {
        companion object {
            val CODEC: Codec<PlayerSkill.Data> = RecordCodecBuilder.create { instance ->
                return@create instance.group(
                    Codec.INT.fieldOf("currentLevel").forGetter(Data::currentLevel),
                    Codec.DOUBLE.fieldOf("currentExpAmount").forGetter(Data::currentExpAmount)
                ).apply(instance, Data::class.primaryConstructor!!::call)
            }
        }
    }

    val id get() = REGISTRY.getId(this)!!

    fun getData(player: ServerPlayerEntity): Data {
        val nbtCompoundData = player.getNbtCompoundData(NBT_KEY_SKILLS_INFO)
        return nbtCompoundData?.getData(id, Data.CODEC) ?: Data()
    }

    private fun setData(player: ServerPlayerEntity, data: Data) {
        val nbtCompoundData = player.getNbtCompoundData(NBT_KEY_SKILLS_INFO) ?: NbtCompound()
        nbtCompoundData.setData(id, Data.CODEC, data)
        player.setNbtData(NBT_KEY_SKILLS_INFO, nbtCompoundData)
    }

    private fun calculateAndWrite(player: ServerPlayerEntity, data: Data) {
        if (data.currentLevel >= properties.maxLevel) return
        val expNeedForNextLevel =
            properties.baseMaxExpAmount * (properties.maxExpAmountModifier * (data.currentLevel)).coerceIn(
                1.0,
                Double.MAX_VALUE
            )
        if (data.currentExpAmount >= expNeedForNextLevel) {
            val delta = abs(data.currentExpAmount - expNeedForNextLevel)
            data.currentExpAmount = delta
            data.currentLevel++
            Events.SkillLevelUpEvent.EVENT.invoker().callback(player,this, data)
        }
        setData(player, data)
    }

    fun incrementExp(player: ServerPlayerEntity) {
        val data = getData(player)
        if (data.currentLevel >= properties.maxLevel) return
        data.currentExpAmount += properties.baseExpGrowth * (properties.expGrowthModifier * data.currentLevel).coerceIn(
            1.0,
            Double.MAX_VALUE
        )
        calculateAndWrite(player, data)

        player.getData(NBT_KEY_SKILLS_INFO, Codec.unboundedMap(PlayerSkill.CODEC, PlayerSkill.Data.CODEC))


    }
}