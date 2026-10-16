package com.example.mios.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.mios.util.toTimeString

@Composable
fun PlayerControls(viewModel: LibraryViewModel, onExpand: () -> Unit) {
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val position by viewModel.currentPosition.collectAsState()
    val duration by viewModel.duration.collectAsState()
    val hasNext by viewModel.hasNext.collectAsState()
    val hasPrevious by viewModel.hasPrevious.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onExpand() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = currentSong?.title ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${position.toTimeString()} / ${duration.toTimeString()}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            IconButton(onClick = { viewModel.playPrevious() }, enabled = hasPrevious) {
                Icon(Icons.Default.SkipPrevious, contentDescription = "Anterior")
            }
            IconButton(onClick = { viewModel.togglePlayPause() }) {
                Icon(
                    if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Reproducir/Pausar"
                )
            }
            IconButton(onClick = { viewModel.playNext() }, enabled = hasNext) {
                Icon(Icons.Default.SkipNext, contentDescription = "Siguiente")
            }
        }

        Slider(
            value = if (duration > 0) (position.toFloat() / duration.toFloat()).coerceIn(0f, 1f) else 0f,
            onValueChange = { fraction ->
                viewModel.seekTo((fraction * duration).toLong())
            }
        )
    }
}