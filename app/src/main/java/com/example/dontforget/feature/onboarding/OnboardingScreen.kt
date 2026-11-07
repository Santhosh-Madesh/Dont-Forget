package com.example.dontforget.feature.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dontforget.R
import com.example.dontforget.ui.theme.Spacing
import com.example.dontforget.ui.theme.extendedColors

private val spacing = Spacing()

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onScheduleTestAlarm: (title: String, content: String) -> Unit,
    onOnboardingComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is OnboardingUiSideEffect.TriggerInstantTestAlarm -> {
                    onScheduleTestAlarm(effect.title, effect.content)
                }
                OnboardingUiSideEffect.CompleteOnboardingAndNavigateHome -> {
                    onOnboardingComplete()
                }
            }
        }
    }

    OnboardingContent(
        state = state,
        onEvent = viewModel::onEvent,
        modifier = modifier
    )
}

@Composable
fun OnboardingContent(
    state: OnboardingUiState,
    onEvent: (OnboardingUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.extendedColors.onboardingSurfaceSubtle
    ) {
        AnimatedContent(
            targetState = state.currentStep,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "OnboardingStepAnimation"
        ) { step ->
            when (step) {
                OnboardingStep.WELCOME -> OnboardingWelcomeStep(
                    onGetStarted = { onEvent(OnboardingUiEvent.OnGetStartedClicked) },
                    onSkip = { onEvent(OnboardingUiEvent.OnSkipClicked) }
                )
                OnboardingStep.CHOOSE_CONTEXT -> OnboardingChooseContextStep(
                    contextText = state.contextText,
                    onContextChanged = { onEvent(OnboardingUiEvent.OnContextChanged(it)) },
                    onNext = { onEvent(OnboardingUiEvent.OnNextClicked) },
                    onBack = { onEvent(OnboardingUiEvent.OnBackClicked) }
                )
                OnboardingStep.ADD_ITEMS -> OnboardingAddItemsStep(
                    contextText = state.contextText,
                    newItemInput = state.newItemInput,
                    checklistItems = state.checklistItems,
                    onNewItemInputChanged = { onEvent(OnboardingUiEvent.OnNewItemInputChanged(it)) },
                    onAddItem = { onEvent(OnboardingUiEvent.OnAddItemClicked) },
                    onRemoveItem = { onEvent(OnboardingUiEvent.OnRemoveItemClicked(it)) },
                    onNext = { onEvent(OnboardingUiEvent.OnNextClicked) },
                    onBack = { onEvent(OnboardingUiEvent.OnBackClicked) }
                )
                OnboardingStep.CONFIGURE_TIMING -> OnboardingConfigureTimingStep(
                    departureTimeText = state.departureTimeText,
                    onDepartureTimeChanged = { onEvent(OnboardingUiEvent.OnDepartureTimeChanged(it)) },
                    onNext = { onEvent(OnboardingUiEvent.OnNextClicked) },
                    onBack = { onEvent(OnboardingUiEvent.OnBackClicked) }
                )
                OnboardingStep.CONFIRMATION_SUMMARY -> OnboardingConfirmationSummaryStep(
                    state = state,
                    onDone = { onEvent(OnboardingUiEvent.OnConfirmReminderClicked) },
                    onBack = { onEvent(OnboardingUiEvent.OnBackClicked) }
                )
                OnboardingStep.INTERACTIVE_PACKING -> OnboardingInteractivePackingStep(
                    state = state,
                    onTogglePacked = { onEvent(OnboardingUiEvent.OnToggleItemPacked(it)) },
                    onPackAll = { onEvent(OnboardingUiEvent.OnPackAllClicked) },
                    onFinish = { onEvent(OnboardingUiEvent.OnNextClicked) },
                    onBack = { onEvent(OnboardingUiEvent.OnBackClicked) }
                )
                OnboardingStep.COMPLETION_VICTORY -> OnboardingCompletionVictoryStep(
                    onComplete = { onEvent(OnboardingUiEvent.OnFinishOnboardingClicked) }
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
// STEP 1: WELCOME
// -----------------------------------------------------------------------------
@Composable
fun OnboardingWelcomeStep(
    onGetStarted: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(spacing.large),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(spacing.large))

        Box(
            modifier = Modifier
                .size(180.dp)
                .clip(CircleShape)
                .background(MaterialTheme.extendedColors.onboardingBrandContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.extendedColors.onboardingBrandPrimary,
                modifier = Modifier.size(80.dp)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = spacing.medium)
        ) {
            Text(
                text = stringResource(R.string.onboarding_welcome_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(spacing.medium))
            Text(
                text = stringResource(R.string.onboarding_welcome_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = onGetStarted,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.extendedColors.onboardingBrandPrimary),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = stringResource(R.string.onboarding_welcome_get_started),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(spacing.small))
            TextButton(onClick = onSkip) {
                Text(
                    text = stringResource(R.string.onboarding_welcome_skip),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 15.sp
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
// STEP 2: CHOOSE CONTEXT
// -----------------------------------------------------------------------------
@Composable
fun OnboardingChooseContextStep(
    contextText: String,
    onContextChanged: (String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(spacing.medium)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            OnboardingHeader(
                stepText = stringResource(R.string.onboarding_step_1_of_5),
                progress = 0.2f,
                onBack = onBack
            )
            Spacer(modifier = Modifier.height(spacing.medium))

            Text(
                text = stringResource(R.string.onboarding_context_title),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(spacing.extraSmall))
            Text(
                text = stringResource(R.string.onboarding_context_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(spacing.extraLarge))

            Text(
                text = stringResource(R.string.onboarding_context_field_label),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(spacing.extraSmall))

            OutlinedTextField(
                value = contextText,
                onValueChange = onContextChanged,
                placeholder = { Text(stringResource(R.string.onboarding_context_field_placeholder)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.extendedColors.onboardingBrandAccent
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                )
            )

            Spacer(modifier = Modifier.height(spacing.small))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(spacing.extraSmall))
                Text(
                    text = stringResource(R.string.onboarding_context_field_hint),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Button(
            onClick = onNext,
            enabled = contextText.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = spacing.medium)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.extendedColors.onboardingBrandPrimary),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = stringResource(R.string.onboarding_action_next),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// -----------------------------------------------------------------------------
// STEP 3: ADD ITEMS
// -----------------------------------------------------------------------------
@Composable
fun OnboardingAddItemsStep(
    contextText: String,
    newItemInput: String,
    checklistItems: List<String>,
    onNewItemInputChanged: (String) -> Unit,
    onAddItem: () -> Unit,
    onRemoveItem: (String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(spacing.medium)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            OnboardingHeader(
                stepText = stringResource(R.string.onboarding_step_2_of_5),
                progress = 0.4f,
                onBack = onBack
            )
            Spacer(modifier = Modifier.height(spacing.medium))

            Text(
                text = stringResource(R.string.onboarding_items_title),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(spacing.extraSmall))
            Text(
                text = stringResource(R.string.onboarding_items_subtitle, contextText),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(spacing.large))

            OutlinedTextField(
                value = newItemInput,
                onValueChange = onNewItemInputChanged,
                placeholder = { Text(stringResource(R.string.onboarding_items_input_placeholder)) },
                trailingIcon = {
                    IconButton(onClick = onAddItem) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.extendedColors.onboardingBrandPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add item",
                                tint = Color.White
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                )
            )

            Spacer(modifier = Modifier.height(spacing.medium))

            checklistItems.forEach { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = spacing.medium, vertical = spacing.small),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color.LightGray.copy(alpha = 0.4f))
                            )
                            Spacer(modifier = Modifier.width(spacing.medium))
                            Text(
                                text = item,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        IconButton(onClick = { onRemoveItem(item) }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove",
                                tint = Color.Gray
                            )
                        }
                    }
                }
            }
        }

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = spacing.medium)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.extendedColors.onboardingBrandPrimary),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = stringResource(R.string.onboarding_action_next),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// -----------------------------------------------------------------------------
// STEP 4: CONFIGURE TIMING
// -----------------------------------------------------------------------------
@Composable
fun OnboardingConfigureTimingStep(
    departureTimeText: String,
    onDepartureTimeChanged: (String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(spacing.medium)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            OnboardingHeader(
                stepText = stringResource(R.string.onboarding_step_3_of_5),
                progress = 0.6f,
                onBack = onBack
            )
            Spacer(modifier = Modifier.height(spacing.medium))

            Text(
                text = stringResource(R.string.onboarding_timing_title),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(spacing.large))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.onboarding_timing_departure_label),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(R.string.onboarding_timing_local_time),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(spacing.small))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(spacing.medium),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = departureTimeText,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.extendedColors.onboardingBrandPrimary)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "PM",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(spacing.large))

            Text(
                text = stringResource(R.string.onboarding_timing_notification_label),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(spacing.small))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(spacing.medium),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.extendedColors.onboardingBrandContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = MaterialTheme.extendedColors.onboardingBrandAccent
                        )
                    }

                    Spacer(modifier = Modifier.width(spacing.medium))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stringResource(R.string.onboarding_timing_now_title),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(spacing.small))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.extendedColors.onboardingBrandContainer)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.onboarding_timing_now_badge),
                                    fontSize = 11.sp,
                                    color = MaterialTheme.extendedColors.onboardingBrandAccent,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Text(
                            text = stringResource(R.string.onboarding_timing_now_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.extendedColors.onboardingBrandPrimary,
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.extendedColors.onboardingBrandContainer)
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.medium))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.padding(spacing.medium),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(spacing.small))
                    Text(
                        text = stringResource(R.string.onboarding_timing_info_text),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = spacing.medium)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.extendedColors.onboardingBrandPrimary),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = stringResource(R.string.onboarding_action_next),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// -----------------------------------------------------------------------------
