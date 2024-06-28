package mba.vm.smart.parking.tool

import mba.vm.smart.parking.frontend.ui.HTMLElement

/**
 * SmartParking - mba.vm.smart.parking.tool
 * @description TODO: coming soon.
 * @author VictorModi
 * @email victormodi@outlook.com
 * @date 2024/6/25 上午12:26
 */
object OrNothingTool {
    fun StringBuilder.appendOrNothing(bool: Boolean, s: String): StringBuilder {
        if (bool) this.append(s)
        return this
    }

    fun HTMLElement.addChildOrNothing(bool: Boolean, element: HTMLElement): HTMLElement {
        if (bool) this.addChild(element)
        return this
    }
}
