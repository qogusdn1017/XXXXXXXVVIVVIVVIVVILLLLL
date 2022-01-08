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

import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.getInstance
import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.injured
import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.respawnDelay
import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.startGame
import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.stopGame
import io.github.monun.kommand.kommand
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.potion.PotionEffectType

/***
 * @author BaeHyeonWoo
 */

object XVLKommand {
    fun xvlKommand() {
        getInstance().kommand { 
            register("xvl") {
                executes { 
                    sender.sendMessage(text("XVL 0.0.2\nA Rechallenge for broken world."))
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
                then("debug") {
                    requires { isOp }
                    then("noinjured") {
                        executes {
                            if (getInstance().config.getBoolean("debug")) {
                                injured[player.uniqueId] = false
                                player.removePotionEffect(PotionEffectType.CONFUSION)
                            }
                        }
                    }
                    then("norespawndelay") {
                        executes {
                            if (getInstance().config.getBoolean("debug")) {
                                respawnDelay[player.uniqueId] = false
                            }
                        }
                    }
                }
            }
        }
    }
}