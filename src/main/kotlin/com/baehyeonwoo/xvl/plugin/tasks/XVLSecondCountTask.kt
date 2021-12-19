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
import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.ending
import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.isNetherBiome
import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.isWarmBiome
import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.stopGame
import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.thirstValue
import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.thirsty
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title.Times.of
import net.kyori.adventure.title.Title.title
import org.bukkit.Sound
import org.bukkit.plugin.Plugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scoreboard.DisplaySlot
import java.time.Duration.ofSeconds

/***
 * @author BaeHyeonWoo
 */

class XVLSecondCountTask: Runnable {
    private fun getInstance(): Plugin {
        return XVLPluginMain.instance
    }

    private fun titleFunction(title: Component? = null, subtitle: Component? = null) {
        server.onlinePlayers.forEach {
            if (title != null && subtitle != null) {
                it.showTitle(title(title, subtitle, of(ofSeconds(0.5.toLong()), ofSeconds(8), ofSeconds(0.5.toLong()))))
            }
            else if (title != null && subtitle == null) {
                it.showTitle(title(title, text(" "), of(ofSeconds(0.5.toLong()), ofSeconds(8), ofSeconds(0.5.toLong()))))
            }
            else if (title == null && subtitle != null) {
                it.showTitle(title(text(" "), subtitle, of(ofSeconds(0.5.toLong()), ofSeconds(8), ofSeconds(0.5.toLong()))))
            }
            else if (title == null && subtitle == null) return
        }
    }

    private val server = getInstance().server

    private var statCount = 0

    private var endingCount = 0

