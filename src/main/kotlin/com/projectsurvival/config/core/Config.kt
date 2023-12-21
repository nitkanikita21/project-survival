package com.projectsurvival.config.core

import com.google.gson.JsonElement
import com.mojang.serialization.Codec
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.JsonOps
import net.minecraft.registry.DynamicRegistryManager
import net.minecraft.registry.RegistryOps
import kotlin.jvm.optionals.getOrNull
import kotlin.reflect.full.companionObjectInstance

interface Config<C : Config<C>> {
    interface CodecProvider<C : Config<C>> {
        val CODEC: Codec<C>

        fun <E> decode(
            ops: DynamicOps<E>,
            source: E,
            registryAccess: DynamicRegistryManager.Immutable? = null
        ): C? {
            val ops2 = if (registryAccess != null) {
                RegistryOps.of(ops, registryAccess)
            } else {
                ops
            }

            return CODEC.decode(ops2, source).result().get()?.first
        }
    }

    val codec: Codec<C> get() = CodecProvider<C>::CODEC.get(this::class.companionObjectInstance as CodecProvider<C>)

    fun <E> encode(
        ops: DynamicOps<E>,
        registryAccess: DynamicRegistryManager.Immutable? = null
    ): E? {
        val ops2 = if (registryAccess != null) {
            RegistryOps.of(ops, registryAccess)
        } else {
            ops
        }

        return codec.encode(this as C, ops2, ops2.empty()).result().getOrNull()
    }
}