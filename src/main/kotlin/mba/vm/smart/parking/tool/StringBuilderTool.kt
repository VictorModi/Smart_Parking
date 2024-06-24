package mba.vm.smart.parking.tool

/**
 * SmartParking - mba.vm.smart.parking.tool
 * @description TODO: coming soon.
 * @author VictorModi
 * @email victormodi@outlook.com
 * @date 2024/6/25 上午12:26
 */
object StringBuilderTool {
    fun StringBuilder.appendOrNothing(bool: Boolean, s: String): StringBuilder {
        if (bool) this.append(s)
        return this
    }
}
