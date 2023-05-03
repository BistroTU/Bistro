package edu.temple.bistro.ui.restaurant

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.temple.bistro.ui.theme.BistroTheme
import edu.temple.bistro.ui.theme.Inter

@Composable
fun CategoryChip(category: String, selected: Boolean?, onClick: () -> Unit) {
    var backgroundColor = Color.White
    if (selected != null && selected) {
        backgroundColor = Color.Gray
    }

    Box(
        Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .padding(10.dp, 2.dp)
            .clickable(onClick = onClick)
    ) {
        Text(text = category.lowercase(), fontSize = 15.sp, fontFamily = Inter, fontWeight = FontWeight.Normal)
    }
}

@Composable
@Preview
fun CategoryChipPreview(){
    CategoryChip("chicken", true, {})
}