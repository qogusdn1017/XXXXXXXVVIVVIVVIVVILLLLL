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
import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.getInstance
import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.isNetherBiome
import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.isWarmBiome
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
import kotlin.random.Random.Default.nextFloat

/***
 * @author BaeHyeonWoo
 */

class XVLSecondCountTask: Runnable {
    private val fwEffect = FireworkEffect.builder().with(FireworkEffect.Type.STAR).withColor(org.bukkit.Color.AQUA).build()

    private fun fireworks(it: Player) {
        it.world.spawn(it.location, Firework::class.java).apply {
            it.location.yaw = nextFloat() * 360.0F
            it.location.pitch = -45F + nextFloat() * -45.0F
            fireworkMeta = fireworkMeta.apply {
                addEffects(fwEffect)
                power = 30
            }
            velocity = it.location.direction.multiply(1.5)
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

            if (onlinePlayers.thirstValue >= 500) {
                onlinePlayers.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 1000000, 0, true, false))
            } else if (onlinePlayers.thirstValue >= 1000) {
                onlinePlayers.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 1000000, 2, true, false))
            } else if (onlinePlayers.thirstValue >= 2000) {
                onlinePlayers.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 1000000, 4, true, false))
            } else if (onlinePlayers.thirstValue >= 3000) {
                onlinePlayers.addPotionEffect(PotionEffect(PotionEffectType.CONFUSION, 1000000, 0, true, false))
                onlinePlayers.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 1000000, 6, true, false))
            }

            /* ====================================================================================================================================================================================================================== */
            /* ====================================================================================================================================================================================================================== */
            /* ====================================================================================================================================================================================================================== */

            // Stats

            onlinePlayers.scoreboard.getObjective("Freeze")?.getScore(onlinePlayers.name)?.score = onlinePlayers.freezeTicks
            onlinePlayers.scoreboard.getObjective("Thirst")?.getScore(onlinePlayers.name)?.score = onlinePlayers.thirstValue
            onlinePlayers.scoreboard.getObjective("Death")?.getScore(onlinePlayers.name)?.score = 0

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
                        if (getInstance().config.getBoolean("system-message")) {
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
                                fireworks(it)

                                it.playSound(it.location, Sound.ENTITY_WITHER_DEATH, 0.25F, 1.5F)
                                it.sendMessage(text("Game Stopped.", NamedTextColor.GREEN))
                                titleFunction(text("클리어를 축하드립니다!", NamedTextColor.GREEN), text(" "))

                                stopGame()
                            }
                        }
                    }
                    5 -> {
                        if (getInstance().config.getBoolean("system-message")) {
                            titleFunction(text("XXXXXXXVVIVVIVVIVVILLLLL."), text("A Rechallenge for the broken world.", NamedTextColor.GRAY))
                        }
                    }
                    10 -> {
                        if (getInstance().config.getBoolean("system-message")) {
                            server.onlinePlayers.forEach {
                                fireworks(it)
                                it.playSound(it.location, Sound.ENTITY_PLAYER_LEVELUP, 1000F, 1F)
                                titleFunction(text("클리어를 축하드립니다!", NamedTextColor.GREEN), text(" "))
                            }
                        }
                    }
                    15 -> {
                        if (getInstance().config.getBoolean("system-message")) {
                            titleFunction(text("클리어를 축하드립니다!", NamedTextColor.GREEN), text("THE END"))
                        }
                    }
                    20 -> {
                        if (getInstance().config.getBoolean("system-message")) {
                            server.onlinePlayers.forEach {
                                it.removePotionEffect(PotionEffectType.LEVITATION)
                                it.removePotionEffect(PotionEffectType.SLOW)
                                it.removePotionEffect(PotionEffectType.BLINDNESS)

                                it.sendMessage(text("XXXXXXXVVIVVIVVIVVILLLLL.\n"))
                                it.sendMessage(text("제작: BaeHyeonWoo\n\nSpecial Thanks: PyBsh, DytroInc, Underconnor, dolphin2410\n"))
                                it.sendMessage(text("코마님의 구독자 15만명과 21년 12월 15일 생일을 축하드립니다.\n"))
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