    override fun run() {
        for (onlinePlayers in server.onlinePlayers) {

            val sm = server.scoreboardManager
            val sc = sm.mainScoreboard

            val death = sc.getObjective("Death")
            val health = sc.getObjective("Health")
            val foodLevel = sc.getObjective("FoodLevel")
            val freeze = sc.getObjective("Freeze")
            val thirst = sc.getObjective("Thirst")

            val death1 = sc.getObjective("Death1")
            val health1 = sc.getObjective("Health1")
            val foodLevel1 = sc.getObjective("FoodLevel1")
            val freeze1 = sc.getObjective("Freeze1")
            val thirst1 = sc.getObjective("Thirst1")

            /* ====================================================================================================================================================================================================================== */
            /* ====================================================================================================================================================================================================================== */
            /* ====================================================================================================================================================================================================================== */

            // Thirst

            if (thirsty[onlinePlayers.uniqueId] == true) {
                if (isWarmBiome[onlinePlayers.uniqueId] == true) {
                    ++onlinePlayers.thirstValue
                    ++onlinePlayers.thirstValue
                } else if (isNetherBiome[onlinePlayers.uniqueId] == true) {
                    ++onlinePlayers.thirstValue
                    ++onlinePlayers.thirstValue
                    ++onlinePlayers.thirstValue
                }
            } else {
                ++onlinePlayers.thirstValue
             }

            if (onlinePlayers.thirstValue >= 600) {
                onlinePlayers.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 1000000, 0, true, false))
            } else if (onlinePlayers.thirstValue >= 2400) {
                onlinePlayers.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 1000000, 1, true, false))
            } else if (onlinePlayers.thirstValue >= 3600) {
                onlinePlayers.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 1000000, 2, true, false))
            } else if (onlinePlayers.thirstValue >= 7200) {
                onlinePlayers.addPotionEffect(PotionEffect(PotionEffectType.CONFUSION, 1000000, 0, true, false))
                onlinePlayers.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 1000000, 4, true, false))
            }

            /* ====================================================================================================================================================================================================================== */
            /* ====================================================================================================================================================================================================================== */
            /* ====================================================================================================================================================================================================================== */

            // Stats

            onlinePlayers.scoreboard.getObjective("Freeze")?.getScore(onlinePlayers.name)?.score = onlinePlayers.freezeTicks
            onlinePlayers.scoreboard.getObjective("Thirst")?.getScore(onlinePlayers.name)?.score = onlinePlayers.thirstValue
            onlinePlayers.scoreboard.getObjective("Freeze1")?.getScore(onlinePlayers.name)?.score = onlinePlayers.freezeTicks
            onlinePlayers.scoreboard.getObjective("Thirst1")?.getScore(onlinePlayers.name)?.score = onlinePlayers.thirstValue
            onlinePlayers.scoreboard.getObjective("Death1")?.getScore(onlinePlayers.name)?.score = 1
            onlinePlayers.scoreboard.getObjective("Death1")?.getScore(onlinePlayers.name)?.score = 0

            when (statCount++) {
                0 -> {
                    death?.displaySlot = DisplaySlot.BELOW_NAME
                    death1?.displaySlot = DisplaySlot.SIDEBAR
                }
                5 -> {
                    health?.displaySlot = DisplaySlot.BELOW_NAME
                    health1?.displaySlot = DisplaySlot.SIDEBAR
                }
                10 -> {
                    foodLevel?.displaySlot = DisplaySlot.BELOW_NAME
                    foodLevel1?.displaySlot = DisplaySlot.SIDEBAR
                }
                20 -> {
                    freeze?.displaySlot = DisplaySlot.BELOW_NAME
                    freeze1?.displaySlot = DisplaySlot.SIDEBAR
                }
                25 -> {
                    thirst?.displaySlot = DisplaySlot.BELOW_NAME
                    thirst1?.displaySlot = DisplaySlot.SIDEBAR
                }
                30 -> {
                    statCount = 0
                }
            }

            /* ====================================================================================================================================================================================================================== */
            /* ====================================================================================================================================================================================================================== */
            /* ====================================================================================================================================================================================================================== */

            // Ending

            if (ending) {
                when (endingCount++) {
                    0 -> {
                        if (getInstance().config.getBoolean("ending-message")) {
                            server.onlinePlayers.forEach {
                                it.playSound(it.location, Sound.ENTITY_WITHER_DEATH, 0.25F, 1.5F)
                                it.addPotionEffect(PotionEffect(PotionEffectType.LEVITATION, 1000000, 0, true, false))
                                it.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 1000000, 255, true, false))
                                it.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1000000, 255, true, false))
                                it.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 1000000, 255, true, false))
                                titleFunction(text("XXXXXXXVVIVVIVVIVVILLLLL."), text(" "))
                            }
                        }
                        else {
                            server.onlinePlayers.forEach {
                                it.playSound(it.location, Sound.ENTITY_WITHER_DEATH, 0.25F, 1.5F)
                                titleFunction(text("게임을 종료합니다!", NamedTextColor.GREEN), text(" "))

                                stopGame()
                            }
                        }
                    }
                    5 -> {
                        if (getInstance().config.getBoolean("ending-message")) {
                            titleFunction(text("XXXXXXXVVIVVIVVIVVILLLLL."), text("A Rechallenge for komq.", NamedTextColor.GRAY))
                        }
                    }
                    10 -> {
                        if (getInstance().config.getBoolean("ending-message")) {
                            server.onlinePlayers.forEach {
                                it.playSound(it.location, Sound.ENTITY_PLAYER_LEVELUP, 1000F, 1F)
                                titleFunction(text("클리어를 축하드립니다!", NamedTextColor.GREEN), text(" "))
                            }
                        }
                    }
                    15 -> {
                        if (getInstance().config.getBoolean("ending-message")) {
                            titleFunction(text("클리어를 축하드립니다!", NamedTextColor.GREEN), text("THE END"))
                        }
                    }
                    20 -> {
                        if (getInstance().config.getBoolean("ending-message")) {
                            server.onlinePlayers.forEach {
                                it.removePotionEffect(PotionEffectType.LEVITATION)
                                it.removePotionEffect(PotionEffectType.SLOW)
                                it.removePotionEffect(PotionEffectType.BLINDNESS)

                                it.sendMessage(text("XXXXXXXVVIVVIVVIVVILLLLL.\n\n"))
                                it.sendMessage(text("제작: BaeHyeonWoo\n\nSpecial Thanks: PyBsh\n"))
                                it.sendMessage(text("코마님의 구독자 15만명과 21년 12월 15일 생일을 축하드립니다. :D\n"))
                                it.sendMessage(text("aHR0cHM6Ly9iYWVoeWVvbndvby5jb20vbWVzc2FnZQ==\n"))
                                titleFunction(text(" "), text("다른 여정의 끝, 이젠 좀 쉬어야겠어.", NamedTextColor.GRAY))
                            }
                        }

                        getInstance().config.set("${onlinePlayers.name}.death", onlinePlayers.scoreboard.getObjective("Death")?.getScore(onlinePlayers.name)?.score)
                        getInstance().saveConfig()

                        onlinePlayers.sendMessage(text("총 죽은 횟수: ${getInstance().config.getInt("${onlinePlayers.name}.death")}"))

                        stopGame()
                    }
                }
            }
            /* ====================================================================================================================================================================================================================== */
            /* ====================================================================================================================================================================================================================== */
            /* ====================================================================================================================================================================================================================== */
        }
    }
}