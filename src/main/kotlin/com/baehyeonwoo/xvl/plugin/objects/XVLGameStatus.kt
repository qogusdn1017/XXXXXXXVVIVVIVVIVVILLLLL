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
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Difficulty
import org.bukkit.GameRule
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.util.*

/***
 * @author BaeHyeonWoo
 */

object XVLGameStatus {
    private fun getInstance(): Plugin {
        return XVLPluginMain.instance
    }

    private val server = getInstance().server

    val highestFreezingTicks = HashMap<UUID, Int>()

    val freezing = HashMap<UUID, Boolean>()
    val warming = HashMap<UUID, Boolean>()
    val isWarmBiome = HashMap<UUID, Boolean>()
    val isNetherBiome = HashMap<UUID, Boolean>()
    val warmflag = HashMap<UUID, Boolean>()

    private val thirst = HashMap<Player, Int>()

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

    fun setupWorlds() {
        for (world in server.worlds) {
            world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false)
            world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, true)
            world.setGameRule(GameRule.SEND_COMMAND_FEEDBACK, false)
            world.setGameRule(GameRule.LOG_ADMIN_COMMANDS, false)
            world.setGameRule(GameRule.REDUCED_DEBUG_INFO, true)
            world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true)
            world.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false)
            world.setGameRule(GameRule.SPAWN_RADIUS, 0)
            world.difficulty = Difficulty.HARD
        }
    }

    fun setupScoreboards() {
        val sm = server.scoreboardManager
        val sc = sm.mainScoreboard

        val health = sc.getObjective("Health")
        if (health == null) sc.registerNewObjective("Health", "health", text("체력", NamedTextColor.RED))

        val foodLevel = sc.getObjective("FoodLevel")
        if (foodLevel == null) sc.registerNewObjective("FoodLevel", "food", text("허기", NamedTextColor.DARK_GRAY))

        val freeze = sc.getObjective("Freeze")
        if (freeze == null) sc.registerNewObjective("Freeze", "dummy", text("추위", NamedTextColor.DARK_BLUE))

        val thirst = sc.getObjective("Thirst")
        if (thirst == null) sc.registerNewObjective("Thirst", "dummy", text("갈증", NamedTextColor.AQUA))
    }
}