package com.projectsurvival

import eu.pb4.polymer.core.api.item.PolymerBlockItem
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

object ItemsRepository {
    lateinit var TEST_BREAKING_ITEM: Item

    fun register() {
        TEST_BREAKING_ITEM = Registry.register(
            Registries.ITEM, Identifier(Projectsurvival.ID, "test"),
            PolymerBlockItem(BlocksRepository.TEST_BRICK, FabricItemSettings(), Items.REDSTONE_BLOCK)
        )
    }
}