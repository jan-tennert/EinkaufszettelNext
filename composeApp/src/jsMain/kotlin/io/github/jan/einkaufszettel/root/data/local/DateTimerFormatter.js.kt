package io.github.jan.einkaufszettel.root.data.local

import kotlin.time.Instant
import kotlinx.datetime.toJSDate

actual object DateTimerFormatter {
    actual fun format(date: Instant): String {
        return date.toJSDate().toDateString()
    }

}