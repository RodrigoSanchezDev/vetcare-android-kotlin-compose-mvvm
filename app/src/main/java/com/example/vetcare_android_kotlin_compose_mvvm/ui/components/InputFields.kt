package com.example.vetcare_android_kotlin_compose_mvvm.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.example.vetcare_android_kotlin_compose_mvvm.ui.theme.*

/**
 * Campo de texto estilizado de VetCare
 */
@Composable
fun VetCareTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            placeholder = placeholder?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            isError = isError,
            singleLine = singleLine,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            visualTransformation = visualTransformation,
            shape = RoundedCornerShape(VetCareShapeTokens.ChipRadius),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = VetCareColors.Primary,
                unfocusedBorderColor = VetCareColors.Divider,
                focusedLabelColor = VetCareColors.Primary,
                unfocusedLabelColor = VetCareColors.MutedText,
                cursorColor = VetCareColors.Primary,
                errorBorderColor = VetCareColors.Danger,
                errorLabelColor = VetCareColors.Danger,
                focusedContainerColor = VetCareColors.Surface,
                unfocusedContainerColor = VetCareColors.Surface
            )
        )

        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.labelSmall,
                color = VetCareColors.Danger,
                modifier = Modifier.padding(start = VetCareSpacing.md, top = VetCareSpacing.xxs)
            )
        }
    }
}

/**
 * Campo de contraseña con toggle de visibilidad
 */
@Composable
fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {}
) {
    var passwordVisible by remember { mutableStateOf(false) }

    VetCareTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        modifier = modifier,
        enabled = enabled,
        isError = isError,
        errorMessage = errorMessage,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(
            onDone = { onImeAction() }
        ),
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                    tint = VetCareColors.MutedText
                )
            }
        }
    )
}

/**
 * Campo de email con validación visual
 */
@Composable
fun EmailField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Email",
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {}
) {
    VetCareTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        modifier = modifier,
        enabled = enabled,
        isError = isError,
        errorMessage = errorMessage,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(
            onNext = { onImeAction() }
        )
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFEEF1F7)
@Composable
private fun VetCareTextFieldPreview() {
    VetCareTheme {
        Column(
            modifier = Modifier.padding(VetCareSpacing.md),
            verticalArrangement = Arrangement.spacedBy(VetCareSpacing.md)
        ) {
            VetCareTextField(
                value = "",
                onValueChange = {},
                label = "Nombre"
            )
            VetCareTextField(
                value = "test@email.com",
                onValueChange = {},
                label = "Email"
            )
            VetCareTextField(
                value = "",
                onValueChange = {},
                label = "Campo con error",
                isError = true,
                errorMessage = "Este campo es requerido"
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFEEF1F7)
@Composable
private fun PasswordFieldPreview() {
    VetCareTheme {
        Column(
            modifier = Modifier.padding(VetCareSpacing.md),
            verticalArrangement = Arrangement.spacedBy(VetCareSpacing.md)
        ) {
            PasswordField(
                value = "123456",
                onValueChange = {},
                label = "Contraseña"
            )
        }
    }
}

