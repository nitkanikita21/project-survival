package com.projectsurvival.serializing

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.JsonOps
import com.projectsurvival.configs.TestConfig
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.nbt.NbtIo
import net.minecraft.nbt.NbtOps
import net.minecraft.registry.DynamicRegistryManager
import net.minecraft.registry.RegistryOps
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import java.io.DataOutputStream
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.*
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.primaryConstructor

class ConfigLoader(
    val configDirectory: Path,
    val registryAccess: DynamicRegistryManager.Immutable
) {
    val gson = GsonBuilder().setPrettyPrinting().create()
    fun getDI(): DI.Module {
        return DI.Module(name = "Configs") {
            readToDI(this, "test.json", createConfigIO<TestConfig>())
        }
    }

    inline fun <reified C : CodecSerializable<C>> readConfig(path: String, rwIO: ConfigRWComparator<C>): C {
        val path = Paths.get(FabricLoader.getInstance().configDir.absolutePathString(), path)

        println(path.absolutePathString())

        if (!path.exists()) {
            val defaultConfig = C::class.primaryConstructor!!.callBy(emptyMap())
            path.createFile()
            when (path.extension) {
                "json" -> {
                    path.writeText(gson.toJson(rwIO.write(JsonOps.INSTANCE, defaultConfig, registryAccess)))

                }

                "nbt" -> {
                    NbtIo.write(
                        rwIO.write(NbtOps.INSTANCE, defaultConfig, registryAccess),
                        DataOutputStream(path.outputStream())
                    )
                }

                else -> {
                    throw RuntimeException("Unsuported file type for config ${path.absolutePathString()}")
                }
            }

            return defaultConfig
        }


        val loaded: C? = when (path.extension) {
            "json" -> {
                rwIO.read(JsonOps.INSTANCE, gson.fromJson(path.reader(), JsonElement::class.java), registryAccess)
            }

            "nbt" -> {
                rwIO.read(NbtOps.INSTANCE, NbtIo.read(path), registryAccess)
            }

            else -> {
                throw RuntimeException("Unsuported file type for config ${path.absolutePathString()}")
            }
        }

        if (loaded == null) throw RuntimeException("Failed to load config ${path.absolutePathString()}")
        return loaded
    }

    inline fun <reified C : CodecSerializable<C>> readToDI(diBuilder: DI.Builder, path: String, rwIO: ConfigRWComparator<C>) {
        diBuilder.apply {
            val config = readConfig(path, rwIO)
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
            return (C::class.companionObjectInstance as CodecSerializable.CodecProvider<C>).decode(ops2, source, registryAccess)
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