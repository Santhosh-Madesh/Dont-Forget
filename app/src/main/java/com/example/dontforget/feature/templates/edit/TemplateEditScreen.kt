package com.example.dontforget.feature.templates.edit

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.dontforget.R
import com.example.dontforget.core.ui.components.AppTopBar
import com.example.dontforget.core.ui.components.ConfirmationDialog
import com.example.dontforget.core.ui.components.ContextIcons
import com.example.dontforget.core.ui.components.WarningBanner

@Composable
fun TemplateEditScreen(
    templateId: Long,
    viewModel: TemplateEditViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                TemplateEditUiSideEffect.NavigateBack -> {
                    onNavigateBack()
                }
                is TemplateEditUiSideEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    if (uiState.showDiscardDialog) {
        ConfirmationDialog(
            title = stringResource(id = R.string.template_edit_discard_title),
            message = stringResource(id = R.string.template_edit_discard_msg),
            confirmText = stringResource(id = R.string.common_btn_discard),
            dismissText = stringResource(id = R.string.common_btn_cancel),
            onConfirm = { viewModel.onEvent(TemplateEditUiEvent.OnConfirmDiscardClick) },
            onDismiss = { viewModel.onEvent(TemplateEditUiEvent.OnDismissDiscardDialogClick) }
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            AppTopBar(
                title = if (templateId > 0L)
                    stringResource(id = R.string.template_edit_title_edit)
                else
                    stringResource(id = R.string.template_edit_title_create),
                onBackClick = { viewModel.onEvent(TemplateEditUiEvent.OnBackClick) }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                else -> {
                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(16.dp)
                    ) {
                        if (uiState.validationError != null) {
                            WarningBanner(
                                message = uiState.validationError!!,
                                actionLabel = stringResource(id = R.string.common_btn_dismiss),
                                onActionClick = { viewModel.onEvent(TemplateEditUiEvent.OnTemplateTitleChange(uiState.templateTitleInput)) },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Template Title
                        Text(
                            text = stringResource(id = R.string.template_edit_section_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = uiState.templateTitleInput,
                            onValueChange = { viewModel.onEvent(TemplateEditUiEvent.OnTemplateTitleChange(it)) },
                            placeholder = { Text(stringResource(id = R.string.template_edit_placeholder_title)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Context Selection
                        Text(
                            text = stringResource(id = R.string.template_edit_section_context),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = !uiState.isNewContext,
                                onClick = { viewModel.onEvent(TemplateEditUiEvent.OnToggleNewContext(false)) },
                                label = { Text(stringResource(id = R.string.template_edit_chip_existing)) }
                            )
                            FilterChip(
                                selected = uiState.isNewContext,
                                onClick = { viewModel.onEvent(TemplateEditUiEvent.OnToggleNewContext(true)) },
                                label = { Text(stringResource(id = R.string.template_edit_chip_new)) }
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (!uiState.isNewContext) {
                            if (uiState.availableContexts.isEmpty()) {
                                Text(
                                    text = stringResource(id = R.string.template_edit_no_existing_contexts),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(
                                        items = uiState.availableContexts,
                                        key = { it.id }
                                    ) { context ->
                                        FilterChip(
                                            selected = uiState.selectedContextId == context.id,
                                            onClick = { viewModel.onEvent(TemplateEditUiEvent.OnSelectExistingContext(context.id)) },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = ContextIcons.getIconByName(context.iconName),
                                                    contentDescription = context.title,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            },
                                            label = { Text(context.title) }
                                        )
                                    }
                                }
                            }
                        } else {
                            // New Context Form
                            OutlinedTextField(
                                value = uiState.contextTitleInput,
                                onValueChange = { viewModel.onEvent(TemplateEditUiEvent.OnContextTitleChange(it)) },
                                label = { Text(stringResource(id = R.string.template_edit_label_new_context)) },
                                placeholder = { Text(stringResource(id = R.string.template_edit_placeholder_new_context)) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = stringResource(id = R.string.template_edit_label_icon_picker),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // Context Icons Selector
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(
                                    items = ContextIcons.AVAILABLE_ICONS,
                                    key = { it.name }
                                ) { iconItem ->
                                    val isSelected = uiState.contextIconName.equals(iconItem.name, ignoreCase = true)
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .border(
                                                width = if (isSelected) 2.dp else 1.dp,
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .clickable {
                                                viewModel.onEvent(TemplateEditUiEvent.OnContextIconSelect(iconItem.name))
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = iconItem.icon,
                                            contentDescription = iconItem.label,
                                            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Checklist Items Manager
                        Text(
                            text = stringResource(id = R.string.template_edit_section_checklist),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = uiState.newItemInput,
                                onValueChange = { viewModel.onEvent(TemplateEditUiEvent.OnNewItemInputChange(it)) },
                                placeholder = { Text(stringResource(id = R.string.template_edit_placeholder_add_item)) },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = { viewModel.onEvent(TemplateEditUiEvent.OnAddChecklistItem) }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = "Add Item"
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        uiState.checklistItems.forEachIndexed { index, itemText ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        IconButton(
                                            onClick = {
                                                if (index > 0) {
                                                    viewModel.onEvent(TemplateEditUiEvent.OnReorderChecklistItem(index, index - 1))
                                                }
                                            },
                                            enabled = index > 0,
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.ArrowUpward,
                                                contentDescription = "Move Up",
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        IconButton(
                                            onClick = {
                                                if (index < uiState.checklistItems.lastIndex) {
                                                    viewModel.onEvent(TemplateEditUiEvent.OnReorderChecklistItem(index, index + 1))
                                                }
                                            },
                                            enabled = index < uiState.checklistItems.lastIndex,
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.ArrowDownward,
                                                contentDescription = "Move Down",
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    OutlinedTextField(
                                        value = itemText,
                                        onValueChange = { newText ->
                                            viewModel.onEvent(TemplateEditUiEvent.OnEditChecklistItem(index, newText))
                                        },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true
                                    )
                                    IconButton(
                                        onClick = { viewModel.onEvent(TemplateEditUiEvent.OnRemoveChecklistItem(index)) }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Delete,
                                            contentDescription = "Remove Item",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = { viewModel.onEvent(TemplateEditUiEvent.OnSaveTemplateClick) },
                            enabled = !uiState.isSaving,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            if (uiState.isSaving) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Text(
                                    text = if (templateId > 0L)
                                        stringResource(id = R.string.template_edit_btn_save_changes)
                                    else
                                        stringResource(id = R.string.template_edit_btn_save),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
