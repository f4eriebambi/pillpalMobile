package com.example.pillpalmobile

import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pillpalmobile.data.AuthStore
import com.example.pillpalmobile.model.RegisterRequest
import com.example.pillpalmobile.network.RetrofitClient
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.launch


// https://developer.android.com/develop/ui/views/components/pickers
// https://www.geeksforgeeks.org/kotlin/datepicker-in-kotlin/
// https://developer.android.com/develop/ui/views/components/dialogs
// https://www.slingacademy.com/article/using-regex-to-validate-email-addresses-in-kotlin/
// https://www.baeldung.com/kotlin/password-validation
// https://www.youtube.com/watch?v=v8tBXEx08Ns


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAccountScreen(
    onNavigateToLogin: () -> Unit,
    onAccountCreated: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()
    var name by remember { mutableStateOf("") }
    var birthday by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    // birthday date picker state
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

//    var showSuccessDialog by remember { mutableStateOf(false) }

    // form fields validation
    val nameRegex = Regex("^[a-zA-Z][a-zA-Z'\\-\\s]{0,14}$")
    val isNameValid = nameRegex.matches(name)
    val emailRegex = Patterns.EMAIL_ADDRESS
    val isEmailValid = emailRegex.matcher(email).matches()
    val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}$")
    val isPasswordValid = passwordRegex.matches(password)
    fun calculateAge(birthday: String): Int {
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val birthDate = format.parse(birthday)
        val today = Calendar.getInstance()
        val dob = Calendar.getInstance().apply { time = birthDate }
        var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) age--
        return age
    }
    val isOldEnough = birthday.isNotBlank() && calculateAge(birthday) >= 17



    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
