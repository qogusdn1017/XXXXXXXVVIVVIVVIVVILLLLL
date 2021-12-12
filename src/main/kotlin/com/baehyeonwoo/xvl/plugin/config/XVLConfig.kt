package com.baehyeonwoo.xvl.plugin.config

import io.github.monun.tap.config.Config
import io.github.monun.tap.config.ConfigSupport
import java.io.File

object XVLConfig {
    @Config
    var killDragon = false

    fun load(configFile: File) {
        ConfigSupport.compute(this, configFile)
    }
}