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

package com.baehyeonwoo.xvl.plugin.events

import com.baehyeonwoo.xvl.plugin.XVLPluginMain
import com.baehyeonwoo.xvl.plugin.objects.XVLGameStatus.thirstValue
import com.baehyeonwoo.xvl.plugin.tasks.XVLEndingTask
import com.destroystokyo.paper.event.server.PaperServerListPingEvent
import io.github.monun.tap.effect.playFirework
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.md_5.bungee.api.ChatColor
import org.bukkit.FireworkEffect
import org.bukkit.Material
import org.bukkit.entity.Monster
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerAdvancementDoneEvent
import org.bukkit.event.player.PlayerBedLeaveEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.plugin.Plugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.awt.Color
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.random.Random.Default.nextInt

/***
 * @author BaeHyeonWoo
 */

class XVLEvent : Listener {
    private fun getInstance(): Plugin {
        return XVLPluginMain.instance
    }

    private val server = getInstance().server

    // 기존 LVX에서 너프; 데미지 1.5배로 이전 코드보다 절반 이상 감소
    @EventHandler
    fun onEntityDamageByEntityEvent(e: EntityDamageByEntityEvent) {
        val dmgr = e.damager
        val dmg = e.finalDamage

        if (dmgr is Projectile) {
            if (dmgr.shooter is Player) {
                (dmgr.shooter as Player).damage((dmg * 1.5))
            } else return

            if (e.entity is Player) {
                e.damage = e.damage * 1.5
            }
        }

        if (dmgr is Player) {
            dmgr.damage(dmg * 1.5)
        }

        if (dmgr is Monster) {
            e.damage = e.damage * 1.5
        }
    }

