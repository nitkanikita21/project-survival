package com.projectsurvival.leveling

import com.mojang.serialization.Codec
import com.mojang.serialization.Lifecycle
import com.projectsurvival.Projectsurvival
import eu.pb4.placeholders.api.TextParserUtils
import me.drex.message.api.LocalizedMessage
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.SimpleRegistry
import net.minecraft.text.Text
import net.minecraft.util.Identifier

class PlayerSkill(
    val properties: Properties,
    val name: Text,
    val description: Text
) {
    companion object {
        val REGISTRY: Registry<PlayerSkill> = SimpleRegistry(
            RegistryKey.ofRegistry(Identifier(Projectsurvival.ID, "skills")),
            Lifecycle.stable()
        )
        val CODEC: Codec<PlayerSkill> = REGISTRY.codec.stable()

        val SPRINTING_SKILL = Registry.register(
            REGISTRY,
            Identifier(Projectsurvival.ID, "sprinting"),
            PlayerSkill(
                Properties(
                    maxLevel = 12,
                    baseExpGrowth = 120.0,
                    expModifierByLevel = 1.2,
                    expForNextLevelModifier = 1.6
                ),
                name = TextParserUtils.formatText("<gold>Sprinting"),
                description = TextParserUtils.formatText("<yellow>Sprinting description"),
            )
        )
    }

    data class Properties(
        val maxLevel: Int,
        val baseExpGrowth: Double,
        val expModifierByLevel: Double,
        val expForNextLevelModifier: Double,
    )

    val id get() = REGISTRY.getId(this)
}