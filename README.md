# Cupboard

Manage your foods, recipes, and shopping lists all in one convenient app!

## Installation:

### Android Studio
Install [Android Studio.](https://developer.android.com/studio/index.html)

From the home screen or an open project, navigate to to settings:
**Home Screen:** Bottom right `Configure > Settings`.
**Open Project:** `File > Settings` or `CTRL + ALT + S`.

#### SDK Settings:
1. Navigate to `Appearance & Behavior > System Settings > Android SDK`.
2. Check off `Android API 27` and `Android 8.0 (Oreo)`.
3. Click **Apply**.
4. Navigate to the `SDK Tools` tab.
5. Make sure `Android SDK Build-Tools`, `Android Emulator`, `Android SDK Platform-Tools`, and `Android SDK Tools` are all checked and their status is "Installed".
6. If they aren't, check them and click **Apply**.

#### Editor Settings:
1. Navigate to `Editor > Code Style > Java`.
2. Navigate to the tab `Code Generation`.
3. For the Field Name Prefix, put `m`.
4. For the Static field name prefix, put `s`.
5. Click **Apply** and **OK**.

### Cupboard
1. Clone the repo: `git clone https://github.com/SCCapstone/Cupboard.git`
2. Open the folder in Android Studio.
3. Gradle should automatically sync. If you are missing SDKs it will prompt you to download them.

### Emulator
1. Once you have successfully built the project with no gradle errors, navigate to the AVD Manager (purple icon in the top right corner of Android Studio).
2. Click **Create Virtual Device**.
3. Make sure **Phone** is selected along with **Nexus 5X** and click **Next**.
4. Select **Oreo API Level 26** (you may need to download it).
5. *Give it an AVD Name (optional).*
6. Click **Finish**.
7. Launch the emulator by clicking the green play button.
8. With the emulator open, navigate to `Extended Controls (triple dots) > Google Play` and click **Update**.
9. Follow the steps to make sure google play is completely up to date.

### Running Unit Tests
1. In Android Studio, with the project open, navigate to Cupboard/app/src/test. This will contain all unit test files. Open one (ex. SignInActivityTest.java).
2. Next to each test, there is a green circle with a play button (next to the line numbers).
3. Click the icon to run the corresponding test, or click the one at the top next to `public class fileName` to run all of the tests in the file.
