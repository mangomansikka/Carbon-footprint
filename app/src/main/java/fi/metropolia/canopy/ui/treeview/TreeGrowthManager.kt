package fi.metropolia.canopy.ui.treeview

/**
 * TreeGrowthManager object for managing tree growth based on emissions
 */
enum class TreeStage {
    SEED,           // 0 - 100
    SPROUT,         // 100 - 220
    SMALL_TREE,     // 220 - 330
    MEDIUM_TREE,    // 330 - 440
    BIG_TREE,       // 440 - 550
    FULL_TREE,      // 550 - 660
    SICK,           // 660 - 800
    DEAD            // 800+
}

object TreeGrowthManager {

    fun getTreeStage(monthlyEmissionKg: Double): TreeStage {
        return when {
            monthlyEmissionKg < 100.0 -> TreeStage.SEED
            monthlyEmissionKg < 220.0 -> TreeStage.SPROUT
            monthlyEmissionKg < 330.0 -> TreeStage.SMALL_TREE
            monthlyEmissionKg < 440.0 -> TreeStage.MEDIUM_TREE
            monthlyEmissionKg < 550.0 -> TreeStage.BIG_TREE
            monthlyEmissionKg < 660.0 -> TreeStage.FULL_TREE
            monthlyEmissionKg < 800.0 -> TreeStage.SICK
            else -> TreeStage.DEAD
        }
    }
}
