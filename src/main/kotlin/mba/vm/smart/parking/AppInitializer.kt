package mba.vm.smart.parking

import jakarta.servlet.ServletContextEvent
import jakarta.servlet.ServletContextListener

/**
 * SmartParking - mba.vm.smart.parking
 * @description TODO: coming soon.
 * @author VictorModi
 * @email victormodi@outlook.com
 * @date 2024/6/2 下午12:40
 */
class AppInitializer : ServletContextListener {
    override fun contextInitialized(servletContextEvent: ServletContextEvent) {
        val context = servletContextEvent.servletContext
        ConfigReader.init(context)
    }
}
