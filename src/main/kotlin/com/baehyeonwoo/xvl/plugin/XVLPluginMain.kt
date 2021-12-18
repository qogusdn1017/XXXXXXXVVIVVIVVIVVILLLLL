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

import com.baehyeonwoo.xvl.plugin.commands.XVLKommand
import com.baehyeonwoo.xvl.plugin.config.XVLConfig
import com.baehyeonwoo.xvl.plugin.events.XVLEvent
import com.baehyeonwoo.xvl.plugin.objects.XVLGameStatus.setupScoreboards
import com.baehyeonwoo.xvl.plugin.objects.XVLGameStatus.setupWorlds
import com.baehyeonwoo.xvl.plugin.objects.XVLGameStatus.thirstValue
import com.baehyeonwoo.xvl.plugin.objects.XVLGameStatus.warmflag
import com.baehyeonwoo.xvl.plugin.tasks.*
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

        XVLConfig.load(configFile)
        XVLKommand.xvlKommand()

        if (this.config.getBoolean("game-running")) {
            setupWorlds()
            setupScoreboards()

            server.pluginManager.registerEvents(XVLEvent(), this)
            server.scheduler.runTaskTimer(this, XVLGameTask(), 0L, 0L)
            server.scheduler.runTaskTimer(this, XVLClimateTask(), 0L, 0L)
            server.scheduler.runTaskTimer(this, XVLConfigReloadTask(), 0L, 0L)
            server.scheduler.runTaskTimer(this, XVLThirstTask(), 20L, 20L)
            server.scheduler.runTaskTimer(this, XVLScoreboardTask(), 20L, 20L)

            for (onlinePlayers in server.onlinePlayers) {
                warmflag[onlinePlayers.uniqueId] = false
                onlinePlayers.freezeTicks = config.getInt("${onlinePlayers.name}.freezeticks", onlinePlayers.freezeTicks)
                onlinePlayers.thirstValue = config.getInt("${onlinePlayers.name}.thirstvalue", onlinePlayers.thirstValue)
            }
        }
    }

    override fun onDisable() {
        for (onlinePlayers in server.onlinePlayers) {
            config.set("${onlinePlayers.name}.freezeticks", onlinePlayers.freezeTicks)
            config.set("${onlinePlayers.name}.thirstvalue", onlinePlayers.thirstValue)
            saveConfig()
        }
    }
}