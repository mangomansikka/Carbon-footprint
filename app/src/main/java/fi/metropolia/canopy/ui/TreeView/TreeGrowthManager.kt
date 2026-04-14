package fi.metropolia.canopy.ui.TreeView

enum class TreeStage {
    SEED, SPROUT, SMALL_TREE, BIG_TREE,  DESTROYED
}

object TreeGrowthManager {
    fun getTreeStage(totalEmissionKg: Double): TreeStage {
        return when {

            totalEmissionKg < 40.0 -> TreeStage.BIG_TREE    // Hyvä
            totalEmissionKg < 70.0 -> TreeStage.SMALL_TREE  // Keskiverto
            totalEmissionKg < 100.0 -> TreeStage.SPROUT     // Melko korkea
            totalEmissionKg < 150.0 -> TreeStage.SEED       // Oletus (Siemen)
            else -> TreeStage.DESTROYED                     // Kriittinen (Ruskea, kuollut puu)
        }
    }
}
