package mba.vm.smart.parking.data

import mba.vm.smart.parking.data.handler.BaseDataHandler
import mba.vm.smart.parking.data.handler.CarHandler
import mba.vm.smart.parking.data.handler.ParkingStatusHandler
import mba.vm.smart.parking.data.handler.UserHandler
import mba.vm.smart.parking.tool.MappingTool.getTyped
import javax.xml.crypto.Data

/**
 * SmartParking - mba.vm.smart.parking.data
 * @description https://github.com/VictorModi/Smart_Parking
 * @author VictorModi
 * @email victormodi@outlook.com
 * @date 2024/6/20 下午7:50
 */
data class DataRequest(
    val actionType: ActionType,
    val dataType: DataType,
    val data: Map<String, Any>?
) {
    companion object {
        val handlerMap: Map<DataType, BaseDataHandler> = mapOf(
            DataType.USER to UserHandler,
            DataType.CAR to CarHandler,
            DataType.PARKING_STATUS to ParkingStatusHandler,
        )

        private inline fun <reified T : Enum<T>> String.toEnum(): Result<T> {
            return try {
                Result.success(enumValueOf<T>(this.uppercase()))
            } catch (e: IllegalArgumentException) {
                Result.failure(IllegalArgumentException("$this is not a valid value for ${T::class.java.simpleName}"))
            }
        }

        fun fromMap(data: Map<String, Any>): Result<DataRequest> {
            val actionTypeResult = (data.getTyped<String, String, Any>("action"))?.toEnum<ActionType>()
                ?.getOrElse { return Result.failure(it) }
                ?: return Result.failure(IllegalArgumentException("Missing or invalid 'action' field"))

            val dataTypeResult = (data.getTyped<String, String, Any>("type"))?.toEnum<DataType>()
                ?.getOrElse { return Result.failure(it) }
                ?: return Result.failure(IllegalArgumentException("Missing or invalid 'type' field"))

            val dataMap: Map<String, Any> = data.getTyped<Map<String, Any>, String, Any>("data")
                ?: return Result.failure(IllegalArgumentException("Missing or invalid 'data' field"))

            return Result.success(DataRequest(actionTypeResult, dataTypeResult, dataMap))
        }
    }

    override fun toString(): String {
        return "DataRequest(actionType=$actionType, dataType=$dataType, data=$data)"
    }

    enum class ActionType {
        INSERT,
        DELETE,
        UPDATE,
        SELECT
    }

    enum class DataType {
        USER,
        CAR,
        PARKING_STATUS,
        CHARGE_HISTORY,
    }
}

