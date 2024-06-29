package mba.vm.smart.parking.data.handler

import mba.vm.smart.parking.UserType
import mba.vm.smart.parking.data.DataRequest
import mba.vm.smart.parking.data.DataResult

/**
 * SmartParking - mba.vm.smart.parking.data.handler
 * @description https://github.com/VictorModi/Smart_Parking
 * @author VictorModi
 * @email victormodi@outlook.com
 * @date 2024/6/20 上午8:52
 */
/**
 * BaseDataHandler抽象类，定义了数据处理的基础结构和权限控制。
 */
abstract class BaseDataHandler {

    /**
     * AccessLevel枚举，定义了访问级别：读取、写入和无访问权限。
     */
    enum class AccessLevel {
        READ, WRITE, NO_ACCESS
    }

    /**
     * 读取权限的用户类型数组，需由子类实现。
     */
    abstract val readPermission: Array<UserType>

    /**
     * 写入权限的用户类型数组，需由子类实现。
     */
    abstract val writePermission: Array<UserType>

    /**
     * 获取当前用户的访问级别。
     * @param currentUserType 当前用户的类型。
     * @return 对应的访问级别。
     */
    fun getAccessLevel(currentUserType: UserType): AccessLevel {
        return when (currentUserType) {
            in writePermission -> AccessLevel.WRITE
            in readPermission -> AccessLevel.READ
            else -> AccessLevel.NO_ACCESS
        }
    }

    /**
     * 处理数据请求，根据用户类型和操作类型执行相应的操作。
     * @param currentUserType 当前用户的类型。
     * @param actionType 请求的操作类型。
     * @param data 请求的数据。
     * @return 数据操作的结果。
     */
    fun handle(currentUserType: UserType, actionType: DataRequest.ActionType, data: Map<String, Any>?): DataResult {
        // 检查用户权限
        when (getAccessLevel(currentUserType)) {
            AccessLevel.READ -> if (actionType !== DataRequest.ActionType.SELECT)
                return DataResult.createFailure("权限不足，缺少写权限")
            AccessLevel.WRITE -> {} // 写入权限通过
            AccessLevel.NO_ACCESS -> return DataResult.createFailure("权限不足")
        }

        // 根据操作类型执行相应的方法
        return when (actionType) {
            DataRequest.ActionType.SELECT -> select(data)
            DataRequest.ActionType.INSERT -> insert(data)
            DataRequest.ActionType.DELETE -> delete(data)
            DataRequest.ActionType.UPDATE -> update(data)
        }
    }

    /**
     * 抽象方法，需由子类实现，定义选择操作。
     * @param data 请求的数据。
     * @return 数据操作的结果。
     */
    abstract fun select(data: Map<String, Any>?): DataResult

    /**
     * 抽象方法，需由子类实现，定义插入操作。
     * @param data 请求的数据。
     * @return 数据操作的结果。
     */
    abstract fun insert(data: Map<String, Any>?): DataResult

    /**
     * 抽象方法，需由子类实现，定义更新操作。
     * @param data 请求的数据。
     * @return 数据操作的结果。
     */
    abstract fun update(data: Map<String, Any>?): DataResult

    /**
     * 抽象方法，需由子类实现，定义删除操作。
     * @param data 请求的数据。
     * @return 数据操作的结果。
     */
    abstract fun delete(data: Map<String, Any>?): DataResult
}
