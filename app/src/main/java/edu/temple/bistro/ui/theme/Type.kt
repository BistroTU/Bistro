package edu.temple.bistro.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import edu.temple.bistro.R


@OptIn(ExperimentalTextApi::class)
val Inter = FontFamily(
    Font(
        R.font.inter_variable,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(400),
            FontVariation.slant(0f)
        ),
        weight = FontWeight.Normal
    ),
    Font(
        R.font.inter_variable,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(500),
            FontVariation.slant(0f),
        ),
        weight = FontWeight.Medium
    ),
    Font(
        R.font.inter_variable,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(700),
            FontVariation.slant(0f),
        ),
        weight = FontWeight.Bold
    ),

)


// Set of Material typography styles to start with
val Typography = Typography(
    h1 = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),
    body1 = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),

    /* Other default text styles to override
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
    */
)