    @EventHandler
    fun onPlayerBedLeave(e: PlayerBedLeaveEvent) {
        val p = e.player

        if (p.world.time == 0L) {
            when (nextInt(7)) {
                0 -> {
                    p.sendMessage(
                        text(
                            "잠을 매우 잘 잤고 컨디션이 나름 괜찮습니다! 힘을 더 쓸 수 있을지도요...? (신속 II 3분, 힘 II 30초)",
                            NamedTextColor.GRAY
                        ).decorate(TextDecoration.ITALIC)
                    )
                    p.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 20 * 180, 1, true, false))
                    p.addPotionEffect(PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 30, 1, true, false))
                }
                1 -> {
                    p.sendMessage(
                        text("머리가 매우 멍하고 컨디션이 좋지 않습니다... (구속 2분)", NamedTextColor.GRAY).decorate(
                            TextDecoration.ITALIC
                        )
                    )
                    p.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 20 * 120, 0, true, false))
                }
                2 -> {
                    p.sendMessage(
                        text(
                            "악몽을 꾸었습니다. 다시는 생각해보기도 싫은 매우 끔찍한 악몽이었습니다. (구속 II 3분, 채굴피로 1분)",
                            NamedTextColor.GRAY
                        ).decorate(TextDecoration.ITALIC)
                    )
                    p.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 20 * 180, 1, true, false))
                    p.addPotionEffect(PotionEffect(PotionEffectType.SLOW_DIGGING, 20 * 60, 0, true, false))
                }
                3 -> {
                    p.sendMessage(
                        text("컨디션이 평범합니다. 평소처럼 활동 할 수 있을듯 합니다. (신속 1분 30초)", NamedTextColor.GRAY).decorate(
                            TextDecoration.ITALIC
                        )
                    )
                    p.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 20 * 90, 0, true, false))
                }
                4 -> {
                    p.sendMessage(
                        text(
                            "수면마비(가위눌림)을 겪으셨습니다. 악몽과 비슷하게 썩 좋지많은 않았습니다. (구속 II 1분, 채굴피로 40초)",
                            NamedTextColor.GRAY
                        ).decorate(TextDecoration.ITALIC)
                    )
                    p.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 20 * 60, 1, true, false))
                    p.addPotionEffect(PotionEffect(PotionEffectType.SLOW_DIGGING, 20 * 40, 0, true, false))
                }
                5 -> {
                    p.sendMessage(
                        text("자는 자세가 뭔가 잘못된걸까요...? 침대에서 굴러떨어지셨습니다.. (멀미 1분)", NamedTextColor.GRAY).decorate(
                            TextDecoration.ITALIC
                        )
                    )
                    p.addPotionEffect(PotionEffect(PotionEffectType.CONFUSION, 20 * 60, 0, true, false))
                }
                6 -> {
                    p.sendMessage(
                        text(
                            "간만에 매우 편하게 잠을 잤습니다! 고된 노동도 가볍게 느껴질거같은데요? (신속 II 3분, 힘 II 15초, 성급함 2분)",
                            NamedTextColor.GRAY
                        ).decorate(TextDecoration.ITALIC)
                    )
                    p.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 20 * 180, 1, true, false))
                    p.addPotionEffect(PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 15, 1, true, false))
                    p.addPotionEffect(PotionEffect(PotionEffectType.FAST_DIGGING, 20 * 120, 0, true, false))
                }
            }
        }
    }

    @EventHandler
    fun onPlayerItemConsume(e: PlayerItemConsumeEvent) {
        val p = e.player
        val type = e.item.type

        if (type == Material.MILK_BUCKET) {
            when (nextInt(2)) {
                0 -> {
                    p.sendMessage(
                        text(
                            "괜찮은 우유를 드신 것 같습니다. (기본 우유 효과)",
                            NamedTextColor.GRAY
                        ).decorate(TextDecoration.ITALIC)
                    )
                }
                1 -> {
                    p.sendMessage(
                        text(
                            "우유가 상한 건지, 소에 병이 들은건지, 무엇인지는 몰라도 일단 좋은 우유는 아닌것 같습니다... (독 10초)",
                            NamedTextColor.GRAY
                        ).decorate(TextDecoration.ITALIC)
                    )
                }
            }
        }
        if (e.item.type == Material.POTION) {
            if (p.thirstValue < 600) {
                p.removePotionEffect(PotionEffectType.SLOW)
            } else if (p.thirstValue < 3600) {
                p.removePotionEffect(PotionEffectType.SLOW)
                p.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 1000000, 0, true, false))
            } else if (p.thirstValue < 7200) {
                p.removePotionEffect(PotionEffectType.CONFUSION)
                p.removePotionEffect(PotionEffectType.SLOW)
                p.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 1000000, 2, true, false))
            }
            if (p.thirstValue - 300 > 0) p.thirstValue = p.thirstValue - 300
            else if (p.thirstValue - 300 <= 0) p.thirstValue = 0
        }
    }

    // 엔딩 조건 체크
    @EventHandler
    fun onPlayerAdvancementDone(e: PlayerAdvancementDoneEvent) {
        val firework = FireworkEffect.builder().with(FireworkEffect.Type.STAR).withColor(org.bukkit.Color.AQUA).build()
        val advc = e.advancement

        if (advc.key.toString() == "minecraft:end/kill_dragon") {
            getInstance().config.set("kill-dragon", true)
            getInstance().saveConfig()
        }
        if (advc.key.toString() == "minecraft:nether/create_full_beacon") {
            if (getInstance().config.getBoolean("kill-dragon")) {
                server.onlinePlayers.forEach {
                    val loc = it.location.add(0.0, 0.9, 0.0)

                    loc.world.playFirework(loc, firework)
                }
                server.scheduler.cancelTasks(getInstance())
                server.scheduler.runTaskTimer(getInstance(), XVLEndingTask(), 20L, 20L)
                HandlerList.unregisterAll(getInstance())
            }
        }
    }

    @EventHandler
    fun onPaperServerListPing(e: PaperServerListPingEvent) {
        val motdString = ("X X X X X X X V V I V V I V V I V V I L L L L L .")
        val localDateTime = LocalDateTime.now()
        val dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd")

        val words = motdString.split(" ").toMutableList()

        for (i in words.indices) {
            words[i] = "${ChatColor.of(Color(nextInt(0xFF0000)))}" + words[i]
        }

        val stringJoiner = StringJoiner("")

        for (word in words) {
            stringJoiner.add(word)
        }

        val sentence = stringJoiner.toString()

        e.numPlayers = 20211122
        e.maxPlayers = localDateTime.format(dateFormat).toInt()

        e.playerSample.clear()

        e.motd(text(sentence))
    }
}