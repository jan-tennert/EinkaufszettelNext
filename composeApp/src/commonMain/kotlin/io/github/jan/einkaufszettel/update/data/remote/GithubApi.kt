package io.github.jan.einkaufszettel.update.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import net.swiftzer.semver.SemVer


sealed interface GithubApi {

    suspend fun retrieveLatestVersion(): SemVer?

}

internal class GithubApiImpl(
    private val httpClient: HttpClient
): GithubApi {

    override suspend fun retrieveLatestVersion(): SemVer? {
        val version = httpClient.get(API_URL)
            .body<JsonObject>()["tag_name"]?.jsonPrimitive?.content
        return version?.let { SemVer.parse(it) }
    }

    companion object {
        const val API_URL = "https://api.github.com/repos/jan-tennert/EinkaufszettelNext/releases/latest"
    }

}