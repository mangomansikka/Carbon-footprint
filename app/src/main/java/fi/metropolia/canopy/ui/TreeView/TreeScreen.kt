package fi.metropolia.canopy.ui.treeview

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import fi.metropolia.canopy.R
import fi.metropolia.canopy.domain.model.TrackingState
import fi.metropolia.canopy.ui.overview.OverviewColors
import fi.metropolia.canopy.utils.viewModelFactories.TripViewModelFactory
import fi.metropolia.canopy.viewmodels.TripViewModel
import java.util.Locale as JavaLocale

@Composable
fun TreeScreen() {
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

    val backgroundColor = when (stage) {
        TreeStage.DEAD -> Color(0xFFBCAAA4)
        TreeStage.SICK -> Color(0xFFD7CCC8)
        else -> OverviewColors.BgGreen
    }

    val size by animateDpAsState(
        targetValue = when (stage) {
            TreeStage.SEED -> 140.dp
            TreeStage.SPROUT -> 200.dp
            TreeStage.SMALL_TREE -> 260.dp
            TreeStage.MEDIUM_TREE -> 320.dp
            TreeStage.BIG_TREE -> 380.dp
            TreeStage.FULL_TREE -> 420.dp
            TreeStage.SICK -> 420.dp
            TreeStage.DEAD -> 420.dp
        },
        label = "tree_size"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.my_tree),
                style = MaterialTheme.typography.headlineLarge,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            TextButton(
                onClick = onShowGallery,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .background(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(50)
                    )
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.Black
                )
            ) {
                Text(
                    stringResource(R.string.view_all),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Tree",
            modifier = Modifier
                .size(size),
            colorFilter = colorFilter
        )

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            color = Color.White.copy(alpha = 0.7f),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
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
                    text = stringResource(R.string.monthly_emissions),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
                
                Text(
                    text = stringResource(R.string.kg_co2_display, displayEmission),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(0.5f))
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
            TextButton(
                onClick = onBack,
                modifier = Modifier
                    .background(
                        color = Color.White.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(50)
                    )
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.Black
                )
            ) {
                Text(stringResource(R.string.back_arrow), fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.tree_stages),
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
            items(items = stagesList) { stage: TreeStage ->
                TreeStageCard(stage)
            }
        }
    }
}

@Composable
fun TreeStageCard(stage: TreeStage) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        )
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
                text = getStageText(stage),
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

@Composable
fun getEmissionRange(stage: TreeStage): String {
    return when (stage) {
        TreeStage.SEED -> stringResource(R.string.range_seed)
        TreeStage.SPROUT -> stringResource(R.string.range_sprout)
        TreeStage.SMALL_TREE -> stringResource(R.string.range_small)
        TreeStage.MEDIUM_TREE -> stringResource(R.string.range_medium)
        TreeStage.BIG_TREE -> stringResource(R.string.range_big)
        TreeStage.FULL_TREE -> stringResource(R.string.range_full)
        TreeStage.SICK -> stringResource(R.string.range_sick)
        TreeStage.DEAD -> stringResource(R.string.range_dead)
    }
}

@Composable
fun getStageText(stage: TreeStage): String {
    return when (stage) {
        TreeStage.SEED -> stringResource(R.string.stage_seedling)
        TreeStage.SPROUT -> stringResource(R.string.stage_growing)
        TreeStage.SMALL_TREE -> stringResource(R.string.stage_little_tree)
        TreeStage.MEDIUM_TREE -> stringResource(R.string.stage_expanding)
        TreeStage.BIG_TREE -> stringResource(R.string.stage_sturdy_tree)
        TreeStage.FULL_TREE -> stringResource(R.string.stage_majestic_tree)
        TreeStage.SICK -> stringResource(R.string.stage_feeling_unwell)
        TreeStage.DEAD -> stringResource(R.string.stage_withered)
    }
}

fun getTreeImage(stage: TreeStage): Int {
    return when (stage) {
        TreeStage.SEED -> R.drawable.tree1
        TreeStage.SPROUT -> R.drawable.tree2
        TreeStage.SMALL_TREE -> R.drawable.tree3
        TreeStage.MEDIUM_TREE -> R.drawable.tree4
        TreeStage.BIG_TREE -> R.drawable.tree5
        TreeStage.FULL_TREE -> R.drawable.tree6
        TreeStage.SICK -> R.drawable.tree7
        TreeStage.DEAD -> R.drawable.tree8
    }
}
