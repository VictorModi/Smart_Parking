package mba.vm.smart.parking.data.handler

import mba.vm.smart.parking.Car_informationQueries
import mba.vm.smart.parking.DataSourceProvider.getDatabase
import mba.vm.smart.parking.UserType
import mba.vm.smart.parking.data.DataResult
import mba.vm.smart.parking.tool.MappingTool.getInt
import mba.vm.smart.parking.tool.MappingTool.getTyped

/**
 * SmartParking - mba.vm.smart.parking.data.handler
 * @description TODO: coming soon.
 * @author VictorModi
 * @email victormodi@outlook.com
 * @date 2024/6/20 上午9:14
 */
object CarHandler : BaseDataHandler() {
    override val readPermission: Array<UserType> = arrayOf(UserType.SUPER_ADMIN, UserType.PARKING_ADMIN)
    override val writePermission: Array<UserType> = arrayOf(UserType.SUPER_ADMIN)
    private val queries: Car_informationQueries = getDatabase().car_informationQueries

    override fun select(data: Map<String, Any>?): DataResult {
        if (data.isNullOrEmpty()) return DataResult.createSuccess(queries.getCars().executeAsList())
        val isCount = data.getTyped<Boolean, String, Any>("count") ?: false
        if (isCount) {
            return DataResult.createSuccessByCount(
                queries.getDataCountByFilter(
                    licensePlate = data["license_plate"] as? String,
                    ownerName = data["owner_name"] as? String,
                    contactNumber = data["contact_number"] as? String,
                    brand = data["brand"] as? String,
                    model = data["model"] as? String,
                    color = data["color"] as? String,
                ).executeAsOne()
            )
        }
        return DataResult.createSuccess(
            queries.getDataByFilter(
            licensePlate = data["license_plate"] as? String,
            ownerName = data["owner_name"] as? String,
            contactNumber = data["contact_number"] as? String,
            brand = data["brand"] as? String,
            model = data["model"] as? String,
            color = data["color"] as? String,
        ).executeAsList())
    }

    override fun insert(data: Map<String, Any>?): DataResult {
        if (data.isNullOrEmpty()) return DataResult.createFailure("Car information need data to insert")
        println(data)
        queries.addCar(
            licensePlate = data["license_plate"] as? String,
            ownerName = data["owner_name"] as? String,
            contactNumber = data["contact_number"] as? String,
            brand = data["brand"] as? String,
            model = data["model"] as? String,
            color = data["color"] as? String,
        )
        return DataResult.createSuccess()
    }

    override fun update(data: Map<String, Any>?): DataResult {
        if (data.isNullOrEmpty()) return DataResult.createFailure("Car information need data to update")
        val targetID: Int = data.getInt("id") ?: run {
            return DataResult.createFailure("Car information needs target id to update")
        }
        queries.updateCarInfo(
            licensePlate = data["license_plate"] as? String,
            ownerName = data["owner_name"] as? String,
            contactNumber = data["contact_number"] as? String,
            brand = data["brand"] as? String,
            model = data["model"] as? String,
            color = data["color"] as? String,
            id = targetID
        )
        return DataResult.createByAffectedRows(queries.getAffectedRows().executeAsOne())
    }

    override fun delete(data: Map<String, Any>?): DataResult {
        if (data.isNullOrEmpty()) return DataResult.createFailure("Car information needed to delete")
        val targetID: Int = data.getInt("id")
            ?: return DataResult.createFailure("Car information needs target id to delete")
        queries.deleteCarByID(targetID)
        return DataResult.createByAffectedRows(queries.getAffectedRows().executeAsOne())
    }
}