// STEP 5: CONFIRMATION SUMMARY
// -----------------------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingConfirmationSummaryStep(
    state: OnboardingUiState,
    onDone: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(spacing.medium)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            OnboardingHeader(
                stepText = stringResource(R.string.onboarding_step_4_of_5),
                progress = 0.8f,
                onBack = onBack
            )
            Spacer(modifier = Modifier.height(spacing.medium))

            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.extendedColors.onboardingBrandContainer)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.extendedColors.onboardingBrandPrimary,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(spacing.medium))

            Text(
                text = stringResource(R.string.onboarding_summary_title),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = stringResource(R.string.onboarding_summary_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(spacing.large))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(spacing.medium)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.FitnessCenter,
                                contentDescription = null,
                                tint = MaterialTheme.extendedColors.onboardingBrandPrimary
                            )
                            Spacer(modifier = Modifier.width(spacing.small))
                            Text(
                                text = state.contextText,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.extendedColors.onboardingBrandContainer)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.onboarding_summary_status_active),
                                fontSize = 12.sp,
                                color = MaterialTheme.extendedColors.onboardingBrandAccent,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(spacing.medium))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.onboarding_summary_departure_header),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = state.departureTimeText,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Column {
                            Text(
                                text = stringResource(R.string.onboarding_summary_reminder_header),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = state.reminderLeadText,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(spacing.medium))

                    Text(
                        text = stringResource(R.string.onboarding_summary_packing_header),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(spacing.extraSmall))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        state.checklistItems.forEach { item ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = item,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = onDone,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = spacing.medium)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.extendedColors.onboardingBrandPrimary),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = stringResource(R.string.onboarding_action_done),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// -----------------------------------------------------------------------------
