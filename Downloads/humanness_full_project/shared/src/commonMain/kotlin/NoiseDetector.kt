package shared

expect class NoiseDetector {
    suspend fun startTest(): NoiseLevel
    suspend fun getCurrentDecibelLevel(): Int
    fun stopTest()
}
