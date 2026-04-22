package fi.metropolia.canopy.ui.TreeView

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import fi.metropolia.canopy.R
import fi.metropolia.canopy.domain.model.TrackingState
import fi.metropolia.canopy.ui.overview.OverviewColors
import fi.metropolia.canopy.utils.viewModelFactories.TripViewModelFactory
import fi.metropolia.canopy.viewmodels.TripViewModel
import java.util.Locale as JavaLocale

@Composable
fun TreeScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: TripViewModel = viewModel(
        factory = TripViewModelFactory(context)
    )

    val emissions by viewModel.emissions.collectAsStateWithLifecycle()
    val totalEmissionsKg = emissions.values.sum() / 1000.0

    LaunchedEffect(Unit) {
        viewModel.loadEmissions()
    }

    var showGallery by remember { mutableStateOf(false) }
    
    if (showGallery) {
        AllTreesGallery(onBack = { showGallery = false })
    } else {
        MainTreeContent(
            totalEmissionsKg = totalEmissionsKg,
            onShowGallery = { showGallery = true }
        )
    }
}

@Composable
fun MainTreeContent(totalEmissionsKg: Double, onShowGallery: () -> Unit) {
    val currentTrackingEmission = TrackingState.totalEmissionKg
    val displayEmission = totalEmissionsKg + currentTrackingEmission
    val stage = TreeGrowthManager.getTreeStage(displayEmission)

    val imageRes = getTreeImage(stage)


    val colorFilter = when (stage) {
        TreeStage.SEED -> ColorFilter.tint(Color(0xFF795548), blendMode = BlendMode.Modulate)
        else -> null
    }

    //tausta muuttuu ruskeaksi kun  on paljon päästöi
    val backgroundColor = when (stage) {
        TreeStage.DESTROYED -> Color(0xFFBCAAA4)
        TreeStage.SEED -> Color(0xFFD7CCC8)
        else -> OverviewColors.BgGreen
    }

    val size by animateDpAsState(
        targetValue = when (stage) {
            TreeStage.SEED -> 140.dp
            TreeStage.SPROUT -> 200.dp
            TreeStage.SMALL_TREE -> 280.dp
            TreeStage.BIG_TREE -> 340.dp
            TreeStage.FULL_TREE -> 420.dp
            TreeStage.DESTROYED -> 420.dp // Kuihtunut puu on suuri, mutta kuollut, koska päästöt on suuret
        },
        label = "tree_size"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "My Tree",
                style = MaterialTheme.typography.headlineLarge,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            TextButton(
                onClick = onShowGallery,
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Text("View All", color = Color.DarkGray, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Tree",
            modifier = Modifier
                .fillMaxWidth()
                .height(450.dp)
                .size(size),
            colorFilter = colorFilter
        )

        Spacer(modifier = Modifier.weight(1f))

        Surface(
            color = Color.White.copy(alpha = 0.7f),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = getStageText(stage),
                    style = MaterialTheme.typography.headlineSmall,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Yearly Emissions",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
                
                Text(
                    text = "%.2f kg CO₂".format(displayEmission),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
        
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun AllTreesGallery(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OverviewColors.BgGreen)
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text("← Back", color = Color.Black, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Tree Stages",
                style = MaterialTheme.typography.headlineMedium,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        val stagesList = remember { TreeStage.entries.toList() }
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(items = stagesList) { stage ->
                TreeStageCard(stage)
            }
        }
    }
}

@Composable
fun TreeStageCard(stage: TreeStage) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = getTreeImage(stage)),
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                colorFilter = if (stage == TreeStage.SEED) ColorFilter.tint(Color(0xFF795548), blendMode = BlendMode.Modulate) else null
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stage.name.replace("_", " ").lowercase(JavaLocale.getDefault())
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(JavaLocale.getDefault()) else it.toString() },
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = getEmissionRange(stage),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

fun getEmissionRange(stage: TreeStage): String {
    return when (stage) {
        TreeStage.FULL_TREE -> "< 1000 kg CO₂"
        TreeStage.BIG_TREE -> "1000 - 2500 kg CO₂"
        TreeStage.SMALL_TREE -> "2500 - 5000 kg CO₂"
        TreeStage.SPROUT -> "5000 - 8000 kg CO₂"
        TreeStage.SEED -> "8000 - 12000 kg CO₂"
        TreeStage.DESTROYED -> "> 12000 kg CO₂"
    }
}

fun getStageText(stage: TreeStage): String {
    return when (stage) {
        TreeStage.DESTROYED -> "Withered 🌫️"
        TreeStage.SEED -> "Baseline 🌱"
        TreeStage.SPROUT -> "Growing 🌿"
        TreeStage.SMALL_TREE -> "Doing Great 🌳"
        TreeStage.BIG_TREE -> "Very Good 🌳✨"
        TreeStage.FULL_TREE -> "Perfect 🌍💚"
    }
}

fun getTreeImage(stage: TreeStage): Int {
    return when (stage) {
        TreeStage.FULL_TREE -> R.drawable.tree4     // Terve puu
        TreeStage.BIG_TREE -> R.drawable.tree4      // Melkein täysi
        TreeStage.SMALL_TREE -> R.drawable.tree3    // Keskikokoinen
        TreeStage.SPROUT -> R.drawable.tree2         // Pieni taimi
        TreeStage.SEED -> R.drawable.tree1          // Alkuvaihe
        TreeStage.DESTROYED -> R.drawable.tree5     // Kuihtunut (ilman lehtiä)
    }
}

@Preview(showBackground = true)
@Composable
fun TreeScreenPreview() {
    val navController = rememberNavController()
    MaterialTheme {
        TreeScreen(navController = navController)
    }
}

@Preview(showBackground = true)
@Composable
fun GalleryPreview() {
    MaterialTheme {
        AllTreesGallery(onBack = {})
    }
}
