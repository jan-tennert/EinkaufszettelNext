package io.github.jan.einkaufszettel.data.local

import kotlinx.datetime.Instant
import kotlinx.datetime.toJSDate

actual object DateTimerFormatter {
    actual fun format(date: Instant): String {
        return date.toJSDate().toDateString()
    }

}