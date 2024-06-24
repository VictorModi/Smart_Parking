package mba.vm.smart.parking.data.adapter

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * SmartParking - mba.vm.smart.parking.data.adapter
 * @description TODO: coming soon.
 * @author VictorModi
 * @email victormodi@outlook.com
 * @date 2024/6/6 上午9:38
 */
class LocalDateTimeAdapter : TypeAdapter<LocalDateTime>() {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    override fun write(out: JsonWriter, value: LocalDateTime) {
        out.value(value.format(formatter))
    }

    override fun read(input: JsonReader): LocalDateTime {
        return LocalDateTime.parse(input.nextString(), formatter)
    }
}
