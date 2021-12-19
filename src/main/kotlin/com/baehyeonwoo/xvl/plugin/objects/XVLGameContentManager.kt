/*
 * Copyright (c) 2021 BaeHyeonWoo
 *
 *  Licensed under the General Public License, Version 3.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/gpl-3.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.baehyeonwoo.xvl.plugin.objects

import com.baehyeonwoo.xvl.plugin.XVLPluginMain
import com.baehyeonwoo.xvl.plugin.events.XVLEvent
import com.baehyeonwoo.xvl.plugin.tasks.*
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Difficulty
import org.bukkit.GameRule
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.plugin.Plugin
import java.util.*

/***
 * @author BaeHyeonWoo
 */

object XVLGameContentManager {
    private fun getInstance(): Plugin {
        return XVLPluginMain.instance
    }

    private val server = getInstance().server

    // Ending Flag
    var ending = false


    // Scoreboard Manager
    private val sm = server.scoreboardManager
    private val sc = sm.mainScoreboard


    // HashMaps
    private val thirst = HashMap<Player, Int>()

    val highestFreezingTicks = HashMap<UUID, Int>()
    val freezing = HashMap<UUID, Boolean>()
    val thirsty = HashMap<UUID, Boolean>()
    val isWarmBiome = HashMap<UUID, Boolean>()
    val isNetherBiome = HashMap<UUID, Boolean>()
    val warmflag = HashMap<UUID, Boolean>()

    var Player.thirstValue: Int
        get() {
            return thirst[this] ?: 0
        }
        set(value) {
            thirst[this] = value
        }

    var UUID.highestFreezingTickValue: Int
        get() {
            return highestFreezingTicks[this] ?: 0
        }
        set(value) {
            highestFreezingTicks[this] = value
        }

    // Game Managing Functions

    fun manageFlags(FreezingFlag: Boolean, ThirstyFlag: Boolean, WarmBiomeFlag: Boolean, NetherBiomeFlag: Boolean) {
        for (onlinePlayers in server.onlinePlayers) {
            freezing[onlinePlayers.uniqueId] = FreezingFlag
            thirsty[onlinePlayers.uniqueId] = ThirstyFlag
            isWarmBiome[onlinePlayers.uniqueId] = WarmBiomeFlag
            isNetherBiome[onlinePlayers.uniqueId] = NetherBiomeFlag
        }
    }

    private fun setupGameRules() {
        for (world in server.worlds) {
            world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, true)
            world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, true)
            world.setGameRule(GameRule.SEND_COMMAND_FEEDBACK, false)
            world.setGameRule(GameRule.LOG_ADMIN_COMMANDS, false)
            world.setGameRule(GameRule.REDUCED_DEBUG_INFO, true)
            world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, false)
            world.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false)
            world.setGameRule(GameRule.SPAWN_RADIUS, 0)
            world.difficulty = Difficulty.HARD
        }
    }

    private fun setupScoreboards() {
        val sm = server.scoreboardManager
        val sc = sm.mainScoreboard

        val death = sc.getObjective("Death")
        if (death == null) sc.registerNewObjective("Death", "deathCount", text("죽은 횟수", NamedTextColor.DARK_RED))

        val health = sc.getObjective("Health")
        if (health == null) sc.registerNewObjective("Health", "health", text("체력", NamedTextColor.RED))

        val foodLevel = sc.getObjective("FoodLevel")
        if (foodLevel == null) sc.registerNewObjective("FoodLevel", "food", text("허기", NamedTextColor.DARK_GRAY))

        val freeze = sc.getObjective("Freeze")
        if (freeze == null) sc.registerNewObjective("Freeze", "dummy", text("추위", NamedTextColor.DARK_BLUE))

        val thirst = sc.getObjective("Thirst")
        if (thirst == null) sc.registerNewObjective("Thirst", "dummy", text("갈증", NamedTextColor.AQUA))

        val death1 = sc.getObjective("Death1")
        if (death1 == null) sc.registerNewObjective("Death1", "deathCount", text("죽은 횟수", NamedTextColor.DARK_RED))

        val health1 = sc.getObjective("Health1")
        if (health1 == null) sc.registerNewObjective("Health1", "health", text("체력", NamedTextColor.RED))

        val foodLevel1 = sc.getObjective("FoodLevel1")
        if (foodLevel1 == null) sc.registerNewObjective("FoodLevel1", "food", text("허기", NamedTextColor.DARK_GRAY))

        val freeze1 = sc.getObjective("Freeze1")
        if (freeze1 == null) sc.registerNewObjective("Freeze1", "dummy", text("추위", NamedTextColor.DARK_BLUE))

        val thirst1 = sc.getObjective("Thirst1")
        if (thirst1 == null) sc.registerNewObjective("Thirst1", "dummy", text("갈증", NamedTextColor.AQUA))
    }

    private fun restoreGameRules() {
        for (world in server.worlds) {
            world.setGameRule(GameRule.SEND_COMMAND_FEEDBACK, true)
            world.setGameRule(GameRule.LOG_ADMIN_COMMANDS, true)
            world.setGameRule(GameRule.REDUCED_DEBUG_INFO, false)
            world.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, true)
            world.setGameRule(GameRule.SPAWN_RADIUS, 10)
            world.difficulty = Difficulty.NORMAL
        }
    }

    fun startGame() {
        if (!getInstance().config.getBoolean("game-running")) {
            getInstance().config.set("game-running", true)
            getInstance().saveConfig()
        }

        setupGameRules()
        setupScoreboards()

        server.pluginManager.registerEvents(XVLEvent(), getInstance())
        server.scheduler.runTaskTimer(getInstance(), XVLGameTask(), 0L, 0L)
        server.scheduler.runTaskTimer(getInstance(), XVLSecondCountTask(), 20L, 20L)

        for (onlinePlayers in server.onlinePlayers) {
            onlinePlayers.damage(0.0001)
            onlinePlayers.foodLevel = 19
            onlinePlayers.foodLevel = 20
            warmflag[onlinePlayers.uniqueId] = false
        }
    }

    fun stopGame() {
        restoreGameRules()

        getInstance().config.set("game-running", false)
        getInstance().saveConfig()
        
        HandlerList.unregisterAll(getInstance())
        server.scheduler.cancelTasks(getInstance())

        manageFlags(FreezingFlag = false, ThirstyFlag = false, WarmBiomeFlag = false, NetherBiomeFlag = false)

        for (onlinePlayers in server.onlinePlayers) {
            onlinePlayers.thirstValue = 0

            getInstance().config.set("${onlinePlayers.name}.death", null)
            getInstance().config.set("${onlinePlayers.name}.freezeticks", null)
            getInstance().config.set("${onlinePlayers.name}.thirstvalue", null)
            getInstance().saveConfig()

            sc.objectives.forEach { it.unregister() }
        }
    }
}