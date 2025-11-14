package shared

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class ProductApiClient {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    suspend fun getProducts(limit: Int = 12): List<Product> {
        return try {
            val response: ProductsResponse = client.get("https://dummyjson.com/products?limit=$limit").body()
            response.products
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getProductById(id: Int): Product? {
        return try {
            client.get("https://dummyjson.com/products/$id").body()
        } catch (e: Exception) {
            null
        }
    }
}
