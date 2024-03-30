package io.github.jan.einkaufszettel.recipes.data.remote

import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class RecipeDto(
    val id: Long,
    val name: String,
    val createdAt: Instant,
    val creatorId: String,
    val imagePath: String?,
    val ingredients: List<String>,
    val steps: String,
    val private: Boolean,
)

@Serializable
data class RecipeCreationDto(
    val name: String,
    val creatorId: String,
    val imagePath: String?,
    val ingredients: List<String>,
    val steps: String,
    val private: Boolean,
)

interface RecipeApi {

    suspend fun createRecipe(
        name: String,
        creatorId: String,
        imagePath: String?,
        ingredients: List<String>,
        steps: String,
        private: Boolean,
    ): RecipeDto

    suspend fun editRecipe(
        id: Long,
        name: String,
        imagePath: String?,
        ingredients: List<String>,
        steps: String,
        private: Boolean,
    )

    suspend fun retrieveRecipes(): List<RecipeDto>

    suspend fun deleteRecipe(id: Long)

    suspend fun uploadImage(imagePath: String, image: ByteArray)

    suspend fun deleteImage(imagePath: String)

}

internal class RecipeApiImpl(
    postgrest: Postgrest,
    storage: Storage,
) : RecipeApi {

    private val table = postgrest["recipes"]
    private val images = storage["recipes"]

    override suspend fun retrieveRecipes(): List<RecipeDto> {
        return table.select().decodeList()
    }

    override suspend fun createRecipe(
        name: String,
        creatorId: String,
        imagePath: String?,
        ingredients: List<String>,
        steps: String,
        private: Boolean
    ): RecipeDto {
        return table.insert(
            RecipeCreationDto(
                name = name,
                creatorId = creatorId,
                imagePath = imagePath,
                ingredients = ingredients,
                steps = steps,
                private = private
            )
        ) {
            select()
        }.decodeSingle()
    }

    override suspend fun deleteRecipe(id: Long) {
        table.delete {
            filter {
                RecipeDto::id eq id
            }
        }
    }

    override suspend fun editRecipe(
        id: Long,
        name: String,
        imagePath: String?,
        ingredients: List<String>,
        steps: String,
        private: Boolean
    ) {
        table.update({
            RecipeDto::name setTo name
            RecipeDto::imagePath setTo imagePath
            RecipeDto::ingredients setTo ingredients
            RecipeDto::steps setTo steps
            RecipeDto::private setTo private
        }) {
            filter {
                RecipeDto::id eq id
            }
        }
    }

    override suspend fun uploadImage(imagePath: String, image: ByteArray) {
        images.upload(imagePath, image)
    }

    override suspend fun deleteImage(imagePath: String) {
        images.delete(imagePath)
    }

}