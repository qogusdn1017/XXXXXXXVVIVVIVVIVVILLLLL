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

import com.baehyeonwoo.xvl.plugin.enums.DecreaseReason
import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.currentDate
import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.ending
import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.gameTaskId
import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.injured
import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.manageFlags
import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.motd
import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.plugin
import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.respawnDelay
import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.respawnTaskId
import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.server
import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.thirstValue
import com.destroystokyo.paper.event.server.PaperServerListPingEvent
import io.github.monun.tap.effect.playFirework
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Color
import org.bukkit.FireworkEffect
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.entity.WitherSkeleton
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.*
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import kotlin.random.Random.Default.nextInt

/***
 * @author BaeHyeonWoo
 */

class XVLGameEvent : Listener {
    private fun decreaseThirst(player: Player, decreaseReason: DecreaseReason) {
        if (player.thirstValue < 600) {
            player.removePotionEffect(PotionEffectType.SLOW)
        } else if (player.thirstValue < 3600) {
            player.removePotionEffect(PotionEffectType.SLOW)
            player.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 1000000, 0, true, false))
        } else if (player.thirstValue < 7200) {
            player.removePotionEffect(PotionEffectType.CONFUSION)
            player.removePotionEffect(PotionEffectType.SLOW)
            player.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 1000000, 2, true, false))
        }

        if (decreaseReason == DecreaseReason.POTION) {
            player.thirstValue = 0.coerceAtLeast(player.thirstValue - 300)
        }
        if (decreaseReason == DecreaseReason.MILK) {
            player.thirstValue = 0.coerceAtLeast(player.thirstValue - 150)
        }
    }

    private fun fallInjured(p: Player) {
        p.sendMessage(text("떨어지시면서 심하게 다치셨습니다! 음.. 오늘은 운이 좋지 않군요.", NamedTextColor.GRAY).decorate(TextDecoration.ITALIC))
        p.addPotionEffect(PotionEffect(PotionEffectType.CONFUSION, 20 * 90, 0, true, false))
        injured[p.uniqueId] = true

        server.scheduler.runTaskLater(plugin, Runnable { injured[p.uniqueId] = false }, 20 * 90L)
    }

    @EventHandler
    fun onEntityDamage(e: EntityDamageEvent) {
        val entity = e.entity

        if (entity is Player) {
            if (e.cause == EntityDamageEvent.DamageCause.FALL) {
                if (e.damage >= entity.health) {
                    if (entity.fallDistance >= 23) {
                        when (nextInt(10)) {
                            0 -> {
                                fallInjured(entity)
                            }
                            1 -> {
                                fallInjured(entity)
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    fun onPlayerRespawn(e: PlayerRespawnEvent) {
        val p = e.player

        if (respawnTaskId != 0) {
            server.scheduler.cancelTask(respawnTaskId)
        }
        respawnDelay[p.uniqueId] = true
        p.sendMessage(text("10초뒤에 움직이실 수 있습니다!", NamedTextColor.RED))
    }

    @EventHandler
    fun onPlayerMove(e: PlayerMoveEvent) {
        val p = e.player

        if (injured[p.uniqueId] == true) {
            e.isCancelled = true
            p.sendMessage(text("부상당한 상태이므로 몸을 움직이실 수 없습니다!", NamedTextColor.RED).decorate(TextDecoration.ITALIC))
        }

        if (respawnDelay[p.uniqueId] == true) {
            e.isCancelled = true
            val respawnTask = server.scheduler.runTaskLater(plugin, Runnable {
                respawnDelay[p.uniqueId] = false
            }, 20 * 10L)

            respawnTaskId = respawnTask.taskId
        }
    }

    // No Damage Ticks to 0
    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        val p = e.player

        p.thirstValue = plugin.config.getInt("${p.name}.thirstvalue")

        e.joinMessage(null)
        p.noDamageTicks = 0
    }

    @EventHandler
    fun onPlayerQuit(e: PlayerQuitEvent) {
        val p = e.player
        e.quitMessage(null)
        plugin.config.set("${p.name}.thirstvalue", p.thirstValue)
        plugin.saveConfig()

        manageFlags(FreezingFlag = false, ThirstyFlag = false, WarmBiomeFlag = false, NetherBiomeFlag = false)
    }

    // Nerfed from original LVX; Returning damage is set to 1.5x, which is lower than original.
    @EventHandler
    fun onEntityDamageByEntityEvent(e: EntityDamageByEntityEvent) {
        val dmgr = e.damager
//
//        if (dmgr is Projectile) {
//            if (dmgr.shooter is Player) {
//                (dmgr.shooter as Player).damage((dmg * 1.5))
//            }
//            else if (dmgr.shooter is Monster) {
//                e.damage = e.damage * 1.5
//            }
//        }
//
//        if (dmgr is Player) {
//            dmgr.damage(dmg * 1.5)
//        }
//
//        if (dmgr is Monster) {
//            e.damage = e.damage * 1.5
//        }
        if (dmgr is WitherSkeleton) {
            e.damage = e.damage * 1.5
        }
    }
    // Suggestion accepted; no JagangDucheon.

    // Bed Event
    @EventHandler
    fun onPlayerBedLeave(e: PlayerBedLeaveEvent) {
        val p = e.player

        if (p.world.time == 0L) {
            when (nextInt(7)) {
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
                    p.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 20 * 90, 0, true, false))
                }
                4 -> {
                    p.sendMessage(text("수면마비(가위눌림)을 겪으셨습니다. 악몽과 비슷하게 썩 좋지많은 않았습니다. (구속 II 1분, 채굴피로 40초)", NamedTextColor.GRAY).decorate(TextDecoration.ITALIC))
                    p.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 20 * 60, 1, true, false))
                    p.addPotionEffect(PotionEffect(PotionEffectType.SLOW_DIGGING, 20 * 40, 0, true, false))
                }
                5 -> {
                    p.sendMessage(text("자는 자세가 뭔가 잘못된걸까요...? 침대에서 굴러떨어지셨습니다.. (멀미 1분)", NamedTextColor.GRAY).decorate(TextDecoration.ITALIC))
                    p.addPotionEffect(PotionEffect(PotionEffectType.CONFUSION, 20 * 60, 0, true, false))
                }
                6 -> {
                    p.sendMessage(text("간만에 매우 편하게 잠을 잤습니다! 고된 노동도 가볍게 느껴질거같은데요? (신속 II 3분, 힘 II 15초, 성급함 2분)", NamedTextColor.GRAY).decorate(TextDecoration.ITALIC))
                    p.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 20 * 180, 1, true, false))
                    p.addPotionEffect(PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 15, 1, true, false))
                    p.addPotionEffect(PotionEffect(PotionEffectType.FAST_DIGGING, 20 * 120, 0, true, false))
                }
            }
        }
    }

    // Milk Event, Decrease Thirst
    @EventHandler
    fun onPlayerItemConsume(e: PlayerItemConsumeEvent) {
        val p = e.player
        val type = e.item.type

        if (type == Material.MILK_BUCKET) {
            when (nextInt(3)) {
                0 -> {
                    p.sendMessage(text("괜찮은 우유를 드신 것 같습니다. (기본 우유 효과)", NamedTextColor.GRAY).decorate(TextDecoration.ITALIC))
                }
                1 -> {
                    p.sendMessage(text("우유가 상한 건지, 소에 병이 들은건지, 무엇인지는 몰라도 일단 좋은 우유는 아닌것 같습니다... (독 10초)", NamedTextColor.GRAY).decorate(TextDecoration.ITALIC))
                    server.scheduler.runTaskLater(plugin, Runnable {
                        p.addPotionEffect(PotionEffect(PotionEffectType.POISON, 200, 0, true, false))
                    }, 10L)
                }
                2 -> {
                    p.sendMessage(text("오늘따라 우유를 먹을 컨디션은 아닌 것 같네요... (멀미 15초)", NamedTextColor.GRAY).decorate(TextDecoration.ITALIC))
                    server.scheduler.runTaskLater(plugin, Runnable {
                        p.addPotionEffect(PotionEffect(PotionEffectType.CONFUSION, 20 * 15, 0, true, false))
                    }, 10L)
                }
            }
            decreaseThirst(p, DecreaseReason.MILK)
        }
        if (e.item.type == Material.POTION) {
            decreaseThirst(p, DecreaseReason.POTION)
        }
    }

    // Check ending conditions
    @EventHandler
    fun onPlayerAdvancementDone(e: PlayerAdvancementDoneEvent) {
        val firework = FireworkEffect.builder().with(FireworkEffect.Type.STAR).withColor(Color.AQUA).build()
        val advc = e.advancement

        if (advc.key.toString() == "minecraft:end/kill_dragon") {
            plugin.config.set("kill-dragon", true)
            plugin.saveConfig()
        }
        if (advc.key.toString() == "minecraft:nether/create_beacon") {
            if (plugin.config.getBoolean("kill-dragon")) {
                server.onlinePlayers.forEach {
                    val loc = it.location.add(0.0, 0.9, 0.0)

                    loc.world.playFirework(loc, firework)
                }
                server.scheduler.cancelTask(gameTaskId)
                ending = true
            }
        }
    }

    @EventHandler
    fun onPaperServerListPing(e: PaperServerListPingEvent) {
        // Project start date; it has been planned earlier, but I forgot to set up the Wakatime & in this date I actually started writing in-game managing codes.

        e.numPlayers = 20211122
        e.maxPlayers = currentDate()
        e.playerSample.clear()
        e.motd(motd())
    }
}