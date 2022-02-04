package com.zelgius.cropkeeper.ui.legacy

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.TextFieldDefaults.IconOpacity
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Card3(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(4.dp),
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentColor: Color = contentColorFor(backgroundColor),
    border: BorderStroke? = null,
    elevation: Dp = 1.dp,
    content: @Composable () -> Unit
) {
    Card(modifier, shape, backgroundColor, contentColor, border, elevation, content)
}

@Composable
fun OutlinedTextField3(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = androidx.compose.material.MaterialTheme.shapes.small,
    colors: TextFieldColors = outlinedTextFieldColors3()
) {
    OutlinedTextField(
        value,
        onValueChange,
        modifier,
        enabled,
        readOnly,
        textStyle,
        label,
        placeholder,
        leadingIcon,
        trailingIcon,
        isError,
        visualTransformation,
        keyboardOptions,
        keyboardActions,
        singleLine,
        maxLines,
        interactionSource,
        shape,
        colors
    )
}

@Composable
fun outlinedTextFieldColors3() = TextFieldDefaults.outlinedTextFieldColors(
    textColor = LocalContentColor.current,
    cursorColor = MaterialTheme.colorScheme.primary,
    errorBorderColor = MaterialTheme.colorScheme.error,
    errorCursorColor = MaterialTheme.colorScheme.error,
    focusedBorderColor =
    MaterialTheme.colorScheme.primary.copy(alpha = ContentAlpha.high),
    unfocusedBorderColor =
    MaterialTheme.colorScheme.onSurface.copy(alpha = ContentAlpha.disabled),
    leadingIconColor =
    MaterialTheme.colorScheme.onSurface.copy(alpha = IconOpacity),
    trailingIconColor =
    MaterialTheme.colorScheme.onSurface.copy(alpha = IconOpacity),
    errorTrailingIconColor = MaterialTheme.colorScheme.error,
    focusedLabelColor =
    MaterialTheme.colorScheme.primary.copy(alpha = ContentAlpha.high),
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(ContentAlpha.medium),
    errorLabelColor = MaterialTheme.colorScheme.error,
    placeholderColor = MaterialTheme.colorScheme.onSurface.copy(ContentAlpha.medium),
)