# NoteMind AI Frontend

## Authentication flow

The login and sign-up screen is implemented in
`app/src/main/java/com/dhaliwal/notemindai/feature/auth/ui/AuthActivity.kt`.

1. `AuthActivity` creates the `AuthViewModel` and displays `AuthScreen`.
2. `AuthScreen` collects the username and password and checks the basic form rules.
3. The screen calls either `login` or `signup` on the ViewModel.
4. `AuthViewModel` runs the network request without blocking the UI and exposes loading/error events.
5. `AuthRepository` sends the request through Retrofit and saves the returned access and refresh tokens.
6. On success, the activity opens `DashboardActivity`.

`AuthTextField.kt` contains the shared text field used by the username, password,
and confirm-password inputs. Password fields hide the characters while they are typed.