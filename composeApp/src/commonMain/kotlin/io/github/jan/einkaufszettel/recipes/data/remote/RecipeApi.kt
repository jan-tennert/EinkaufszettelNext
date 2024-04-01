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
        name: String?,
        imagePath: String?,
        ingredients: List<String>?,
        steps: String?,
    ): RecipeDto

    suspend fun retrieveRecipes(): List<RecipeDto>

    suspend fun deleteRecipe(id: Long)

    suspend fun uploadImage(imagePath: String, image: ByteArray)

    suspend fun deleteImage(imagePath: String)

    companion object {
        const val BUCKET_ID = "recipes"
        const val TABLE_NAME = "recipes"
    }

}

internal class RecipeApiImpl(
    postgrest: Postgrest,
    storage: Storage,
) : RecipeApi {

    private val table = postgrest[RecipeApi.TABLE_NAME]
    private val images = storage[RecipeApi.BUCKET_ID]

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
        name: String?,
        imagePath: String?,
        ingredients: List<String>?,
        steps: String?,
    ): RecipeDto {
        return table.update({
            name?.let { RecipeDto::name setTo it }
            imagePath?.let { RecipeDto::imagePath setTo it }
            ingredients?.let { RecipeDto::ingredients setTo it }
            steps?.let { RecipeDto::steps setTo it }
        }) {
            select()
            filter {
                RecipeDto::id eq id
            }
        }.decodeSingle()
    }

    override suspend fun uploadImage(imagePath: String, image: ByteArray) {
        images.upload(imagePath, image)
    }

    override suspend fun deleteImage(imagePath: String) {
        images.delete(imagePath)
    }

}