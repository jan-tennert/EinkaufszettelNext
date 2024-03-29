package io.github.jan.einkaufszettel.root.data.local

import kotlinx.datetime.Instant

expect object DateTimerFormatter {

    fun format(date: Instant): String

}