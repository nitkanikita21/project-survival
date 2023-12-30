package com.projectsurvival.serializing

import com.mojang.serialization.Codec
import com.mojang.serialization.DynamicOps
import net.minecraft.registry.DynamicRegistryManager
import net.minecraft.registry.RegistryOps
import kotlin.jvm.optionals.getOrNull
import kotlin.reflect.full.companionObjectInstance

interface CodecSerializable<C : CodecSerializable<C>> {
    interface CodecProvider<C : CodecSerializable<C>> {
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

            return CODEC.decode(ops2, source).result().get().first
        }
    }

    val codec: Codec<C> get() = CodecProvider<C>::CODEC.get(this::class.companionObjectInstance as CodecProvider<C>)

    fun <E> encode(
        ops: DynamicOps<E>,
        registryAccess: DynamicRegistryManager.Immutable? = null
    ): E {
        val ops2 = if (registryAccess != null) {
            RegistryOps.of(ops, registryAccess)
        } else {
            ops
        }

        return codec.encode(this as C, ops2, ops2.empty()).getOrThrow(true) {
            println(it)
        }
    }
}