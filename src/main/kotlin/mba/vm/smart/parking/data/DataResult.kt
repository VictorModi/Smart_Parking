package mba.vm.smart.parking.data

/**
 * SmartParking - mba.vm.smart.parking.data
 * @description TODO: coming soon.
 * @author VictorModi
 * @email victormodi@outlook.com
 * @date 2024/6/21 上午9:00
 */

/**
 * DataResult数据类，用于表示操作的结果。
 * @param success 表示操作是否成功。
 * @param message 操作的消息，可能为null。
 * @param count 计数结果，可能为null。
 * @param affectedRows 影响的行数，可能为null。
 * @param list 返回的数据列表，可能为null。
 */
data class DataResult(
    val success: Boolean,
    val message: String?,
    val count: Long?,
    val affectedRows: Long?,
    val list: List<Any>?
) {
    /**
     * DataResult的伴生对象，用于创建不同类型的DataResult实例。
     */
    companion object {
        /**
         * 创建一个成功的DataResult实例，包含影响的条目数。
         * @param count 计数结果。
         * @return 一个新的DataResult实例。
         */
        fun createSuccessByCount(count: Long): DataResult {
            return DataResult(true, "", count, null, null)
        }

        /**
         * 创建一个根据影响行数的DataResult实例。
         * @param affectedRows 影响的行数。
         * @return 一个新的DataResult实例。
         */
        fun createByAffectedRows(affectedRows: Long): DataResult {
            return DataResult(
                affectedRows > 0,
                if (affectedRows > 0) null else "None of the rows have been modified.",
                affectedRows,
                null,
                null
            )
        }

        /**
         * 创建一个成功的DataResult实例，包含可选的数据列表。
         * @param list 返回的数据列表，默认为null。
         * @return 一个新的DataResult实例。
         */
        fun createSuccess(list: List<Any>? = null): DataResult {
            return DataResult(true, "", null, null, list)
        }

        /**
         * 创建一个失败的DataResult实例，包含可选的失败消息。
         * @param message 失败的消息，默认为null。
         * @return 一个新的DataResult实例。
         */
        fun createFailure(message: String? = null): DataResult {
            return DataResult(false, message, null, null, null)
        }
    }

    /**
     * 将DataResult实例转换为Map。
     * @return 包含DataResult数据的Map，如果没有有效数据则返回null。
     */
    fun toMap(): Map<String, Any>? {
        return when {
            list != null -> mapOf("list" to list)
            affectedRows != null -> mapOf("affectedRows" to affectedRows)
            count != null -> mapOf("count" to count)
            else -> null
        }
    }
}
