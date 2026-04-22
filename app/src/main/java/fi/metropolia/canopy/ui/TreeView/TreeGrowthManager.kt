package fi.metropolia.canopy.ui.TreeView

enum class TreeStage {
    SEED, SPROUT, SMALL_TREE, BIG_TREE, FULL_TREE, DESTROYED
}

object TreeGrowthManager {
    fun getTreeStage(totalEmissionKg: Double): TreeStage {
        return when {
            totalEmissionKg < 1000.0  -> TreeStage.FULL_TREE   // Erinomainen
            totalEmissionKg < 2500.0  -> TreeStage.BIG_TREE    // Hyvä
            totalEmissionKg < 5000.0  -> TreeStage.SMALL_TREE  // Kohtuullinen
            totalEmissionKg < 8000.0  -> TreeStage.SPROUT     // Melko korkea
            totalEmissionKg < 12000.0 -> TreeStage.SEED       // Baseline
            else -> TreeStage.DESTROYED                       // Kriittinen (Yli keskiarvon)
        }
    }
}
