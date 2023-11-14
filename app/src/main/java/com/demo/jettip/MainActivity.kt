package com.demo.jettip

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableDoubleState
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.demo.jettip.components.InputField
import com.demo.jettip.ui.theme.JetTipTheme
import com.demo.jettip.util.calculateTotalPerPerson
import com.demo.jettip.util.calculateTotalTip
import com.demo.jettip.widgets.RoundedIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp{
                MainContent()
            }
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {
    JetTipTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            content()
        }
    }
}

@Composable
fun TopHeader(totalPerPerson: Double = 134.0){
    Surface(modifier = Modifier
        .fillMaxWidth()
        .padding(15.dp)
        .height(150.dp)
        .clip(shape = RoundedCornerShape(corner = CornerSize(12.dp))),
        color = Color(0XFFE9D7F7),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val total = "%.2f".format(totalPerPerson)

            Text(
                text = "Total Per Person",
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = "\$$total",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.ExtraBold,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainContent() {

    // split State
    val splitByState = remember {
        mutableIntStateOf(1)
    }



    val tipAmountState = remember {
        mutableDoubleStateOf(0.0)
    }


    val totalPerPersonState = remember {
        mutableDoubleStateOf(0.0)
    }

    BillForm(
        splitByState = splitByState,
        tipAmountState = tipAmountState,
        totalPerPersonState = totalPerPersonState
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BillForm(
    modifier: Modifier = Modifier,
    range: IntRange = 1..100,
    splitByState: MutableIntState,
    tipAmountState: MutableDoubleState,
    totalPerPersonState: MutableDoubleState,
    ) {
    val totalBillState = remember {
        mutableStateOf("")
    }

    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    // slider state
    val sliderPositionState = remember {
        mutableFloatStateOf(0f)
    }

    val tipPercentage = (sliderPositionState.floatValue * 100).toInt()



    Column {
        TopHeader(totalPerPerson = totalPerPersonState.doubleValue)
        Surface(
            modifier = modifier
                .padding(15.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(
                corner = CornerSize(
                    8.dp,
                )
            ),
            border = BorderStroke(
                width = 1.dp,
                color = Color.LightGray,
            )
        ) {
            Column(
                modifier = modifier
                    .padding(6.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                InputField(
                    valueState = totalBillState,
                    labelId = "Enter Bill",
                    enabled = true,
                    isSingleLine = true,
                    onAction = KeyboardActions{
                        if(!validState) return@KeyboardActions

                        keyboardController?.hide()
                    },
                    modifier = modifier.fillMaxWidth()
                )

                if(validState) {
                    // Split Row
                    Row(
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = "Split",
                            modifier = modifier
                                .align(alignment = Alignment.CenterVertically)
                        )
                        Spacer(
                            modifier = modifier
                                .width(120.dp)
                        )
                        Row(
                            modifier = modifier
                                .padding(horizontal = 3.dp),
                            horizontalArrangement = Arrangement.End,
                        ){
                            RoundedIconButton(imageVector = Icons.Default.Remove, onClick = {
                                if(splitByState.intValue > 1){
                                    splitByState.intValue -=  1

                                    totalPerPersonState.doubleValue = calculateTotalPerPerson(
                                        totalBill = totalBillState.value.toDouble(),
                                        splitBy = splitByState.intValue,
                                        tipPercentage =  tipPercentage
                                    )
                                }
                            })
                            Text(
                                text = "${splitByState.intValue}",
                                modifier = modifier
                                    .align(alignment = Alignment.CenterVertically)
                                    .padding(start = 9.dp, end = 9.dp))
                            RoundedIconButton(imageVector = Icons.Default.Add, onClick = {
                                if(splitByState.intValue < range.last){
                                    splitByState.intValue +=  1

                                    totalPerPersonState.doubleValue = calculateTotalPerPerson(
                                        totalBill = totalBillState.value.toDouble(),
                                        splitBy = splitByState.intValue,
                                        tipPercentage =  tipPercentage
                                    )
                                }
                            })
                        }
                    }

                    // Tip Row
                    Row (
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = "Tip",
                            modifier = modifier.align(alignment = Alignment.CenterVertically)
                        )
                        Spacer(modifier = modifier.width(200.dp))
                        Text(
                            text = "$ ${tipAmountState.doubleValue}",
                            modifier = modifier.align(alignment = Alignment.CenterVertically)
                        )
                    }

                    //
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "$tipPercentage%")
                        Spacer(modifier = modifier.height(14.dp))

                        // Slider
                        Slider(
                            modifier = modifier.padding(start =16.dp, end = 16.dp),
                            value = sliderPositionState.floatValue,
                            onValueChange = { newVal ->
                                sliderPositionState.floatValue = newVal

                                val percentage = (newVal * 100).toInt()

                                tipAmountState.doubleValue = calculateTotalTip(totalBill = totalBillState.value.toDouble(), tipPercentage = percentage)

                                totalPerPersonState.doubleValue = calculateTotalPerPerson(
                                    totalBill = totalBillState.value.toDouble(),
                                    splitBy = splitByState.intValue,
                                    tipPercentage =  percentage
                                )
                            },
                            steps = 5,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    JetTipTheme {
        MyApp {
            MainContent()
        }
    }
}