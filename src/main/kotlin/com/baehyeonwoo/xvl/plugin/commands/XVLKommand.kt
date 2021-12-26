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
import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.startGame
import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.stopGame
import io.github.monun.kommand.kommand
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title
import org.bukkit.Material
import org.bukkit.plugin.Plugin
import java.time.Duration

/***
 * @author BaeHyeonWoo
 */

object XVLKommand {
    private fun getInstance(): Plugin {
        return XVLPluginMain.instance
    }

    private val warmBlocks = arrayListOf(
        Material.FIRE,
        Material.SOUL_FIRE,
        Material.CAMPFIRE,
        Material.SOUL_CAMPFIRE,
        Material.LAVA,
        Material.LAVA_CAULDRON,
        Material.MAGMA_BLOCK,
        Material.TORCH,
        Material.SOUL_TORCH,
        Material.WALL_TORCH,
        Material.SOUL_WALL_TORCH
    )
    
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
                            startGame()
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
                            stopGame()
                            sender.sendMessage(text("Game Stopped."))
                        }
                        else {
                            sender.sendMessage(text("The game has already stopped!", NamedTextColor.RED))
                        }
                    }
                }
                then("test") {
                    executes {
                        for (x in -3..3) {
                            for (y in -3..3) {
                                for (z in -3..3) {
                                    for (onlinePlayers in getInstance().server.onlinePlayers) {
                                        val location = onlinePlayers.location.add(x.toDouble(), y.toDouble(), z.toDouble())
                                        val boolArray = ArrayList<Boolean>()
                                        val blockType = location.block.type

                                        sender.sendMessage(text(blockType.toString()))
                                        sender.sendMessage(text("x: ${location.x}, y: ${location.y}, z: ${location.z}"))

                                        boolArray.addAll(listOf(blockType in warmBlocks))

                                        getInstance().server.showTitle(Title.title(text(" "), text("$boolArray"), Title.Times.of(Duration.ofSeconds(0L), Duration.ofSeconds(5L), Duration.ofSeconds(0L))                                         ))
                                        boolArray.clear()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}