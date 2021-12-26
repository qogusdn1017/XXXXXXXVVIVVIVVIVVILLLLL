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

package com.baehyeonwoo.xvl.plugin

import com.baehyeonwoo.xvl.plugin.commands.XVLKommand.xvlKommand
import com.baehyeonwoo.xvl.plugin.config.XVLConfig.load
import com.baehyeonwoo.xvl.plugin.events.XVLMotdEvent
import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.startGame
import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.thirstValue
import com.baehyeonwoo.xvl.plugin.tasks.XVLConfigReloadTask
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

/***
 * @author BaeHyeonWoo
 */

class XVLPluginMain : JavaPlugin() {

    companion object {
        lateinit var instance: XVLPluginMain
            private set
    }

    private val configFile = File(dataFolder, "config.yml")

    override fun onEnable() {
        instance = this
        server.pluginManager.registerEvents(XVLMotdEvent(), this)
        server.scheduler.runTaskTimer(this, XVLConfigReloadTask(), 0L, 0L)

        load(configFile)
        xvlKommand()

        if (this.config.getBoolean("game-running")) {
            startGame()
        }
    }

    override fun onDisable() {
        for (onlinePlayers in server.onlinePlayers) {
            config.set("${onlinePlayers.name}.death", onlinePlayers.scoreboard.getObjective("Death")?.getScore(onlinePlayers.name)?.score)
            config.set("${onlinePlayers.name}.thirstvalue", onlinePlayers.thirstValue)
            saveConfig()
        }
    }
}