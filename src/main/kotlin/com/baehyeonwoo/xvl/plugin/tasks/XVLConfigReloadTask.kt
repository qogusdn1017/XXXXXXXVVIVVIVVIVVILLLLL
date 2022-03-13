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

import com.baehyeonwoo.xvl.plugin.objects.XVLGameContentManager.plugin
import java.io.File

/***
 * @author BaeHyeonWoo
 */

class XVLConfigReloadTask: Runnable {
    private val configFile = File(plugin.dataFolder, "config.yml")

    private var configFileLastModified = configFile.lastModified()

    override fun run() {
        // Live Config Reloading

        if (configFileLastModified != configFile.lastModified()) {
            plugin.logger.info("Config Reloaded.")
            plugin.reloadConfig()
            plugin.saveConfig()

            configFileLastModified = configFile.lastModified()
        }
    }
}