//                .verticalScroll(scrollState)
                .padding(24.dp)
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(24.dp))

            Image(
                painter = painterResource(R.drawable.pillpal_team),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Welcome to",
                fontSize = 38.sp,
                fontFamily = Montserrat,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(1.dp))

            Text(
                text = "PillPal",
                fontSize = 38.sp,
                fontFamily = Montserrat,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 390.dp)
                    .background(
                        color = Color(0xFFFFFEF5),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = Color(0xFF918C84),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(scrollState)
                        .background(Color.White)
                        .fillMaxWidth()
                        .border(
                            width = 0.5.dp,
                            color = Color.Black,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(top = 24.dp, bottom = 24.dp),
                ) {
                    // user info
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 22.dp)
//                    .padding(vertical = 16.dp)
                    ) {
                        Text(
                            text = "* means required",
                            fontSize = 16.sp,
                            color = Color(0xFF4A4A4A),
                            fontFamily = Montserrat,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )

                        // enter name
                        Text(
                            text = "Name *",
                            fontSize = 20.sp,
                            fontFamily = Montserrat,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            isError = name.isNotBlank() && !isNameValid,
                            placeholder = {
                                Text(
                                    "Name",
                                    color = Color(0xFF828282),
                                    fontSize = 18.sp,
                                    fontFamily = Montserrat,
                                    fontWeight = FontWeight.Normal
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 0.5.dp,
                                    color = Color.Gray,
                                    shape = RoundedCornerShape(15.dp)
                                ),
                            singleLine = true,
                            shape = RoundedCornerShape(15.dp),
                            textStyle = LocalTextStyle.current.copy(
                                fontSize = 18.sp,
                                fontFamily = Montserrat,
                                fontWeight = FontWeight.Medium,
                                color = if (name.isNotEmpty()) Color.Black else Color(0xFF828282)
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFACBD6F),
                                unfocusedBorderColor = Color.Gray
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                        )
                        if (name.isNotBlank() && !isNameValid) {
                            Text(
                                text = "Use 1-15 letters, spaces, ' or -",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // enter email
                        Text(
                            text = "Email address *",
                            fontSize = 20.sp,
                            fontFamily = Montserrat,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            isError = email.isNotBlank() && !isEmailValid,
                            placeholder = {
                                Text(
                                    "Email address",
                                    color = Color(0xFF828282),
                                    fontSize = 18.sp,
                                    fontFamily = Montserrat,
                                    fontWeight = FontWeight.Normal
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 0.5.dp,
                                    color = Color.Gray,
                                    shape = RoundedCornerShape(15.dp)
                                ),
                            singleLine = true,
                            shape = RoundedCornerShape(15.dp),
                            textStyle = LocalTextStyle.current.copy(
                                fontSize = 18.sp,
                                fontFamily = Montserrat,
                                fontWeight = FontWeight.Medium,
                                color = if (email.isNotEmpty()) Color.Black else Color(0xFF828282)
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFACBD6F),
                                unfocusedBorderColor = Color.Gray
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                        )
                        if (email.isNotBlank() && !isEmailValid) {
                            Text(
                                text = "Please enter a valid email address",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // enter birthday
                        Text(
                            text = "Date of birth *",
                            fontSize = 20.sp,
                            fontFamily = Montserrat,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )

                        OutlinedTextField(
                            value = birthday,
                            onValueChange = { },
                            isError = birthday.isNotBlank() && !isOldEnough,
                            readOnly = true,
                            placeholder = {
                                Text(
                                    "dd/mm/yyyy",
                                    color = Color(0xFF828282),
                                    fontSize = 18.sp,
                                    fontFamily = Montserrat,
                                    fontWeight = FontWeight.Normal
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { showDatePicker = true }) {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = "Select date",
                                        tint = Color.Black
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 0.5.dp,
                                    color = Color.Gray,
                                    shape = RoundedCornerShape(15.dp)
                                )
                                .clickable { showDatePicker = true },
                            singleLine = true,
                            shape = RoundedCornerShape(15.dp),
                            textStyle = LocalTextStyle.current.copy(
                                fontSize = 18.sp,
                                fontFamily = Montserrat,
                                fontWeight = FontWeight.Medium,
                                color = if (birthday.isNotEmpty()) Color.Black else Color(0xFF828282)
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFACBD6F),
                                unfocusedBorderColor = Color.Gray,
                                disabledBorderColor = Color.Gray,
                                disabledTextColor = Color.Black
                            )
                        )
                        if (birthday.isNotBlank() && !isOldEnough) {
                            Text(
                                text = "You must be at least 17 years old to sign up",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                            )
                        }

                        // date picker modal
                        if (showDatePicker) {
                            DatePickerDialog(
                                onDismissRequest = { showDatePicker = false },
                                confirmButton = {
                                    TextButton(
                                        onClick = {
                                            datePickerState.selectedDateMillis?.let { millis ->
                                                val dateFormat =
                                                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                                birthday = dateFormat.format(Date(millis))
                                            }
                                            showDatePicker = false
                                        }
                                    ) {
                                        Text("OK")
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showDatePicker = false }) {
                                        Text("Cancel")
                                    }
                                }
                            ) {
                                DatePicker(state = datePickerState)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // enter password
                        Text(
                            text = "Password *",
                            fontSize = 20.sp,
                            fontFamily = Montserrat,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            isError = password.isNotBlank() && !isPasswordValid,
                            placeholder = {
                                Text(
                                    "Create a pasword",
                                    color = Color(0xFF828282),
                                    fontSize = 18.sp,
                                    fontFamily = Montserrat,
                                    fontWeight = FontWeight.Normal
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 0.5.dp,
                                    color = Color.Gray,
                                    shape = RoundedCornerShape(15.dp)
                                ),
                            singleLine = true,
                            shape = RoundedCornerShape(15.dp),
                            textStyle = LocalTextStyle.current.copy(
                                fontSize = 18.sp,
                                fontFamily = Montserrat,
                                fontWeight = FontWeight.Medium,
                                color = if (password.isNotEmpty()) Color.Black else Color(0xFF828282)
                            ),
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        painter = painterResource(
                                            if (passwordVisible) R.drawable.password_show else R.drawable.password_hide
                                        ),
                                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFACBD6F),
                                unfocusedBorderColor = Color.Gray
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                        )
                        if (password.isNotBlank() && !isPasswordValid) {
                            Text(
                                text = "Password must be 8+ characters with uppercase, lowercase, number, and symbol",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // enter password again
                        Text(
                            text = "Confirm Password *",
                            fontSize = 20.sp,
                            fontFamily = Montserrat,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )

                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            placeholder = {
                                Text(
                                    "Confirm Password",
                                    color = Color(0xFF828282),
                                    fontSize = 18.sp,
                                    fontFamily = Montserrat,
                                    fontWeight = FontWeight.Normal
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 0.5.dp,
                                    color = Color.Gray,
                                    shape = RoundedCornerShape(15.dp)
                                ),
                            singleLine = true,
                            shape = RoundedCornerShape(15.dp),
                            textStyle = LocalTextStyle.current.copy(
                                fontSize = 18.sp,
                                fontFamily = Montserrat,
                                fontWeight = FontWeight.Medium,
                                color = if (confirmPassword.isNotEmpty()) Color.Black else Color(0xFF828282)
                            ),
                            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                    Icon(
                                        painter = painterResource(
                                            if (confirmPasswordVisible) R.drawable.password_show else R.drawable.password_hide
                                        ),
                                        contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFACBD6F),
                                unfocusedBorderColor = Color.Gray
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            isError = confirmPassword.isNotEmpty() && password != confirmPassword
                        )

                        // error message if not a match
                        if (confirmPassword.isNotEmpty() && password != confirmPassword) {
                            Text(
                                text = "Passwords do not match",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, top = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
//                    .padding(horizontal = 22.dp)
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            println("Create Account Clicked!")

                            try {
                                val result = RetrofitClient.authService.register(
                                    RegisterRequest(
                                        email = email.trim(),
                                        password = password.trim(),
                                        full_name = name.trim(),
                                        birthday = birthday.trim()
                                    )
                                )

                                println("Response Code: ${result.code()}")

                                if (result.isSuccessful) {
                                    val token = result.body()?.token
                                    println("Signup Success!! Token: $token")

                                    if (token != null) {
                                        AuthStore.saveToken(context, token)
                                        onAccountCreated()
                                    } else {
                                        errorMessage = "Signup succeeded but token missing"
                                    }
                                } else {
                                    val errorBody = result.errorBody()?.string()
                                    println("Signup Failed: $errorBody")
                                    errorMessage = "Signup failed: ${result.code()}"
                                }

                            } catch (e: Exception) {
                                println("Network Error: ${e.localizedMessage}")
                                errorMessage = "Network error: ${e.localizedMessage}"
                            }
                        }
                    },
                    enabled = isNameValid && isEmailValid && isPasswordValid && isOldEnough &&
                            confirmPassword == password,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .border(
                            width = 2.dp,
                            color = if (isNameValid && isEmailValid && isPasswordValid && isOldEnough && confirmPassword == password)
                                Color(0xFFCBCBE7)
                            else
                                Color(0xFFCBCBE7).copy(alpha = 0.5f),
                            shape = RoundedCornerShape(15.dp)
                        ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF595880),
                        disabledContainerColor = Color(0xFF595880),
                        disabledContentColor = Color.White
                    ),
                    shape = RoundedCornerShape(15.dp)
                ) {
                    Text(
                        text = "Create Account",
                        fontSize = 24.sp,
                        color = Color.White,
                        fontFamily = Montserrat,
                        fontWeight = FontWeight.SemiBold
                    )
                }


            }
            // we can work on email verification later as an extra if we have time but i dont feel we need it rn
//            if (showSuccessDialog) {
//                AlertDialog(
//                    onDismissRequest = { showSuccessDialog = false },
//                    confirmButton = {
//                        Box(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(bottom = 8.dp),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            TextButton(
//                                onClick = { showSuccessDialog = false },
//                                modifier = Modifier
//                                    .border(
//                                        width = 1.dp,
//                                        color = Color.Black,
//                                        shape = RoundedCornerShape(15.dp)
//                                    )
//                                    .background(Color.White, RoundedCornerShape(15.dp))
//                            ) {
//                                Text(
//                                    text = "ok",
//                                    fontFamily = Montserrat,
//                                    fontWeight = FontWeight.SemiBold,
//                                    fontSize = 20.sp
//                                )
//                            }
//                        }
//                    },
//                    title = {
//                        Box(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(top = 12.dp),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Text(
//                                text = "Account Created!",
//                                fontFamily = Montserrat,
//                                fontWeight = FontWeight.SemiBold,
//                                textAlign = TextAlign.Center,
//                                fontSize = 22.sp,
//                                modifier = Modifier.padding(top = 12.dp)
//                            )
//                        }
//                    },
//                    text = {
//                        Box(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(top = 12.dp),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Text(
//                                text = "Verification email sent to\n$email",
//                                textAlign = TextAlign.Center,
//                                fontFamily = Montserrat,
//                                fontWeight = FontWeight.Medium,
//                                fontSize = 22.sp,
//                                modifier = Modifier.fillMaxWidth()
//                            )
//                        }
//                    }
//                )
//            }

            Row(
                modifier = Modifier.padding(bottom = 16.dp).background(Color.White),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Already have an account? ",
                    fontSize = 14.sp,
                    color = Color.Black,
                    fontFamily = Inter,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Log In",
                    fontSize = 14.sp,
                    color = Color.Black,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { onNavigateToLogin() },
                    fontFamily = Inter,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.width(16.dp))
            }
        }
    }
}