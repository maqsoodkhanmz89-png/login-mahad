# Prevent Duplicate Email and Username in Account Creation

This plan aims to prevent users from creating multiple accounts with the same email or username by enforcing uniqueness constraints in the database and adding validation logic in the sign-up process.

## Proposed Changes

### [Data Layer]

#### [MODIFY] [User.kt](file:///Users/apple/Documents/Project/LoginScreen/app/src/main/java/com/mahad/login/data/User.kt)
- Add unique indices for `username` and `email` fields in the `@Entity` annotation.

#### [MODIFY] [UserDao.kt](file:///Users/apple/Documents/Project/LoginScreen/app/src/main/java/com/mahad/login/data/UserDao.kt)
- Add `getUserByUsername(username: String): User?` method.

#### [MODIFY] [AppDatabase.kt](file:///Users/apple/Documents/Project/LoginScreen/app/src/main/java/com/mahad/login/data/AppDatabase.kt)
- Increment database version.
- Add `fallbackToDestructiveMigration()` to the database builder to handle schema changes easily during development.

### [UI Layer]

#### [MODIFY] [SignUpActivity.kt](file:///Users/apple/Documents/Project/LoginScreen/app/src/main/java/com/mahad/login/SignUpActivity.kt)
- Update the sign-up logic to check if a user with the provided email or username already exists before performing the insertion.
- Display a `Toast` message informing the user if the email or username is already taken.

## Verification Plan

### Manual Verification
- Attempt to create an account with an existing email. Verify that an error message is shown and the account is not created.
- Attempt to create an account with an existing username. Verify that an error message is shown and the account is not created.
- Create an account with a unique email and username. Verify that the account is created successfully.
