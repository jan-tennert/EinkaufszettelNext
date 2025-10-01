package io.github.jan.einkaufszettel.root.data.local

import kotlin.time.Instant

expect object DateTimerFormatter {

    fun format(date: Instant): String

}