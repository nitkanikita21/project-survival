package com.projectsurvival

import com.projectsurvival.configs.TestConfig
import com.projectsurvival.leveling.PlayerSkill
import com.projectsurvival.serializing.ConfigLoader
import com.projectsurvival.serializing.createConfigIO
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.registry.DynamicRegistryManager
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import org.kodein.di.DI
import org.slf4j.LoggerFactory
import xyz.nucleoid.stimuli.Stimuli
import xyz.nucleoid.stimuli.event.player.PlayerChatEvent
import java.io.File
import java.util.Optional
import kotlin.io.path.absolutePathString
import kotlin.jvm.optionals.getOrNull


object Projectsurvival : ModInitializer {
    const val ID: String = "project_survival"
    private val logger = LoggerFactory.getLogger("project-survival")

    lateinit var di: DI
    var registryAccess: DynamicRegistryManager.Immutable? = null

    override fun onInitialize() {
        logger.info("Initializing mod")

        BlocksRepository.register()
        ItemsRepository.register()


        ServerLifecycleEvents.SERVER_STARTING.register(::onServerStarting)
        ServerLifecycleEvents.SERVER_STARTED.register(::onServerStarted)

        Stimuli.global().listen(PlayerChatEvent.EVENT, PlayerChatEvent l@{ player, message, _ ->
            player.server.executeSync {
                PlayerSkill.TEST_SKILL.incrementExp(player)
            }
            player.sendMessage(Text.literal("lvl: ${PlayerSkill.TEST_SKILL.getData(player).currentLevel}"))
            player.sendMessage(Text.literal("exp: ${PlayerSkill.TEST_SKILL.getData(player).currentExpAmount}"))
            return@l ActionResult.SUCCESS
        })

        PlayerSkill.Events.SkillLevelUpEvent.EVENT.register { player, skill, data ->
            player.playSound(
                SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
                1f, 1f
            )
            player.sendMessage(Text.literal("Level up!"))
        }
    }

    private fun onServerStarting(server: MinecraftServer) {
        logger.info("Starting server")
        registryAccess = server.registryManager

        val configLoader = ConfigLoader(
            FabricLoader.getInstance().configDir.toFile(),
            server.registryManager
        )

    }

    private fun onServerStarted(server: MinecraftServer) {
        logger.info("Server started successfully")
    }
}