// STEP 6: INTERACTIVE PACKING
// -----------------------------------------------------------------------------
@Composable
fun OnboardingInteractivePackingStep(
    state: OnboardingUiState,
    onTogglePacked: (String) -> Unit,
    onPackAll: () -> Unit,
    onFinish: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(spacing.medium)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.extendedColors.onboardingBrandContainer)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = MaterialTheme.extendedColors.onboardingBrandAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(spacing.small))
                    Text(
                        text = stringResource(R.string.onboarding_packing_banner),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.extendedColors.onboardingBrandAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.small))

            OnboardingHeader(
                stepText = stringResource(R.string.onboarding_step_5_of_5),
                progress = 1.0f,
                onBack = onBack
            )

            Spacer(modifier = Modifier.height(spacing.small))

            Text(
                text = stringResource(R.string.onboarding_packing_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(spacing.extraSmall))
            Text(
                text = stringResource(R.string.onboarding_packing_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(spacing.medium))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.onboarding_packing_cargo_header),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.width(spacing.small))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.LightGray.copy(alpha = 0.3f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = stringResource(
                                R.string.onboarding_packing_cargo_count,
                                state.packedItems.size,
                                state.checklistItems.size
                            ),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                TextButton(onClick = onPackAll) {
                    Text(
                        text = stringResource(R.string.onboarding_packing_pack_all),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.extendedColors.onboardingBrandPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.small))

            state.checklistItems.forEach { item ->
                val isPacked = state.packedItems.contains(item)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onTogglePacked(item) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(spacing.medium),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isPacked,
                            onCheckedChange = { onTogglePacked(item) },
                            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.extendedColors.onboardingBrandPrimary)
                        )
                        Spacer(modifier = Modifier.width(spacing.small))
                        Text(
                            text = item,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        Button(
            onClick = onFinish,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = spacing.medium)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.extendedColors.onboardingBrandPrimary),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = stringResource(R.string.onboarding_packing_finish),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// -----------------------------------------------------------------------------
