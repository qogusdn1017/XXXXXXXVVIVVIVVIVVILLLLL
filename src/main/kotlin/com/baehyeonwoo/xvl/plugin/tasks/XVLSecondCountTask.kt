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

import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.ending
import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.isNetherBiome
import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.isWarmBiome
import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.plugin
import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.server
import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.stopGame
import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.thirstValue
import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.thirsty
import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.titleFunction
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.FireworkEffect
import org.bukkit.Sound
import org.bukkit.entity.Firework
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scoreboard.DisplaySlot

/***
 * @author BaeHyeonWoo
 */

class XVLSecondCountTask: Runnable {
    private val fwEffect = FireworkEffect.builder().with(FireworkEffect.Type.STAR).withColor(org.bukkit.Color.AQUA).build()

    private fun fireworks(it: Player) {
        it.world.spawn(it.location, Firework::class.java).apply {
            fireworkMeta = fireworkMeta.apply {
                addEffects(fwEffect)
                power = 30
            }
        }
    }

    private var statCount = 0

    private var endingCount = 0

    override fun run() {
        server.onlinePlayers.forEach {
            val sm = server.scoreboardManager
            val sc = sm.mainScoreboard

            val death = sc.getObjective("Death")
            val health = sc.getObjective("Health")
            val foodLevel = sc.getObjective("FoodLevel")
            val freeze = sc.getObjective("Freeze")
            val thirst = sc.getObjective("Thirst")

            /* ====================================================================================================================================================================================================================== */
            /* ====================================================================================================================================================================================================================== */
            /* ====================================================================================================================================================================================================================== */

            // Thirst

            if (thirsty[it.uniqueId] == true) {
                if (isWarmBiome[it.uniqueId] == true) {
                    ++it.thirstValue
                    ++it.thirstValue
                } else if (isNetherBiome[it.uniqueId] == true) {
                    ++it.thirstValue
                    ++it.thirstValue
                    ++it.thirstValue
                }
            } else {
                ++it.thirstValue
             }

            if (it.thirstValue >= 500) {
                it.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 1000000, 0, true, false))
            } else if (it.thirstValue >= 1000) {
                it.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 1000000, 2, true, false))
            } else if (it.thirstValue >= 2000) {
                it.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 1000000, 4, true, false))
            } else if (it.thirstValue >= 3000) {
                it.addPotionEffect(PotionEffect(PotionEffectType.CONFUSION, 1000000, 0, true, false))
                it.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 1000000, 6, true, false))
            }

            /* ====================================================================================================================================================================================================================== */
            /* ====================================================================================================================================================================================================================== */
            /* ====================================================================================================================================================================================================================== */

            // Stats

            it.scoreboard.getObjective("Freeze")?.getScore(it.name)?.score = it.freezeTicks
            it.scoreboard.getObjective("Thirst")?.getScore(it.name)?.score = it.thirstValue
            it.scoreboard.getObjective("Death")?.getScore(it.name)?.score = 0

            when (statCount++) {
                0 -> {
                    death?.displaySlot = DisplaySlot.BELOW_NAME
                }
                5 -> {
                    health?.displaySlot = DisplaySlot.BELOW_NAME
                }
                10 -> {
                    foodLevel?.displaySlot = DisplaySlot.BELOW_NAME
                }
                20 -> {
                    freeze?.displaySlot = DisplaySlot.BELOW_NAME
                }
                25 -> {
                    thirst?.displaySlot = DisplaySlot.BELOW_NAME
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
                        if (plugin.config.getBoolean("system-message")) {
                            it.playSound(it.location, Sound.ENTITY_WITHER_DEATH, 0.25F, 1.5F)
                            it.addPotionEffect(PotionEffect(PotionEffectType.LEVITATION, 1000000, 0, true, false))
                            it.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 1000000, 255, true, false))
                            it.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1000000, 255, true, false))
                            it.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 1000000, 255, true, false))
                            titleFunction(text("XXXXXXXVVIVVIVVIVVILLLLL."), text(" "))
                        }
                        else {
                            fireworks(it)

                            it.playSound(it.location, Sound.ENTITY_WITHER_DEATH, 0.25F, 1.5F)
                            it.sendMessage(text("Game Stopped.", NamedTextColor.GREEN))
                            titleFunction(text("클리어를 축하드립니다!", NamedTextColor.GREEN), text(" "))

                            stopGame()
                        }
                    }
                    5 -> {
                        if (plugin.config.getBoolean("system-message")) {
                            titleFunction(text("XXXXXXXVVIVVIVVIVVILLLLL."), text("A Rechallenge for the broken world.", NamedTextColor.GRAY))
                        }
                    }
                    10 -> {
                        if (plugin.config.getBoolean("system-message")) {
                            fireworks(it)
                            it.playSound(it.location, Sound.ENTITY_PLAYER_LEVELUP, 1000F, 1F)
                            titleFunction(text("클리어를 축하드립니다!", NamedTextColor.GREEN), text(" "))
                        }
                    }
                    15 -> {
                        if (plugin.config.getBoolean("system-message")) {
                            titleFunction(text("클리어를 축하드립니다!", NamedTextColor.GREEN), text("THE END"))
                        }
                    }
                    20 -> {
                        if (plugin.config.getBoolean("system-message")) {
                            it.removePotionEffect(PotionEffectType.LEVITATION)
                            it.removePotionEffect(PotionEffectType.SLOW)
                            it.removePotionEffect(PotionEffectType.BLINDNESS)

                            it.sendMessage(text("XXXXXXXVVIVVIVVIVVILLLLL.\n"))
                            it.sendMessage(text("제작: BaeHyeonWoo\n\nSpecial Thanks: PyBsh, DytroInc, Underconnor, dolphin2410\n"))
                            it.sendMessage(text("코마님의 구독자 15만명과 21년 12월 15일 생일을 축하드립니다.\n"))
                            it.sendMessage(text("aHR0cHM6Ly9iYWVoeWVvbndvby5jb20vbWVzc2FnZQ==\n"))
                            titleFunction(text(" "), text("다른 여정의 끝, 이젠 좀 쉬어야겠어.", NamedTextColor.GRAY))
                        }

                        plugin.config.set("${it.name}.death", it.scoreboard.getObjective("Death")?.getScore(it.name)?.score)
                        plugin.saveConfig()

                        it.sendMessage(text("총 죽은 횟수: ${plugin.config.getInt("${it.name}.death")}"))

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
