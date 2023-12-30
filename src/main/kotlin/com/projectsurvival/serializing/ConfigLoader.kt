package com.projectsurvival.serializing

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.JsonOps
import net.minecraft.nbt.NbtIo
import net.minecraft.nbt.NbtOps
import net.minecraft.registry.DynamicRegistryManager
import net.minecraft.registry.RegistryOps
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import java.io.DataOutputStream
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.nameWithoutExtension
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.primaryConstructor

class ConfigLoader(
    val configDirectory: File,
    val registryAccess: DynamicRegistryManager.Immutable
) {
    val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    inline fun <reified C : CodecSerializable<C>> loadConfig(path: String, rwIO: ConfigRWComparator<C>): C {
        val file = File(configDirectory, path)

        if (!file.exists()) {
            val defaultConfig = C::class.primaryConstructor!!.callBy(emptyMap())
            file.createNewFile()
            when (file.extension) {
                "json" -> {
                    file.writeText(gson.toJson(rwIO.write(JsonOps.INSTANCE, defaultConfig, registryAccess)))

                }

                "nbt" -> {
                    NbtIo.write(
                        rwIO.write(NbtOps.INSTANCE, defaultConfig, registryAccess),
                        DataOutputStream(file.outputStream())
                    )
                }

                else -> {
                    throw RuntimeException("Unsuported file type for config ${file.absolutePath}")
                }
            }

            return defaultConfig
        }


        val loaded: C? = when (file.extension) {
            "json" -> {
                rwIO.read(JsonOps.INSTANCE, gson.fromJson(file.reader(), JsonElement::class.java), registryAccess)
            }

            "nbt" -> {
                rwIO.read(NbtOps.INSTANCE, NbtIo.read(file.toPath()), registryAccess)
            }

            else -> {
                throw RuntimeException("Unsuported file type for config ${file.absolutePath}")
            }
        }

        if (loaded == null) throw RuntimeException("Failed to load config ${file.absolutePath}")
        return loaded
    }

    inline fun <reified C : CodecSerializable<C>> readToDI(
        diBuilder: DI.Builder,
        path: String,
        rwIO: ConfigRWComparator<C>
    ) {
        diBuilder.apply {
            val config = loadConfig(path, rwIO)
            bindSingleton(Path(path).nameWithoutExtension) {
                config
            }
        }
    }
}


interface ConfigRWComparator<C : CodecSerializable<C>> {
    fun <E> read(ops: DynamicOps<E>, source: E, registryAccess: DynamicRegistryManager.Immutable? = null): C?
    fun <E> write(ops: DynamicOps<E>, data: C, registryAccess: DynamicRegistryManager.Immutable? = null): E?
}

inline fun <reified C : CodecSerializable<C>> createConfigIO(): ConfigRWComparator<C> {
    return object : ConfigRWComparator<C> {
        override fun <E> read(ops: DynamicOps<E>, source: E, registryAccess: DynamicRegistryManager.Immutable?): C? {
            val ops2 = if (registryAccess != null) {
                RegistryOps.of(ops, registryAccess)
            } else {
                ops
            }
            return (C::class.companionObjectInstance as CodecSerializable.CodecProvider<C>).decode(
                ops2,
                source,
                registryAccess
            )
        }

        override fun <E> write(ops: DynamicOps<E>, data: C, registryAccess: DynamicRegistryManager.Immutable?): E? {
            val ops2 = if (registryAccess != null) {
                RegistryOps.of(ops, registryAccess)
            } else {
                ops
            }
            return data.encode(ops2, registryAccess)
        }
    }
}