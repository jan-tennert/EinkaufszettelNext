package io.github.jan.einkaufszettel.data.local

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter

actual object DateTimerFormatter {
    actual fun format(date: Instant): String {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
        return formatter.format(date.toLocalDateTime(TimeZone.currentSystemDefault()).toJavaLocalDateTime())
    }

}