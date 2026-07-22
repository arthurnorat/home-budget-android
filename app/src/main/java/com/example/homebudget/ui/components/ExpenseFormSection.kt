package com.example.homebudget.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.homebudget.model.Category
import com.example.homebudget.ui.theme.SuccessGreen
import com.example.homebudget.util.Formatters
import com.example.homebudget.viewmodel.ExpenseEntryUiState
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseFormSection(
    uiState: ExpenseEntryUiState,
    onDescriptionChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onDateChange: (String) -> Unit,
    onCategoryChange: (Category) -> Unit,
    onSaveClick: () -> Unit,
    onSavedConfirmationTimeout: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    var showDatePicker by remember { mutableStateOf(false) }

    // Equivalente ao .onAppear { descriptionFocused = true } do iOS
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    // "Gasto salvo!" por 2s + refoco na descrição (fluxo de lançamento em sequência)
    LaunchedEffect(uiState.showSavedConfirmation) {
        if (uiState.showSavedConfirmation) {
            delay(2000)
            onSavedConfirmationTimeout()
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = uiState.description,
            onValueChange = onDescriptionChange,
            label = { Text("Descrição") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().focusRequester(focusRequester)
        )

        OutlinedTextField(
            value = uiState.amountText,
            onValueChange = onAmountChange,
            label = { Text("Valor (R$)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        // Campo "Data": OutlinedTextField somente leitura que abre um DatePickerDialog.
        // readOnly=true por si só ainda intercepta o toque para foco/cursor, então
        // usamos um interactionSource para detectar o clique de forma confiável.
        val dateFieldInteractionSource = remember { MutableInteractionSource() }
        LaunchedEffect(dateFieldInteractionSource) {
            dateFieldInteractionSource.interactions.collect { interaction ->
                if (interaction is PressInteraction.Release) showDatePicker = true
            }
        }
        OutlinedTextField(
            value = Formatters.displayDate(uiState.dateIso),
            onValueChange = {},
            readOnly = true,
            label = { Text("Data") },
            trailingIcon = { Icon(Icons.Filled.DateRange, contentDescription = null) },
            interactionSource = dateFieldInteractionSource,
            modifier = Modifier.fillMaxWidth()
        )

        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            Category.entries.forEachIndexed { index, category ->
                SegmentedButton(
                    selected = uiState.category == category,
                    onClick = { onCategoryChange(category) },
                    shape = SegmentedButtonDefaults.itemShape(index, Category.entries.size)
                ) {
                    Text(category.displayName)
                }
            }
        }

        Button(
            onClick = onSaveClick,
            enabled = uiState.canSave && !uiState.isSaving,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState.isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Salvar")
            }
        }

        if (uiState.showSavedConfirmation) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = SuccessGreen)
                Text("Gasto salvo!", color = SuccessGreen, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = Formatters.isoDateToUtcMillis(uiState.dateIso)
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        onDateChange(Formatters.utcMillisToIsoDate(millis))
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") } }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
