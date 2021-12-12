package com.baehyeonwoo.xvl.plugin.tasks

import com.baehyeonwoo.xvl.plugin.XVLPluginMain
import net.kyori.adventure.text.Component.text
import org.bukkit.Material
import org.bukkit.plugin.Plugin


class XVLFreezingTask : Runnable {
    private fun getInstance(): Plugin {
        return XVLPluginMain.instance
    }

    private val server = getInstance().server

    private val warmArray = arrayListOf(
        Material.FIRE,
        Material.SOUL_FIRE,
        Material.CAMPFIRE,
        Material.SOUL_CAMPFIRE,
        Material.LAVA,
        Material.LAVA_CAULDRON,
        Material.MAGMA_BLOCK,
        Material.TORCH,
        Material.SOUL_TORCH
    )

    override fun run() {
        for (onlinePlayers in server.onlinePlayers) {
            val location = onlinePlayers.location

            for (x in location.blockX - 2..location.blockX + 2) {
                for (y in location.blockY - 2..location.blockY + 2) {
                    for (z in location.blockZ - 2..location.blockZ + 2) {
                        if (warmArray.contains((location.world.getBlockAt(x, y, z)).type)) {
                            server.broadcast(text("WARMBLOCK NEARBY"))
                            getInstance().config.set("${onlinePlayers.name}.warmflag", true)
                            getInstance().saveConfig()
                        }
                    }
                }
            }

            server.broadcast(text("WARMFLAG: ${getInstance().config.getBoolean("${onlinePlayers.name}.warmflag")}"))

            if (getInstance().config.getBoolean("${onlinePlayers.name}.warmflag")) {
                // warm
                server.broadcast(text("WARM"))
                if (onlinePlayers.freezeTicks != 0) --onlinePlayers.freezeTicks

                getInstance().config.set("${onlinePlayers.name}.warmflag", false)
                getInstance().saveConfig()
            }
            else {
                //cold
                server.broadcast(text("COLD"))
                onlinePlayers.freezeTicks = ++onlinePlayers.freezeTicks
            }
        }
    }
}
