package com.projectsurvival.extensions

import com.mojang.serialization.Codec
import com.projectsurvival.Projectsurvival
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtOps
import net.minecraft.registry.RegistryOps
import net.minecraft.util.Identifier

fun <T> NbtElement.convertTo(codec: Codec<T>): T {
    val ops = if (Projectsurvival.registryAccess != null) {
        RegistryOps.of(NbtOps.INSTANCE, Projectsurvival.registryAccess)
    } else {
        NbtOps.INSTANCE
    }

    return codec.decode(ops, this).result().get().first
}

fun <T> NbtCompound.getData(key: String, codec: Codec<T>): T {
    return this.get(key)!!.convertTo(codec)
}

fun <T> NbtCompound.setData(key: String, codec: Codec<T>, data: T) {
    val ops = if (Projectsurvival.registryAccess != null) {
        RegistryOps.of(NbtOps.INSTANCE, Projectsurvival.registryAccess)
    } else {
        NbtOps.INSTANCE
    }

    this.put(key, codec.encode(data, ops, ops.empty()).result().get())
}

fun <T> NbtCompound.setData(id: Identifier, codec: Codec<T>, data: T) {
    setData(id.toString(), codec, data)
}

fun <T> NbtCompound.getData(id: Identifier, codec: Codec<T>): T = getData(id.toString(), codec)