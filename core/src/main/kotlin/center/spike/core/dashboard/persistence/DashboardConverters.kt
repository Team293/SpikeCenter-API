package center.spike.core.dashboard.persistence

import center.spike.common.dashboard.FilterType
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class FilterTypeListConverter : AttributeConverter<MutableList<FilterType>, String> {
    override fun convertToDatabaseColumn(attribute: MutableList<FilterType>?): String? =
        attribute?.joinToString(",") { it.name }

    override fun convertToEntityAttribute(dbData: String?): MutableList<FilterType> =
        dbData?.split(",")?.mapNotNull { FilterType.entries.find { ft -> ft.name == it } }?.toMutableList() ?: mutableListOf()
}