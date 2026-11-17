package com.poswal.noor.feature.saved

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.poswal.noor.core.designsystem.NoorShapes
import com.poswal.noor.core.designsystem.NoorSpacing
import com.poswal.noor.core.designsystem.NoorThemeTokens
import com.poswal.noor.core.designsystem.components.CalmCard
import com.poswal.noor.domain.model.SavedItem
import com.poswal.noor.feature.saved.components.CollectionFilterRow
import com.poswal.noor.feature.saved.components.CreateCollectionDialog
import com.poswal.noor.feature.saved.components.EditNoteBottomSheet
import com.poswal.noor.feature.saved.components.MoveCollectionSheet
import com.poswal.noor.feature.saved.components.SavedEmptyState
import com.poswal.noor.feature.saved.components.SavedItemCard
import com.poswal.noor.feature.saved.model.SavedTypeFilter
import kotlinx.coroutines.launch
import java.io.InputStream
import java.io.OutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedScreen(
    onNavigateToSurahReader: (surahNumber: Int, ayahNumber: Int) -> Unit = { _, _ -> },
    onNavigateToHadithDetail: (hadithId: Long) -> Unit = {},
    viewModel: SavedViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val colors = NoorThemeTokens.colors
    val typography = NoorThemeTokens.typography
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var isSearchExpanded by remember { mutableStateOf(false) }
    var showOptionsMenu by remember { mutableStateOf(false) }
    var showCreateCollectionDialog by remember { mutableStateOf(false) }

    var selectedItemForNote by remember { mutableStateOf<SavedItem?>(null) }
    var selectedItemForMove by remember { mutableStateOf<SavedItem?>(null) }

    // Pending JSON string to export via SAF
    var pendingExportJson by remember { mutableStateOf<String?>(null) }

    // Storage Access Framework (SAF) — Export JSON Launcher
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        if (uri != null && pendingExportJson != null) {
            try {
                context.contentResolver.openOutputStream(uri)?.use { outputStream: OutputStream ->
                    outputStream.write(pendingExportJson!!.toByteArray(Charsets.UTF_8))
                }
                scope.launch {
                    snackbarHostState.showSnackbar("Bookmarks backup saved successfully")
                }
            } catch (e: Exception) {
                scope.launch {
                    snackbarHostState.showSnackbar("Export failed: ${e.localizedMessage}")
                }
            } finally {
                pendingExportJson = null
            }
        }
    }

    // Storage Access Framework (SAF) — Import JSON Launcher
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val jsonString = context.contentResolver.openInputStream(uri)?.use { inputStream: InputStream ->
                    inputStream.bufferedReader(Charsets.UTF_8).readText()
                } ?: ""

                viewModel.importBookmarksJson(jsonString) { success, msg ->
                    scope.launch {
                        snackbarHostState.showSnackbar(msg)
                    }
                }
            } catch (e: Exception) {
                scope.launch {
                    snackbarHostState.showSnackbar("Failed to read file: ${e.localizedMessage}")
                }
            }
        }
    }

    // Show transient user messages from ViewModel
    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearUserMessage()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colors.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "Saved Library",
                                style = typography.title.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                                color = colors.textPrimary
                            )
                            Text(
                                text = "${uiState.totalCount} saved items · 100% Offline",
                                style = typography.caption.copy(fontSize = 11.sp),
                                color = colors.textSecondary
                            )
                        }
                    },
                    actions = {
                        // Search Toggle Button
                        IconButton(onClick = { isSearchExpanded = !isSearchExpanded }) {
                            Icon(
                                imageVector = if (isSearchExpanded) Icons.Default.Close else Icons.Default.Search,
                                contentDescription = "Search",
                                tint = colors.textPrimary
                            )
                        }

                        // Overflow Options Menu
                        Box {
                            IconButton(onClick = { showOptionsMenu = true }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Options",
                                    tint = colors.textPrimary
                                )
                            }

                            DropdownMenu(
                                expanded = showOptionsMenu,
                                onDismissRequest = { showOptionsMenu = false },
                                modifier = Modifier.background(colors.surface)
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Backup Bookmarks (JSON Export)", style = typography.body.copy(fontSize = 14.sp)) },
                                    leadingIcon = {
                                        Icon(Icons.Default.FileUpload, contentDescription = null, tint = colors.primary)
                                    },
                                    onClick = {
                                        showOptionsMenu = false
                                        viewModel.exportBookmarksJson { json ->
                                            pendingExportJson = json
                                            exportLauncher.launch("poswal_noor_bookmarks_${System.currentTimeMillis()}.json")
                                        }
                                    }
                                )

                                DropdownMenuItem(
                                    text = { Text("Restore Bookmarks (JSON Import)", style = typography.body.copy(fontSize = 14.sp)) },
                                    leadingIcon = {
                                        Icon(Icons.Default.FileDownload, contentDescription = null, tint = colors.accent)
                                    },
                                    onClick = {
                                        showOptionsMenu = false
                                        importLauncher.launch(arrayOf("application/json", "text/*"))
                                    }
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colors.background,
                        scrolledContainerColor = colors.surface
                    )
                )

                // Animated Search Bar
                AnimatedVisibility(
                    visible = isSearchExpanded,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = NoorSpacing.l, vertical = NoorSpacing.xs)
                    ) {
                        OutlinedTextField(
                            value = uiState.searchQuery,
                            onValueChange = { viewModel.setSearchQuery(it) },
                            placeholder = { Text("Search by verse, translation, or note...", fontSize = 13.sp) },
                            singleLine = true,
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = null, tint = colors.textSecondary)
                            },
                            trailingIcon = {
                                if (uiState.searchQuery.isNotBlank()) {
                                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = colors.textSecondary)
                                    }
                                }
                            },
                            shape = NoorShapes.chip,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = colors.surface,
                                unfocusedContainerColor = colors.surface,
                                focusedIndicatorColor = colors.primary,
                                unfocusedIndicatorColor = colors.outline
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Type Filter Pills (All / Ayahs / Hadith) & Notes Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = NoorSpacing.l, vertical = NoorSpacing.xs),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Segmented Pill: All · Ayahs · Hadith
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = colors.surfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, colors.outline)
                    ) {
                        Row(modifier = Modifier.padding(3.dp)) {
                            SavedTypeFilter.entries.forEach { filter ->
                                val isSelected = uiState.selectedType == filter
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(9.dp))
                                        .background(if (isSelected) colors.primary else colors.surfaceElevated)
                                        .clickable { viewModel.setTypeFilter(filter) }
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = filter.title,
                                        style = typography.caption.copy(
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        ),
                                        color = if (isSelected) colors.onPrimary else colors.textSecondary
                                    )
                                }
                            }
                        }
                    }

                    // Notes-Only Toggle Chip
                    val isNotesActive = uiState.hasNotesOnly
                    Box(
                        modifier = Modifier
                            .height(34.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isNotesActive) colors.accent.copy(alpha = 0.2f) else colors.surface)
                            .border(
                                width = 1.dp,
                                color = if (isNotesActive) colors.accent else colors.outline,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { viewModel.toggleNotesOnly() }
                            .padding(horizontal = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.EditNote,
                                contentDescription = null,
                                tint = if (isNotesActive) colors.accent else colors.textSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Notes",
                                style = typography.caption.copy(
                                    fontSize = 12.sp,
                                    fontWeight = if (isNotesActive) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = if (isNotesActive) colors.accent else colors.textSecondary
                            )
                        }
                    }
                }

                // Collections Filter Row (Folders)
                CollectionFilterRow(
                    collections = uiState.collections,
                    selectedCollection = uiState.selectedCollection,
                    onSelectCollection = { viewModel.setCollectionFilter(it) },
                    onNewCollectionClick = { showCreateCollectionDialog = true },
                    onDeleteCollection = { viewModel.deleteCollection(it) }
                )

                Spacer(modifier = Modifier.height(NoorSpacing.xs))
            }
        }
    ) { innerPadding ->
        Crossfade(
            targetState = uiState.items.isEmpty(),
            label = "saved_content_crossfade",
            modifier = Modifier.padding(innerPadding)
        ) { isEmpty ->
            if (isEmpty) {
                SavedEmptyState(
                    isSearching = uiState.searchQuery.isNotBlank() || uiState.selectedCollection != null
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = NoorSpacing.l,
                        end = NoorSpacing.l,
                        top = NoorSpacing.s,
                        bottom = 100.dp // Accommodate floating glass bottom bar
                    ),
                    verticalArrangement = Arrangement.spacedBy(NoorSpacing.m)
                ) {
                    items(
                        items = uiState.items,
                        key = { it.bookmark.id }
                    ) { item ->
                        SavedItemCard(
                            savedItem = item,
                            onItemClick = {
                                if (item.content.type.equals("quran", ignoreCase = true)) {
                                    val surah = item.content.surah ?: 1
                                    val ayah = item.content.ayah ?: 1
                                    onNavigateToSurahReader(surah, ayah)
                                } else {
                                    onNavigateToHadithDetail(item.content.id)
                                }
                            },
                            onMoveCollectionClick = { selectedItemForMove = item },
                            onEditNoteClick = { selectedItemForNote = item },
                            onShareClick = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "${item.content.arabic}\n\n${item.content.translationEn}\n\n— ${item.content.reference} (via Poswal Noor)"
                                    )
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share Scripture"))
                            },
                            onRemoveClick = {
                                viewModel.removeBookmark(item.bookmark.contentId)
                            }
                        )
                    }
                }
            }
        }
    }

    // Dialog: Create New Collection
    if (showCreateCollectionDialog) {
        CreateCollectionDialog(
            onDismiss = { showCreateCollectionDialog = false },
            onConfirm = { viewModel.createCollection(it) }
        )
    }

    // Bottom Sheet: Edit Reflection Note
    selectedItemForNote?.let { item ->
        EditNoteBottomSheet(
            savedItem = item,
            onDismiss = { selectedItemForNote = null },
            onSaveNote = { note ->
                viewModel.updateNote(item.bookmark.contentId, note)
            }
        )
    }

    // Bottom Sheet: Move to Collection
    selectedItemForMove?.let { item ->
        MoveCollectionSheet(
            savedItem = item,
            availableCollections = uiState.collections,
            onDismiss = { selectedItemForMove = null },
            onSelectCollection = { folder ->
                viewModel.moveItemToCollection(item.bookmark.contentId, folder)
            },
            onCreateNewCollection = {
                showCreateCollectionDialog = true
            }
        )
    }
}
