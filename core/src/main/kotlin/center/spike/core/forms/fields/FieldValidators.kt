package center.spike.core.forms.fields

object AverageStopwatchFieldValidator : FieldValidator<AverageStopwatchField, AverageStopwatchAnswer> {
    override fun validate(def: AverageStopwatchField, answer: AverageStopwatchAnswer?): List<String> {
        val errors = mutableListOf<String>()
        if (def.required && answer == null) {
            errors.add("This field is required.")
            return errors
        }
        if (answer != null) {
            if (answer.times.any { it < 0 }) {
                errors.add("Invalid stopwatch times.")
            }
        }
        return errors
    }
}

object CheckmarkFieldValidator : FieldValidator<CheckmarkField, CheckmarkAnswer> {
    override fun validate(def: CheckmarkField, answer: CheckmarkAnswer?): List<String> {
        val errors = mutableListOf<String>()
        if (def.required && answer == null) {
            errors.add("This field is required.")
        }
        return errors
    }
}

object DropdownMultiFieldValidator : FieldValidator<DropdownMultiField, DropdownMultiAnswer> {
    override fun validate(def: DropdownMultiField, answer: DropdownMultiAnswer?): List<String> {
        val errors = mutableListOf<String>()
        if (def.required && answer == null) {
            errors.add("This field is required.")
            return errors
        }
        if (answer != null) {
            if (answer.value.any { it !in def.options }) {
                errors.add("Invalid selection.")
            }
            def.maxSelections?.let { max ->
                if (answer.value.size > max) {
                    errors.add("You can select up to $max options.")
                }
            }
        }
        return errors
    }
}

object DropdownSingleFieldValidator : FieldValidator<DropdownSingleField, DropdownSingleAnswer> {
    override fun validate(def: DropdownSingleField, answer: DropdownSingleAnswer?): List<String> {
        val errors = mutableListOf<String>()
        if (def.required && answer == null) {
            errors.add("This field is required.")
            return errors
        }
        if (answer != null && answer.value !in def.options) {
            errors.add("Invalid selection.")
        }
        return errors
    }
}

object MatrixNumberFieldValidator : FieldValidator<MatrixNumberField, MatrixNumberAnswer> {
    override fun validate(def: MatrixNumberField, answer: MatrixNumberAnswer?): List<String> {
        val errors = mutableListOf<String>()
        if (def.required && answer == null) {
            errors.add("This field is required.")
            return errors
        }
        if (answer != null) {
            val invalidRows = answer.value.keys - def.rows.map { it.id }.toSet()
            if (invalidRows.isNotEmpty()) {
                errors.add("Invalid rows: ${invalidRows.joinToString(", ")}")
            }
            answer.value.forEach { (rowId, number) ->
                val rowDef = def.rows.find { it.id == rowId }
                if (rowDef != null) {
                    rowDef.min?.let { min ->
                        if (number < min) {
                            errors.add("Value for row '$rowId' must be at least $min.")
                        }
                    }
                    rowDef.max?.let { max ->
                        if (number > max) {
                            errors.add("Value for row '$rowId' must be at most $max.")
                        }
                    }
                }
            }
        }
        return errors
    }
}

object NumberFieldValidator : FieldValidator<NumberField, NumberAnswer> {
    override fun validate(def: NumberField, answer: NumberAnswer?): List<String> {
        val errors = mutableListOf<String>()
        if (def.required && answer == null) {
            errors.add("This field is required.")
            return errors
        }
        if (answer != null) {
            def.min?.let { min ->
                if (answer.value < min) {
                    errors.add("Value must be at least $min.")
                }
            }
            def.max?.let { max ->
                if (answer.value > max) {
                    errors.add("Value must be at most $max.")
                }
            }
        }
        return errors
    }
}

object ParagraphFieldValidator : FieldValidator<ParagraphField, ParagraphAnswer> {
    override fun validate(def: ParagraphField, answer: ParagraphAnswer?): List<String> {
        val errors = mutableListOf<String>()
        if (def.required && answer == null) {
            errors.add("This field is required.")
            return errors
        }
        if (answer != null) {
            def.characterLimit ?.let { limit ->
                if (answer.value.length > limit) {
                    errors.add("Response exceeds character limit of $limit.")
                }
            }
        }
        return errors
    }
}

object PictureFieldValidator : FieldValidator<PictureField, PictureAnswer> {
    override fun validate(def: PictureField, answer: PictureAnswer?): List<String> {
        val errors = mutableListOf<String>()
        if (def.required && answer == null) {
            errors.add("This field is required.")
            return errors
        }
        if (answer != null) {
            def.min?.let { min ->
                if (answer.value.size < min) {
                    errors.add("At least $min pictures are required.")
                }
            }
            def.max?.let { max ->
                if (answer.value.size > max) {
                    errors.add("At most $max pictures are allowed.")
                }
            }
        }
        return errors
    }
}

object RadioFieldValidator : FieldValidator<RadioField, RadioAnswer> {
    override fun validate(def: RadioField, answer: RadioAnswer?): List<String> {
        val errors = mutableListOf<String>()
        if (def.required && answer == null) {
            errors.add("This field is required.")
            return errors
        }
        if (answer != null && answer.value !in def.options) {
            errors.add("Invalid selection.")
        }
        return errors
    }
}

object RangeFieldValidator : FieldValidator<RangeField, RangeAnswer> {
    override fun validate(def: RangeField, answer: RangeAnswer?): List<String> {
        val errors = mutableListOf<String>()
        if (def.required && answer == null) {
            errors.add("This field is required.")
            return errors
        }
        if (answer != null) {
            if (answer.value < def.min || answer.value > def.max) {
                errors.add("Value must be between ${def.min} and ${def.max}.")
            }
        }
        return errors
    }
}

object ShortTextFieldValidator : FieldValidator<ShortTextField, ShortTextAnswer> {
    override fun validate(def: ShortTextField, answer: ShortTextAnswer?): List<String> {
        val errors = mutableListOf<String>()
        if (def.required && answer == null) {
            errors.add("This field is required.")
            return errors
        }
        if (answer != null) {
            def.characterLimit ?.let { limit ->
                if (answer.value.length > limit) {
                    errors.add("Response exceeds character limit of $limit.")
                }
            }
        }
        return errors
    }
}

object SketchFieldValidator : FieldValidator<SketchField, SketchAnswer> {
    override fun validate(def: SketchField, answer: SketchAnswer?): List<String> {
        val errors = mutableListOf<String>()
        if (def.required && answer == null) {
            errors.add("This field is required.")
        }
        return errors
    }
}

object StopwatchFieldValidator : FieldValidator<StopwatchField, StopwatchAnswer> {
    override fun validate(def: StopwatchField, answer: StopwatchAnswer?): List<String> {
        val errors = mutableListOf<String>()
        if (def.required && answer == null) {
            errors.add("This field is required.")
            return errors
        }
        if (answer != null) {
            if (answer.time < 0) { errors.add("Time cannot be negative.") }
        }
        return errors
    }
}