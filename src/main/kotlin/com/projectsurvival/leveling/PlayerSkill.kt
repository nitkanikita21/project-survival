package com.projectsurvival.leveling

import com.mojang.serialization.Codec
import com.mojang.serialization.Lifecycle
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.projectsurvival.Projectsurvival
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.SimpleRegistry
import net.minecraft.util.Identifier
import kotlin.reflect.full.primaryConstructor

class PlayerSkill(
    val properties: Properties
) {
    companion object {
        val REGISTRY: Registry<PlayerSkill> = SimpleRegistry(
            RegistryKey.ofRegistry(Identifier(Projectsurvival.ID, "skills")),
            Lifecycle.stable()
        )
        val REGISTRY_CODEC: Codec<PlayerSkill> = REGISTRY.codec.stable()
        val CODEC: Codec<PlayerSkill> = RecordCodecBuilder.create { instance ->
            return@create instance.group(
                Properties.CODEC.fieldOf("properties").forGetter(PlayerSkill::properties)
            ).apply(instance, PlayerSkill::class.primaryConstructor!!::call)
        }

        val SPRINTING_SKILL = Registry.register(
            REGISTRY,
            Identifier(Projectsurvival.ID, "sprinting"),
            PlayerSkill(
                Properties(
                    maxLevel = 12,
                    baseExpGrowth = 120.0,
                    expGrowthModifier = 1.2,
                    baseMaxExpAmount = 1000.0,
                    maxExpCountModifier = 1.6
                )
            )
        )
    }

    data class Properties(
        val maxLevel: Int,
        val baseExpGrowth: Double,
        val expGrowthModifier: Double,
        val baseMaxExpAmount: Double,
        val maxExpCountModifier: Double,
    ) {
        companion object {
            val CODEC: Codec<PlayerSkill.Properties> = RecordCodecBuilder.create { instance ->
                return@create instance.group(
                    Codec.INT.fieldOf("maxLevel").forGetter(Properties::maxLevel),
                    Codec.DOUBLE.fieldOf("baseExpGrowth").forGetter(Properties::baseExpGrowth),
                    Codec.DOUBLE.fieldOf("expGrowthModifier").forGetter(Properties::expGrowthModifier),
                    Codec.DOUBLE.fieldOf("baseMaxExpAmount").forGetter(Properties::baseMaxExpAmount),
                    Codec.DOUBLE.fieldOf("maxExpCountModifier").forGetter(Properties::maxExpCountModifier)
                ).apply(instance, Properties::class.primaryConstructor!!::call)
            }
        }
    }

    val id get() = REGISTRY.getId(this)
}