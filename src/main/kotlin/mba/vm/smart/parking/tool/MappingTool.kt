package mba.vm.smart.parking.tool

import kotlin.math.roundToInt

/**
 * SmartParking - mba.vm.smart.parking.tool
 * @description TODO: coming soon.
 * @author VictorModi
 * @email victormodi@outlook.com
 * @date 2024/6/24 上午12:52
 */

/**
 * MappingTools对象，提供对映射（Map）进行类型安全访问的方法。
 */
object MappingTool {
    /**
     * 从映射中获取指定键的值并将其转换为指定类型。
     * @param key 要获取值的键。
     * @return 如果值存在且类型正确，则返回值，否则返回null。
     */
    inline fun <reified T, K, V> Map<K, V>.getTyped(key: K): T? {
        return this[key] as? T
    }

    /**
     * 从映射中获取指定键的值，并将其转换为Int类型。
     * @param key 要获取值的键。
     * @return 如果值存在且可以转换为Double类型，则返回其四舍五入后的Int值，否则返回null。
     */
    inline fun <reified K, V> Map<K, V>.getInt(key: K): Int? {
        return this.getTyped<Double, K, V>(key)?.roundToInt()
    }

    /**
     * 从映射中获取指定键的值并将其转换为指定类型，如果类型不匹配或键不存在则抛出异常。
     * @param key 要获取值的键。
     * @return 如果值存在且类型正确，则返回值。
     * @throws IllegalArgumentException 如果键不存在或值的类型不匹配。
     */
    inline fun <reified T, K, V> Map<K, V>.getTypedOrThrow(key: K): T {
        return this[key] as? T ?: throw IllegalArgumentException("Key '$key' is missing or not of type ${T::class}")
    }
}
