package shared

object RecordingValidator {
    private const val MIN_DURATION_MS = 10_000L // 10 seconds
    private const val MAX_DURATION_MS = 20_000L // 20 seconds

    fun isValidDuration(durationMs: Long): Pair<Boolean, String?> {
        return when {
            durationMs < MIN_DURATION_MS -> {
                Pair(false, "Recording too short (min 10 s).")
            }
            durationMs > MAX_DURATION_MS -> {
                Pair(false, "Recording too long (max 20 s).")
            }
            else -> Pair(true, null)
        }
    }

    fun getDurationInSeconds(durationMs: Long): Int {
        return (durationMs / 1000).toInt()
    }
}
