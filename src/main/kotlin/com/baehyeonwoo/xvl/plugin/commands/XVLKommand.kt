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

package com.baehyeonwoo.xvl.plugin.commands

import com.baehyeonwoo.xvl.plugin.XVLPluginMain
import com.baehyeonwoo.xvl.plugin.events.XVLEvent
import com.baehyeonwoo.xvl.plugin.objects.XVLGameStatus.setupScoreboards
import com.baehyeonwoo.xvl.plugin.objects.XVLGameStatus.setupWorlds
import com.baehyeonwoo.xvl.plugin.objects.XVLGameStatus.warmflag
import com.baehyeonwoo.xvl.plugin.tasks.*
import io.github.monun.kommand.kommand
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.event.HandlerList
import org.bukkit.plugin.Plugin

/***
 * @author BaeHyeonWoo
 */

object XVLKommand {
    private fun getInstance(): Plugin {
        return XVLPluginMain.instance
    }
    
    private val server = getInstance().server
    
    fun xvlKommand() {
        getInstance().kommand { 
            register("xvl") {
                executes { 
                    sender.sendMessage(text("XVL 0.0.1\nA Rechallenge for komq."))
                }
                then("start") {
                    requires { isOp }
                    executes {
                        if (!getInstance().config.getBoolean("game-running")) {
                            getInstance().config.set("game-running", true)
                            getInstance().saveConfig()

                            setupWorlds()
                            setupScoreboards()

                            server.pluginManager.registerEvents(XVLEvent(), getInstance())
                            server.scheduler.runTaskTimer(getInstance(), XVLGameTask(), 0L, 0L)
                            server.scheduler.runTaskTimer(getInstance(), XVLClimateTask(), 0L, 0L)
                            server.scheduler.runTaskTimer(getInstance(), XVLConfigReloadTask(), 0L, 0L)
                            server.scheduler.runTaskTimer(getInstance(), XVLThirstTask(), 20L, 20L)
                            server.scheduler.runTaskTimer(getInstance(), XVLScoreboardTask(), 20L, 20L)

                            for (onlinePlayers in server.onlinePlayers) {
                                warmflag[onlinePlayers.uniqueId] = false
                            }

                            sender.sendMessage(text("Game Started."))
                        }
                        else {
                            sender.sendMessage(text("The game has already started!", NamedTextColor.RED))
                        }
                    }
                }
                then("stop") {
                    requires { isOp }
                    executes {
                        if (getInstance().config.getBoolean("game-running")) {
                            getInstance().config.set("game-running", false)
                            getInstance().saveConfig()

                            HandlerList.unregisterAll(getInstance())
                            server.scheduler.cancelTasks(getInstance())
                            for (onlinePlayers in server.onlinePlayers) {
                                getInstance().config.set("${onlinePlayers.name}.freezeticks", null)
                                getInstance().config.set("${onlinePlayers.name}.thirstvalue", null)
                                getInstance().saveConfig()
                            }
                            sender.sendMessage(text("Game Stopped."))
                        }
                        else {
                            sender.sendMessage(text("The game has already stopped!", NamedTextColor.RED))
                        }
                    }
                }
            }
        }
    }
}