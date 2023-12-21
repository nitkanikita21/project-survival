package com.projectsurvival

import com.projectsurvival.configs.TestConfig
import com.projectsurvival.serializing.ConfigLoader
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.server.MinecraftServer
import org.kodein.di.DI
import org.kodein.di.instance
import org.slf4j.LoggerFactory
import java.nio.file.Paths
import kotlin.io.path.absolutePathString


object Projectsurvival : ModInitializer {
    const val ID: String = "project-survival"
    private val logger = LoggerFactory.getLogger("project-survival")

    lateinit var di: DI

    override fun onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        logger.info("Initializing mod")

        ServerLifecycleEvents.SERVER_STARTING.register(::onServerStarting)
        ServerLifecycleEvents.SERVER_STARTED.register(::onServerStarted)
    }

    private fun onServerStarting(server: MinecraftServer) {
        logger.info("Starting server")

        val module = ConfigLoader(
            Paths.get(FabricLoader.getInstance().configDir.absolutePathString(), "projectsurvival"),

            server.registryManager
        ).getDI()

        val di = DI {
            import(module)
        }

        val config: TestConfig by di.instance("test")
        logger.info(config.uuid.toString())

    }

    private fun onServerStarted(server: MinecraftServer) {

        logger.info("Server started successfully")
    }

    private fun loadConfigs(): DI.Module {
        return DI.Module {

        }
    }
}