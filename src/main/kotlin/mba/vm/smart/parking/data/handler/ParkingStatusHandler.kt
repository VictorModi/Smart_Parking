package mba.vm.smart.parking.data.handler

import mba.vm.smart.parking.Car_informationQueries
import mba.vm.smart.parking.DataSourceProvider.getDatabase
import mba.vm.smart.parking.Parking_spaceQueries
import mba.vm.smart.parking.UserType
import mba.vm.smart.parking.data.DataResult
import mba.vm.smart.parking.tool.MappingTool.getInt
import mba.vm.smart.parking.tool.MappingTool.getTyped

/**
 * SmartParking - mba.vm.smart.parking.data.handler
 * @description https://github.com/VictorModi/Smart_Parking
 * @author VictorModi
 * @email victormodi@outlook.com
 * @date 2024/6/29 上午10:45
 */
object ParkingStatusHandler : BaseDataHandler(){
    /**
     * 读取权限的用户类型数组，需由子类实现。
     */
    override val readPermission: Array<UserType> = arrayOf(UserType.PARKING_ADMIN, UserType.SUPER_ADMIN)

    /**
     * 写入权限的用户类型数组，需由子类实现。
     */
    override val writePermission: Array<UserType> = arrayOf(UserType.PARKING_ADMIN, UserType.SUPER_ADMIN)

    private val queries: Parking_spaceQueries = getDatabase().parking_spaceQueries

    override fun select(data: Map<String, Any>?): DataResult {
        if (data.isNullOrEmpty()) return DataResult.createSuccess(queries.getSpaces().executeAsList())
        val isCount = data.getTyped<Boolean, String, Any>("count") ?: false
        val isLimited = data.getInt("limit") != null && data.getInt("offset") != null;

        return when (Pair(isCount, isLimited)) {
            Pair(true, true) -> DataResult.createSuccessByCount(
                queries.getLimitDataCountByFilter(
                    location = data.getTyped("location")
                ).executeAsOne()
            )
            Pair(false, true) -> DataResult.createSuccess(
                queries.getLimitDataByFilter(
                    location = data.getTyped("location")
                ).executeAsList()
            )
            Pair(true, false) -> DataResult.createSuccessByCount(
                queries.getDataCountByFilter(
                    location = data.getTyped("location")
                ).executeAsOne()
            )
            Pair(false, false) -> DataResult.createSuccess(
                queries.getLimitDataByFilter(
                    location = data.getTyped("location")
                ).executeAsList()
            )
            else -> DataResult.createFailure("WTF??")
        }
    }

    override fun insert(data: Map<String, Any>?): DataResult {
        if (data.isNullOrEmpty()) return DataResult.createFailure("Parking Space need data to insert")
        queries.addParkingSpaceWithCarId(
            data.getTyped("location"),
            data.getTyped("current_car_id"),
        )
        return DataResult.createSuccess()
    }

    override fun update(data: Map<String, Any>?): DataResult {
        if (data.isNullOrEmpty()) return DataResult.createFailure("Parking Space need data to update")
        val targetID: Int = data.getInt("id") ?: run {
            return DataResult.createFailure("Parking Space needs target id to update")
        }
        queries.updateParkingSpace(
            data.getTyped("location"),
            data.getTyped("current_car_id"),
            targetID
        )
        return DataResult.createByAffectedRows(queries.getAffectedRows().executeAsOne())
    }

    override fun delete(data: Map<String, Any>?): DataResult {
        if (data.isNullOrEmpty()) return DataResult.createFailure("Parking Space needed id to delete")
        val targetID: Int = data.getInt("id")
            ?: return DataResult.createFailure("Car information needs target id to delete")
        queries.deleteParkingSpaceByID(targetID)
        return DataResult.createByAffectedRows(queries.getAffectedRows().executeAsOne())
    }
}
