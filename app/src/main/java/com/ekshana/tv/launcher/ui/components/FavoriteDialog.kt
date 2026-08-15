package com.ekshana.tv.launcher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import com.ekshana.tv.launcher.data.AppInfo
import com.ekshana.tv.launcher.ui.theme.DialogBg
import com.ekshana.tv.launcher.ui.theme.TextPrimary
import com.ekshana.tv.launcher.ui.theme.TextSecondary

/**
 * A minimal dialog shown on long-press of an [AppCard].
 * Contains only two focusable actions so D-pad Up/Down navigates between them.
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun FavoriteDialog(
    app: AppInfo,
    isFavorite: Boolean,
    onToggle: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .width(300.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(DialogBg)
                .padding(28.dp),
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = app.label,
                    fontSize = 18.sp,
                    color = TextPrimary,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = app.packageName,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    maxLines = 1,
                )
                Spacer(Modifier.height(20.dp))

                // Primary action
                Button(
                    onClick = {
                        onToggle()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(if (isFavorite) "★  Remove from Favorites" else "☆  Add to Favorites")
                }

                Spacer(Modifier.height(10.dp))

                // Cancel
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}
