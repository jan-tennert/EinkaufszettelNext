package io.github.jan.einkaufszettel.data.local.db.adapters

import app.cash.sqldelight.ColumnAdapter
import kotlinx.datetime.Instant

data object InstantAdapter: ColumnAdapter<Instant, Long> {

    override fun decode(databaseValue: Long): Instant {
        return Instant.fromEpochMilliseconds(databaseValue)
    }

    override fun encode(value: Instant): Long {
        return value.toEpochMilliseconds()
    }

}