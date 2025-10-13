package center.spike.core.forms.fields

import center.spike.common.forms.fields.FieldAnswer
import center.spike.common.forms.fields.FieldDefinition
import jakarta.enterprise.inject.Produces
import jakarta.inject.Singleton
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

sealed interface FieldValidator<Def: FieldDefinition, Ans: FieldAnswer> {
    fun validate(def: Def, answer: Ans?): List<String>
}

object Validator {
    @Suppress("UNCHECKED_CAST")
    fun getValidator(field: FieldDefinition): FieldValidator<FieldDefinition, FieldAnswer>? =
        when (field) {
            is AverageStopwatchField -> AverageStopwatchFieldValidator
            is CheckmarkField -> CheckmarkFieldValidator
            is DropdownMultiField -> DropdownMultiFieldValidator
            is DropdownSingleField -> DropdownSingleFieldValidator
            is MatrixNumberField -> MatrixNumberFieldValidator
            is NumberField -> NumberFieldValidator
            is ParagraphField -> ParagraphFieldValidator
            is PictureField -> PictureFieldValidator
            is RadioField -> RadioFieldValidator
            is RangeField -> RangeFieldValidator
            is ShortTextField -> ShortTextFieldValidator
            is SketchField -> SketchFieldValidator
            is StopwatchField -> StopwatchFieldValidator
            is CategoryField -> null
            else -> null
        } as FieldValidator<FieldDefinition, FieldAnswer>?
}

val formFieldModule = SerializersModule {
    polymorphic(FieldDefinition::class) {
        subclass(AverageStopwatchField::class)
        subclass(CategoryField::class)
        subclass(CheckmarkField::class)
        subclass(DropdownMultiField::class)
        subclass(DropdownSingleField::class)
        subclass(MatrixNumberField::class)
        subclass(NumberField::class)
        subclass(ParagraphField::class)
        subclass(PictureField::class)
        subclass(RadioField::class)
        subclass(RangeField::class)
        subclass(ShortTextField::class)
        subclass(SketchField::class)
        subclass(StopwatchField::class)
    }
}

val answerModule = SerializersModule {
    polymorphic(FieldAnswer::class) {
        subclass(AverageStopwatchAnswer::class)
        subclass(CheckmarkAnswer::class)
        subclass(DropdownMultiAnswer::class)
        subclass(DropdownSingleAnswer::class)
        subclass(MatrixNumberAnswer::class)
        subclass(NumberAnswer::class)
        subclass(ParagraphAnswer::class)
        subclass(PictureAnswer::class)
        subclass(RadioAnswer::class)
        subclass(RangeAnswer::class)
        subclass(ShortTextAnswer::class)
        subclass(SketchAnswer::class)
        subclass(StopwatchAnswer::class)
    }
}

val combinedModule = SerializersModule {
    include(formFieldModule)
    include(answerModule)
}

@Singleton
class KotlinxJsonProvider {
    @Produces
    fun json(): Json = Json {
        serializersModule = combinedModule
        classDiscriminator = "kind"
        encodeDefaults = true
        ignoreUnknownKeys = false
        prettyPrint = false
    }
}

val fieldJson: Json = KotlinxJsonProvider().json()
