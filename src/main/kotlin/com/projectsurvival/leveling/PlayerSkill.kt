package com.projectsurvival.leveling

import com.mojang.serialization.Codec
import com.mojang.serialization.Lifecycle
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.projectsurvival.Projectsurvival
import com.projectsurvival.extensions.getData
import com.projectsurvival.extensions.getNbtCompoundData
import com.projectsurvival.extensions.setData
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
                    baseExpGrowth = 120.0,
                    expGrowthModifier = 1.2,
                    baseMaxExpAmount = 1000.0,
                    maxExpAmountModifier = 1.6
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
        var currentLevel: Int,
        var currentExpAmount: Double
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

    private fun getData(player: ServerPlayerEntity): Data {
        val nbtCompoundData = player.getNbtCompoundData(NBT_KEY_SKILLS_INFO)
        return nbtCompoundData.getData(id, Data.CODEC)
    }

    private fun setData(player: ServerPlayerEntity, data: Data) {
        return player.setData(id, Data.CODEC, data)
    }

    private fun calculateAndWrite(player: ServerPlayerEntity) {
        val data = getData(player)
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
            setData(player, data)
        }
    }

    fun incrementExp(player: ServerPlayerEntity) {
        val data = getData(player)
        if (data.currentLevel >= properties.maxLevel) return
        data.currentExpAmount += properties.baseExpGrowth * (properties.expGrowthModifier * data.currentLevel).coerceIn(
            1.0,
            Double.MAX_VALUE
        )
        setData(player, data)
        calculateAndWrite(player)
    }
}