// STEP 7: COMPLETION VICTORY
// -----------------------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingCompletionVictoryStep(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(spacing.medium)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onComplete) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.LightGray.copy(alpha = 0.3f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.onboarding_victory_brand_tag),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.medium))

            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.extendedColors.onboardingBrandContainer)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.extendedColors.onboardingBrandPrimary,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(spacing.medium))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.extendedColors.onboardingBrandContainer)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = stringResource(R.string.onboarding_victory_pill),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.extendedColors.onboardingBrandAccent
                )
            }

            Spacer(modifier = Modifier.height(spacing.medium))

            Text(
                text = stringResource(R.string.onboarding_victory_title),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(spacing.small))
            Text(
                text = stringResource(R.string.onboarding_victory_body),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(spacing.large))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(spacing.medium)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.onboarding_victory_inventory_header),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = stringResource(R.string.onboarding_victory_inventory_sub),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.extendedColors.onboardingBrandAccent
                        )
                    }

                    Spacer(modifier = Modifier.height(spacing.small))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val verifiedItems = listOf("Keys & FOB", "Work Badge", "Noise Canceling", "Water Bottle")
                        verifiedItems.forEach { item ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "✓ $item",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = onComplete,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = spacing.medium)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.extendedColors.onboardingBrandPrimary),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = stringResource(R.string.onboarding_action_roll),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// -----------------------------------------------------------------------------
// HELPER HEADER
// -----------------------------------------------------------------------------
@Composable
private fun OnboardingHeader(
    stepText: String,
    progress: Float,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Text(
                text = stringResource(R.string.onboarding_header_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.size(48.dp))
        }

        Spacer(modifier = Modifier.height(spacing.extraSmall))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = MaterialTheme.extendedColors.onboardingBrandPrimary,
                trackColor = Color.LightGray.copy(alpha = 0.4f)
            )
            Spacer(modifier = Modifier.width(spacing.medium))
            Text(
                text = stepText,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingWelcomeStepPreview() {
    OnboardingWelcomeStep(onGetStarted = {}, onSkip = {})
}

@Preview(showBackground = true)
@Composable
fun OnboardingChooseContextStepPreview() {
    OnboardingChooseContextStep(
        contextText = "Gym",
        onContextChanged = {},
        onNext = {},
        onBack = {}
    )
}
