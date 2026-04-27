package fi.metropolia.canopy.ui.treeview

enum class TreeStage {
    SEED, SPROUT, SMALL_TREE, BIG_TREE, FULL_TREE, DESTROYED
}

object TreeGrowthManager {

    fun getTreeStage(monthlyEmissionKg: Double): TreeStage {
        return when {
            monthlyEmissionKg < 30.0  -> TreeStage.FULL_TREE   // Erinomainen (Kävely, pyöräily, juna, metro)
            monthlyEmissionKg < 80.0  -> TreeStage.BIG_TREE    // Hyvä
            monthlyEmissionKg < 150.0 -> TreeStage.SMALL_TREE  // Kohtuullinen
            monthlyEmissionKg < 300.0 -> TreeStage.SPROUT      // Melko korkea
            monthlyEmissionKg < 1000.0 -> TreeStage.DESTROYED        // Korkea (Paljon autoilua)
            else -> TreeStage.DESTROYED                       // Erittäin korkea (Kriittinen)
        }
    }
}
