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
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title.Times.of
import net.kyori.adventure.title.Title.title
import org.bukkit.Sound
import org.bukkit.event.HandlerList
import org.bukkit.plugin.Plugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.time.Duration.ofSeconds

/***
 * @author BaeHyeonWoo
 */

class XVLEndingTask : Runnable {

    private fun getInstance(): Plugin {
        return XVLPluginMain.instance
    }

    private var count = 0

    private val server = getInstance().server

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

    override fun run() {
        when (count++) {
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

                        HandlerList.unregisterAll(getInstance())
                        server.scheduler.cancelTasks(getInstance())
                        getInstance().config.set("game-running", false)

                        for (onlinePlayers in server.onlinePlayers) {
                            getInstance().config.set("${onlinePlayers.name}.freezeticks", null)
                            getInstance().config.set("${onlinePlayers.name}.thirstvalue", null)
                            getInstance().saveConfig()
                        }
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
                    titleFunction(text("클리어를 축하드립니다!", NamedTextColor.GREEN), text(""))
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

                        if (getInstance().config.getBoolean("ending-message")) {
                            it.sendMessage(text("XXXXXXXVVIVVIVVIVVILLLLL.\n\n"))
                            it.sendMessage(text("제작: BaeHyeonWoo\n\nSpecial Thanks: PyBsh\n"))
                            it.sendMessage(text("코마님의 구독자 15만명과 21년 12월 15일 생일을 축하드립니다. :D\n"))
                            it.sendMessage(text("aHR0cHM6Ly9iYWVoeWVvbndvby5jb20vbWVzc2FnZQ=="))
                        }
                    }
                    titleFunction(text(" "), text("다른 여정의 끝, 이젠 좀 쉬어야겠어.", NamedTextColor.GRAY))

                    for (onlinePlayers in server.onlinePlayers) {
                        getInstance().config.set("${onlinePlayers.name}.freezeticks", null)
                        getInstance().config.set("${onlinePlayers.name}.thirstvalue", null)
                        getInstance().saveConfig()
                    }

                    HandlerList.unregisterAll(getInstance())
                    server.scheduler.cancelTasks(getInstance())
                    getInstance().config.set("game-running", false)
                }
            }
        }
    }
}