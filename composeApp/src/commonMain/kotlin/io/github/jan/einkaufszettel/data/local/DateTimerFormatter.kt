package io.github.jan.einkaufszettel.data.local

import kotlinx.datetime.Instant

expect object DateTimerFormatter {

    fun format(date: Instant): String

}