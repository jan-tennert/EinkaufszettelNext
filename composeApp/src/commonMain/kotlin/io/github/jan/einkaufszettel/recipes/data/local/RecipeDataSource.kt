package io.github.jan.einkaufszettel.recipes.data.local

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import einkaufszettel.GetAllRecipes
import io.github.jan.einkaufszettel.recipes.data.remote.RecipeDto
import io.github.jan.einkaufszettel.root.data.local.db.DatabaseProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface RecipeDataSource {

    fun getAllRecipes(): Flow<List<GetAllRecipes>>

    fun getRecipeById(recipeId: Long): Flow<GetAllRecipes?>

    suspend fun retrieveAllRecipes(): List<GetAllRecipes>

    suspend fun insertRecipe(recipe: RecipeDto)

    suspend fun deleteRecipe(recipeId: Long)

    suspend fun insertAll(recipes: List<RecipeDto>)

    suspend fun deleteAll(ids: List<Long>)

    suspend fun clearRecipes()

}

internal class RecipeDataSourceImpl(
    private val databaseProvider: DatabaseProvider
) : RecipeDataSource {

    private val queries get() = databaseProvider.getDatabase().recipeTableQueries

    override suspend fun deleteRecipe(recipeId: Long) {
        queries.deleteRecipeById(recipeId)
    }

    override fun getAllRecipes(): Flow<List<GetAllRecipes>> {
        return queries.getAllRecipes().asFlow().mapToList(Dispatchers.Default)
    }

    override fun getRecipeById(recipeId: Long): Flow<GetAllRecipes?> = getAllRecipes().map { recipes ->
        recipes.find { it.id == recipeId }
    }

    override suspend fun retrieveAllRecipes(): List<GetAllRecipes> {
        return queries.getAllRecipes().awaitAsList()
    }

    override suspend fun insertAll(recipes: List<RecipeDto>) {
        queries.transaction {
            recipes.forEach { recipe ->
                insertRecipe(recipe)
            }
        }
    }

    override suspend fun insertRecipe(recipe: RecipeDto) {
        queries.insertRecipe(
            id = recipe.id,
            name = recipe.name,
            createdAt = recipe.createdAt,
            creatorId = recipe.creatorId,
            imagePath = recipe.imagePath,
            ingredients = recipe.ingredients,
            steps = recipe.steps,
            isPrivate = if(recipe.private) 1 else 0
        )
    }

    override suspend fun deleteAll(ids: List<Long>) {
        queries.transaction {
            ids.forEach { id ->
                deleteRecipe(id)
            }
        }
    }

    override suspend fun clearRecipes() {
        queries.clearRecipes()
    }

}