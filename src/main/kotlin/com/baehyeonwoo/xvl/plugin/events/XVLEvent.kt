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
import com.baehyeonwoo.xvl.plugin.tasks.XVLEndingTask
import com.destroystokyo.paper.event.server.PaperServerListPingEvent
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.md_5.bungee.api.ChatColor
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Monster
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent
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

    private val config = getInstance().config

    private val killedDragon = config.getBoolean("killed-dragon")

    // 기존 LVX에서 너프; 데미지 0.5배. 절반 이상 감소
    @EventHandler
    fun onEntityDamageByEntityEvent(e: EntityDamageByEntityEvent) {
        val dmgr = e.damager
        val dmg = e.finalDamage

        if (dmgr is Projectile) {
            if (dmgr.shooter is Player) {
                (dmgr.shooter as Player).damage((dmg * 1.5))
            }
            else return

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
            when (nextInt(4)) {
                0 -> {
                    p.sendMessage(text("잠을 매우 잘 잤고 컨디션이 나름 괜찮습니다! 힘을 더 쓸 수 있을지도요...? (신속 II 3분, 힘 II 30초)", NamedTextColor.GRAY).decorate(TextDecoration.ITALIC))
                    p.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 20 * 180, 1, true, false))
                    p.addPotionEffect(PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 30, 1, true, false))
                }
                1 -> {
                    p.sendMessage(text("머리가 매우 멍하고 컨디션이 좋지 않습니다... (구속 2분)", NamedTextColor.GRAY).decorate(TextDecoration.ITALIC))
                    p.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 20 * 120, 0, true, false))
                }
                2 -> {
                    p.sendMessage(text("악몽을 꾸었습니다. 다시는 생각해보기도 싫은 매우 끔찍한 악몽이었습니다. (구속 II 3분, 채굴피로 1분)", NamedTextColor.GRAY).decorate(TextDecoration.ITALIC))
                    p.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 20 * 180, 1, true, false))
                    p.addPotionEffect(PotionEffect(PotionEffectType.SLOW_DIGGING, 20 * 60, 0, true, false))
                }
                3 -> {
                    p.sendMessage(text("컨디션이 평범합니다. 평소처럼 활동 할 수 있을듯 합니다. (신속 1분 30초)", NamedTextColor.GRAY).decorate(TextDecoration.ITALIC))
                    p.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 20 * 90, 0 ,true, false))
                }
            }
        }
    }

    @EventHandler
    fun onPlayerItemConsume(e: PlayerItemConsumeEvent) {
        val p = e.player
        val type = e.item.type

        if (type == Material.MILK_BUCKET) {
            when(nextInt(2)) {
                0 -> {
                    p.sendMessage(text("괜찮은 우유를 드신 것 같습니다. (기본 우유 효과)", NamedTextColor.GRAY).decorate(TextDecoration.ITALIC))
                }
                1 -> {
                    p.sendMessage(text("우유가 상한 건지, 소에 병이 들은건지, 무엇인지는 몰라도 일단 좋은 우유는 아닌것 같습니다... (독 10초)", NamedTextColor.GRAY).decorate(TextDecoration.ITALIC))
                }
            }
        }
    }

    // 엔딩 조건 체크
    @EventHandler
    fun onEntityDeathEvent(e: EntityDeathEvent) {
        val entity = e.entity

        if (entity.type == EntityType.ENDER_DRAGON) {
            if (entity.killer is Player) {
                getInstance().config.set("kill-dragon", true)
                getInstance().saveConfig()
            }
        }
        if (entity.type == EntityType.WITHER) {
            if (killedDragon) {
                if (entity.killer is Player) {
                    server.scheduler.cancelTasks(getInstance())
                    server.scheduler.scheduleSyncRepeatingTask(getInstance(), XVLEndingTask(), 20, 20)
                    HandlerList.unregisterAll(getInstance())
                }
            }
            else return
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