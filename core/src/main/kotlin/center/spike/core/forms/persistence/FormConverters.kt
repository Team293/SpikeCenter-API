package center.spike.core.forms.persistence

import center.spike.common.forms.fields.FieldAnswer
import center.spike.common.forms.fields.FieldDefinition
import center.spike.core.forms.fields.fieldJson
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant
import java.time.Instant as JInstant
import kotlin.time.Instant as KInstant

@Converter(autoApply = true)
class FieldDefinitionListConverter :
    AttributeConverter<List<FieldDefinition>?, String?> {

    override fun convertToDatabaseColumn(attribute: List<FieldDefinition>?): String? =
        attribute?.let { fieldJson.encodeToString(it) }

    override fun convertToEntityAttribute(dbData: String?): List<FieldDefinition>? =
        when {
            dbData.isNullOrBlank() -> emptyList()
            else -> fieldJson.decodeFromString<List<FieldDefinition>>(dbData)
        }
}

@Converter(autoApply = true)
class FieldAnswerListConverter :
    AttributeConverter<List<FieldAnswer>?, String?> {

    override fun convertToDatabaseColumn(attribute: List<FieldAnswer>?): String? =
        attribute?.let { fieldJson.encodeToString(it) }

    override fun convertToEntityAttribute(dbData: String?): List<FieldAnswer>? =
        when {
            dbData.isNullOrBlank() -> emptyList()
            else -> fieldJson.decodeFromString<List<FieldAnswer>>(dbData)
        }
}

@OptIn(ExperimentalTime::class)
@Converter(autoApply = true)
class KotlinInstantConverter : AttributeConverter<KInstant, JInstant> {
    override fun convertToDatabaseColumn(attribute: KInstant?): JInstant? =
        attribute?.toJavaInstant()
    override fun convertToEntityAttribute(dbData: JInstant?): KInstant? =
        dbData?.toKotlinInstant()
}
