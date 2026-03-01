package fi.metropolia.canopy.ui.overview

import androidx.compose.ui.graphics.Color

object OverviewColors {
    val BgGreen = Color(0xFF9BB59D)
    val CardWhite = Color(0xFFF7F7F7)

    val Slice1 = Color(0xFFB7F7D0)
    val Slice2 = Color(0xFF86D8A8)
    val Slice3 = Color(0xFF5E9B73)
    val Slice4 = Color(0xFF3B3A2E)
}

object OverviewFakeData {

    fun myAverage(): CompareTarget {
        val slices = listOf(
            EmissionSlice(
                "Bus",
                3.65,
                OverviewColors.Slice2,
                iconKey = "bus"
            ),
            EmissionSlice(
                "Petrol",
                0.44,
                OverviewColors.Slice4,
                iconKey = "car"
            ),
            EmissionSlice(
                "Train",
                1.60,
                OverviewColors.Slice3,
                iconKey = "train"
            ),
            EmissionSlice(
                "Metro",
                2.00,
                OverviewColors.Slice1,
                iconKey = "metro"
            ),
        )
        val total = slices.sumOf { it.value }
        return CompareTarget(
            id = "me",
            name = "Minä",
            breakdown = EmissionBreakdown(total, slices)
        )
    }

    fun campuses(): List<CompareTarget> {
        fun campus(
            id: String,
            name: String,
            bus: Double,
            petrol: Double,
            train: Double,
            metro: Double
        ): CompareTarget {
            val slices = listOf(
                EmissionSlice(
                    "Bus",
                    bus,
                    OverviewColors.Slice2,
                    "bus"
                ),
                EmissionSlice(
                    "Petrol",
                    petrol,
                    OverviewColors.Slice4,
                    "car"
                ),
                EmissionSlice(
                    "Train",
                    train,
                    OverviewColors.Slice3,
                    "train"
                ),
                EmissionSlice(
                    "Metro",
                    metro,
                    OverviewColors.Slice1,
                    "metro"
                ),
            )
            return CompareTarget(
                id = id,
                name = name,
                breakdown = EmissionBreakdown(
                    slices.sumOf { it.value },
                    slices
                )
            )
        }

        return listOf(
            campus("campus_myllypuro", "Myllypuro", 2.0, 0.6, 1.2, 1.4),
            campus("campus_arabia", "Arabia", 1.7, 0.5, 1.3, 1.1),
            campus("campus_karamalmi", "Karamalmi", 1.8, 0.7, 1.0, 1.3),
            campus("campus_myyrmaki", "Myyrmäki", 1.9, 0.8, 0.9, 1.3),
        )
    }

    fun roles(): List<CompareTarget> {
        fun role(id: String, name: String, bus: Double, petrol: Double, train: Double, metro: Double): CompareTarget {
            val slices = listOf(
                EmissionSlice(
                    "Bus",
                    bus,
                    OverviewColors.Slice2,
                    "bus"
                ),
                EmissionSlice(
                    "Petrol",
                    petrol,
                    OverviewColors.Slice4,
                    "car"
                ),
                EmissionSlice(
                    "Train",
                    train,
                    OverviewColors.Slice3,
                    "train"
                ),
                EmissionSlice(
                    "Metro",
                    metro,
                    OverviewColors.Slice1,
                    "metro"
                ),
            )
            return CompareTarget(
                id = id,
                name = name,
                breakdown = EmissionBreakdown(
                    slices.sumOf { it.value },
                    slices
                )
            )
        }

        return listOf(
            role("role_students", "Opiskelijat", 2.4, 0.3, 1.7, 1.5),
            role("role_staff", "Henkilökunta", 1.6, 1.1, 0.9, 1.0),
        )
    }
}