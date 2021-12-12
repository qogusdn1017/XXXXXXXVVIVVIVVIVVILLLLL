package com.baehyeonwoo.xvl.plugin.tasks

import com.baehyeonwoo.xvl.plugin.XVLPluginMain
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.title.Title.Times.of
import net.kyori.adventure.title.Title.title
import org.bukkit.plugin.Plugin
import java.time.Duration.ofSeconds

class XVLEndingTask : Runnable {

    private fun getInstance(): Plugin {
        return XVLPluginMain.instance
    }

    private var count = 0

    private val server = getInstance().server

    private fun titleFunction(title: String? = null, subtitle: String? = null) {
        server.onlinePlayers.forEach {
            if (title != null && subtitle != null) {
                it.showTitle(title(text(title), text(subtitle), of(ofSeconds(0.5.toLong()), ofSeconds(8), ofSeconds(0.5.toLong()))))
            }
            else if (title != null && subtitle == null) {
                it.showTitle(title(text(title), text(""), of(ofSeconds(0.5.toLong()), ofSeconds(8), ofSeconds(0.5.toLong()))))
            }
            else if (title == null && subtitle == null) return
        }
    }

    private fun textFunction(msg: String) {
        server.onlinePlayers.forEach {
            it.sendMessage(text(msg))
        }
    }

    override fun run() {
        when (count++) {
            5 -> {
                titleFunction("title", "subtitle")
                textFunction("text")
            }
            15 -> {
                titleFunction("another title", "another subtitle")
                textFunction("another text")
            }
        }
    }
}