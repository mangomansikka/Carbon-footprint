package fi.metropolia.canopy.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

class PermissionManager {

    companion object {
        val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACTIVITY_RECOGNITION
        )
    }

    fun hasAllPermissions(context: Context): Boolean {
        return REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                context,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
}

@Composable
fun rememberPermissionLauncher(
    onPermissionsGranted: () -> Unit,
    onPermissionsDenied: () -> Unit
): () -> Unit {

    val context = LocalContext.current
    val permissionManager = PermissionManager()

    val launcher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            val granted = PermissionManager.REQUIRED_PERMISSIONS.all {
                permissions[it] == true
            }

            if (granted) onPermissionsGranted()
            else onPermissionsDenied()
        }

    return {
        if (permissionManager.hasAllPermissions(context)) {
            onPermissionsGranted()
        } else {
            launcher.launch(PermissionManager.REQUIRED_PERMISSIONS)
        }
    }
}