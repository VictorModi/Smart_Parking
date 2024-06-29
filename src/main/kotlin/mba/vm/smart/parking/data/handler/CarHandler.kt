package mba.vm.smart.parking.data.handler

import mba.vm.smart.parking.Car_informationQueries
import mba.vm.smart.parking.DataSourceProvider.getDatabase
import mba.vm.smart.parking.UserType
import mba.vm.smart.parking.data.DataResult
import mba.vm.smart.parking.tool.MappingTool.getInt
import mba.vm.smart.parking.tool.MappingTool.getTyped

/**
 * SmartParking - mba.vm.smart.parking.data.handler
 * @description https://github.com/VictorModi/Smart_Parking
 * @author VictorModi
 * @email victormodi@outlook.com
 * @date 2024/6/20 上午9:14
 */
object CarHandler : BaseDataHandler() {
    override val readPermission: Array<UserType> = arrayOf(
        UserType.CHARGE_ADMIN,
        UserType.SYSTEM_ADMIN,
        UserType.PARKING_ADMIN,
        UserType.SECURITY_ADMIN,
        UserType.CUSTOMER_SERVICE_ADMIN
    )
    override val writePermission: Array<UserType> = arrayOf(
        UserType.SUPER_ADMIN,
        UserType.PARKING_ADMIN
    )
    private val queries: Car_informationQueries = getDatabase().car_informationQueries

    override fun select(data: Map<String, Any>?): DataResult {
        if (data.isNullOrEmpty()) return DataResult.createSuccess(queries.getCars().executeAsList())
        val isCount = data.getTyped<Boolean, String, Any>("count") ?: false
        val isLimited = data.getInt("limit") != null && data.getInt("offset") != null;
        return when (Pair(isCount, isLimited)) {
            Pair(true, true) -> DataResult.createSuccessByCount(
                queries.getLimitedCarsCount(
                    licensePlate = data.getTyped("license_plate"),
                    ownerName = data.getTyped("owner_name"),
                    contactNumber = data.getTyped("contact_number"),
                    brand = data.getTyped("brand"),
                    model = data.getTyped("model"),
                    color = data.getTyped("color"),
                    limit = data.getInt("limit")!!.toLong(),
                    offset = data.getInt("offset")!!.toLong()
                ).executeAsOne()
            )
            Pair(false, true) -> DataResult.createSuccess(
                queries.getLimitedCars(
                    licensePlate = data.getTyped("license_plate"),
                    ownerName = data.getTyped("owner_name"),
                    contactNumber = data.getTyped("contact_number"),
                    brand = data.getTyped("brand"),
                    model = data.getTyped("model"),
                    color = data.getTyped("color"),
                    limit = data.getInt("limit")!!.toLong(),
                    offset = data.getInt("offset")!!.toLong()
                ).executeAsList()
            )
            Pair(true, false) -> DataResult.createSuccessByCount(
                queries.getDataCountByFilter(
                    licensePlate = data.getTyped("license_plate"),
                    ownerName = data.getTyped("owner_name"),
                    contactNumber = data.getTyped("contact_number"),
                    brand = data.getTyped("brand"),
                    model = data.getTyped("model"),
                    color = data.getTyped("color"),
                ).executeAsOne()
            )
            Pair(false, false) -> DataResult.createSuccess(
                queries.getDataByFilter(
                    licensePlate = data.getTyped("license_plate"),
                    ownerName = data.getTyped("owner_name"),
                    contactNumber = data.getTyped("contact_number"),
                    brand = data.getTyped("brand"),
                    model = data.getTyped("model"),
                    color = data.getTyped("color")
                ).executeAsList()
            )

            else -> DataResult.createFailure("WTF??")
        }
    }

    override fun insert(data: Map<String, Any>?): DataResult {
        if (data.isNullOrEmpty()) return DataResult.createFailure("Car information need data to insert")
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
