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

package com.baehyeonwoo.xvl.plugin.tasks

import com.baehyeonwoo.xvl.plugin.XVLPluginMain
import com.baehyeonwoo.xvl.plugin.objects.XVLGameStatus.thirstValue
import org.bukkit.plugin.Plugin
import org.bukkit.scoreboard.DisplaySlot

/***
 * @author BaeHyeonWoo
 */

class XVLScoreboardTask: Runnable {
    private fun getInstance(): Plugin {
        return XVLPluginMain.instance
    }

    private val server = getInstance().server

    private var count = 0

    override fun run() {
        val sm = server.scoreboardManager
        val sc = sm.mainScoreboard

        val health = sc.getObjective("Health")
        val foodLevel = sc.getObjective("FoodLevel")
        val freeze = sc.getObjective("Freeze")
        val thirst = sc.getObjective("Thirst")

        for (onlinePlayers in server.onlinePlayers) {
            onlinePlayers.scoreboard.getObjective("Freeze")?.getScore(onlinePlayers.name)?.score = onlinePlayers.freezeTicks
            onlinePlayers.scoreboard.getObjective("Thirst")?.getScore(onlinePlayers.name)?.score = onlinePlayers.thirstValue
        }

        when(count++) {
            0 -> {
                health?.displaySlot = DisplaySlot.BELOW_NAME
            }
            5 -> {
                foodLevel?.displaySlot = DisplaySlot.BELOW_NAME
            }
            10 -> {
                freeze?.displaySlot = DisplaySlot.BELOW_NAME
            }
            15 -> {
                thirst?.displaySlot = DisplaySlot.BELOW_NAME
            }
            20 -> {
                count = 0
            }
        }
    }
}
