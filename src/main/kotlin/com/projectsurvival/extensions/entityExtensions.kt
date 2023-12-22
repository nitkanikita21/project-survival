package com.projectsurvival.extensions

import com.mojang.serialization.Codec
import com.projectsurvival.IEntityDataSaver
import com.projectsurvival.Projectsurvival
import net.minecraft.entity.Entity
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtOps
import net.minecraft.registry.RegistryOps
import net.minecraft.util.Identifier

fun Entity.getNbtData(key: String): NbtElement? {
    if(!(this as IEntityDataSaver).`project_survival$hasData`(key))return null
    return (this as IEntityDataSaver).`project_survival$getData`(key)
}
fun Entity.getNbtCompoundData(key: String): NbtCompound? {
    if(!(this as IEntityDataSaver).`project_survival$hasData`(key))return null
    return (this as IEntityDataSaver).`project_survival$getData`(key) as NbtCompound
}

fun Entity.setNbtData(key: String, data: NbtElement) = (this as IEntityDataSaver).`project_survival$setData`(key, data)

fun Entity.getNbtData(id: Identifier): NbtElement? = getNbtData(id.toString())
fun Entity.getNbtCompoundData(id: Identifier): NbtCompound? = getNbtCompoundData(id.toString())

fun Entity.setNbtData(id: Identifier, data: NbtElement) = setNbtData(id.toString(), data)

fun <T> Entity.getData(key: String, codec: Codec<T>): T? {
    return (getNbtData(key) as NbtCompound?)?.convertTo(codec)
}

fun <T> Entity.getData(id: Identifier, codec: Codec<T>): T? = getData(id.toString(), codec)
fun <T> Entity.setData(key: String, codec: Codec<T>, data: T) {
    val ops = if (Projectsurvival.registryAccess != null) {
        RegistryOps.of(NbtOps.INSTANCE, Projectsurvival.registryAccess)
    } else {
        NbtOps.INSTANCE
    }

    setNbtData(key, codec.encode(data, ops, ops.empty()).result().get())
}

fun <T> Entity.setData(id: Identifier, codec: Codec<T>, data: T) {
    setData(id.toString(), codec, data)
}