package io.github.jan.einkaufszettel.root.data.local.db.adapters

import app.cash.sqldelight.ColumnAdapter

data object ListToStringAdapter: ColumnAdapter<List<String>, String> {

    override fun decode(databaseValue: String): List<String> {
        return databaseValue.split(",")
    }

    override fun encode(value: List<String>): String {
        return value.joinToString(",")
    }

}