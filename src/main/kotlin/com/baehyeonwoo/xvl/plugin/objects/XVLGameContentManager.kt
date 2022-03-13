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
import com.baehyeonwoo.xvl.plugin.tasks.XVLConfigReloadTask
import com.baehyeonwoo.xvl.plugin.tasks.XVLGameTask
import com.baehyeonwoo.xvl.plugin.tasks.XVLOpeningTask
import com.baehyeonwoo.xvl.plugin.tasks.XVLSecondCountTask
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title.Times.times
import net.kyori.adventure.title.Title.title
import org.bukkit.ChatColor.getByChar
import org.bukkit.Difficulty
import org.bukkit.GameRule
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.potion.PotionEffectType
import java.time.Duration.ofSeconds
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.random.Random.Default.nextInt

/***
 * @author BaeHyeonWoo
 */

object XVLGameContentManager {
    val plugin = XVLPluginMain.instance

    val server = plugin.server

    lateinit var gameEvent: Listener
    lateinit var motdEvent: Listener

    // TaskID
    var gameTaskId = 0
    var respawnTaskId = 0
    private var openingTaskId = 0

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
    val blockArray = HashMap<UUID, ArrayList<Material>>()
    val injured = HashMap<UUID, Boolean>()
    val respawnDelay = HashMap<UUID, Boolean>()

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

    fun currentDate(): Int {
        val localDateTime = LocalDateTime.now()
        val dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd")

        return localDateTime.format(dateFormat).toInt()
    }

    fun motd(): Component {
        // The most fucking motd ever seen in your life lmfao

        val motdString = "X X X X X X X V V I V V I V V I V V I L L L L L ."

        val mainMotdWords = motdString.split(" ").toMutableList()

        mainMotdWords.indices.forEach {
            mainMotdWords[it] = "${getByChar(nextInt(0xFF0000).toString())}${mainMotdWords[it]}"
        }

        val mainMotdString = StringJoiner("")

        mainMotdWords.forEach {
            mainMotdString.add(it)
        }

        return text("$mainMotdString").append(text("\n${getByChar(nextInt(0xFFFF00).toString())}by Depressed BaeHyeonWoo."))
    }

    fun titleFunction(title: Component? = null, subtitle: Component? = null) {
        server.onlinePlayers.forEach {
            if (title != null) {
                if (subtitle != null) {
                    it.showTitle(title(title, subtitle, times(ofSeconds(0.5.toLong()), ofSeconds(8), ofSeconds(0.5.toLong()))))
                }
                else {
                    it.showTitle(title(title, text(" "), times(ofSeconds(0.5.toLong()), ofSeconds(8), ofSeconds(0.5.toLong()))))
                }
            }
            else {
                if (subtitle != null) {
                    it.showTitle(title(text(" "), subtitle, times(ofSeconds(0.5.toLong()), ofSeconds(8), ofSeconds(0.5.toLong()))))
                }
                else return
            }
        }
    }

    fun manageFlags(FreezingFlag: Boolean, ThirstyFlag: Boolean, WarmBiomeFlag: Boolean, NetherBiomeFlag: Boolean) {
        server.onlinePlayers.forEach {
            freezing[it.uniqueId] = FreezingFlag
            thirsty[it.uniqueId] = ThirstyFlag
            isWarmBiome[it.uniqueId] = WarmBiomeFlag
            isNetherBiome[it.uniqueId] = NetherBiomeFlag
        }
    }

    private fun setupGameRules() {
        server.worlds.forEach {
            it.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, true)
            it.setGameRule(GameRule.SHOW_DEATH_MESSAGES, true)
            it.setGameRule(GameRule.SEND_COMMAND_FEEDBACK, false)
            it.setGameRule(GameRule.LOG_ADMIN_COMMANDS, false)

            if (!plugin.config.getBoolean("debug")) {
                it.setGameRule(GameRule.REDUCED_DEBUG_INFO, true)
            }
            else {
                it.setGameRule(GameRule.REDUCED_DEBUG_INFO, false)
            }
            
            it.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true)
            it.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false)
            it.setGameRule(GameRule.SPAWN_RADIUS, 0)
            it.difficulty = Difficulty.NORMAL
            it.difficulty = Difficulty.HARD
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
        if (foodLevel == null) sc.registerNewObjective("FoodLevel", "food", text("허기", NamedTextColor.GOLD))

        val freeze = sc.getObjective("Freeze")
        if (freeze == null) sc.registerNewObjective("Freeze", "dummy", text("추위", NamedTextColor.DARK_BLUE))

        val thirst = sc.getObjective("Thirst")
        if (thirst == null) sc.registerNewObjective("Thirst", "dummy", text("갈증", NamedTextColor.AQUA))
    }

    private fun restoreGameRules() {
        server.worlds.forEach {
            it.setGameRule(GameRule.SEND_COMMAND_FEEDBACK, true)
            it.setGameRule(GameRule.LOG_ADMIN_COMMANDS, true)
            it.setGameRule(GameRule.REDUCED_DEBUG_INFO, false)
            it.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, true)
            it.setGameRule(GameRule.SPAWN_RADIUS, 10)
            it.difficulty = Difficulty.NORMAL
        }
    }

    fun startGame() {
        HandlerList.unregisterAll(gameEvent)

        val gameTask = server.scheduler.runTaskTimer(plugin, XVLGameTask(), 0L, 0L)
        if (!plugin.config.getBoolean("game-running")) {
            if (plugin.config.getBoolean("system-message")) {
                val openingTask = server.scheduler.runTaskTimer(plugin, XVLOpeningTask(), 0L, 0L)
                openingTaskId = openingTask.taskId
            }
            plugin.config.set("game-running", true)
            plugin.saveConfig()
        }

        setupGameRules()
        setupScoreboards()

        server.scheduler.runTaskTimer(plugin, XVLSecondCountTask(), 20L, 20L)
        gameTaskId = gameTask.taskId

        server.pluginManager.registerEvents(gameEvent, plugin)

        server.onlinePlayers.forEach {
            it.health = 20.0
            it.foodLevel = 20

            it.damage(1.0)

            it.foodLevel = 19
            it.foodLevel = 20
            warmflag[it.uniqueId] = false
        }

        server.scheduler.scheduleSyncDelayedTask(plugin, {
            server.scheduler.cancelTask(openingTaskId)
        }, 300L)
    }

    fun stopGame() {
        ending = false
        restoreGameRules()
        
        HandlerList.unregisterAll(gameEvent)
        server.scheduler.cancelTasks(plugin)
        server.scheduler.runTaskTimer(plugin, XVLConfigReloadTask(), 0L, 0L)

        manageFlags(FreezingFlag = false, ThirstyFlag = false, WarmBiomeFlag = false, NetherBiomeFlag = false)

        server.onlinePlayers.forEach {
            if (it.hasPotionEffect(PotionEffectType.SLOW)) {
                it.removePotionEffect(PotionEffectType.SLOW)
                injured[it.uniqueId] = false
                respawnDelay[it.uniqueId] = false
            }

            it.thirstValue = 0

            plugin.config.set("game-running", false)
            plugin.config.set("kill-dragon", false)
            plugin.config.set("${it.name}.death", null)
            plugin.config.set("${it.name}.freezeticks", null)
            plugin.config.set("${it.name}.thirstvalue", null)
            plugin.saveConfig()
        }

        sc.objectives.forEach { it.unregister() }
    }
}