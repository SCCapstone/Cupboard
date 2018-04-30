# Cupboard

Manage your foods, recipes, and shopping lists all in one convenient app!

I couldn't tell you how many times I've forgotten which ingredients I have in my kitchen. When I go grocery shopping, I often accidentally buy something I may already have! And remember that cheese in the bottom drawer of the fridge? Me neither. I could have sworn that it had another week of life left in it. Yuck!

Cupboard plans to solve these problems, as well as provide other functionality to drastically improve your cooking and shopping experience. With the Cupboard app, you will be able to keep track of foods you already have in your pantry, when they expire, and more. Having your ingredients stored inside your phone, you can go shopping with the peace of mind that you will not buy foods you already own. It is Cupboard's mission to save you time and money both inside the kitchen and out.

{% include youtubePlayer.html id=page.youtubeId %}



## Tutorial

### Create an account

Open the navigation tray in the upper-left corner. Tapping the area at the top will lead you to the sign-in/account creation screen. Simply input your email and password to create an account!

<img src="https://i.imgur.com/Aro8eCg.png" alt="hi" style="inline" img align="left" width="200" />
 
<img src="https://i.imgur.com/eVrGHoD.png" alt="hi" style="inline" img align="center" width="200" />



### The home screen
 
The home screen displays your foods that are about to expire and the last lists you modified.
 
<img src="https://i.imgur.com/Jexeyyy.png" alt="hi" style="inline" width="200" />



### Adding items to your cupboard

Open the navigation tray once again in the upper-left corner and head over to cupboard. Clicking the red button at the bottom will lead you to adding a new food screen. Input a name, expiration date, quantity, category, and description for your food. Alternativly, scan in the barcode with the camera button in the upper-right corner to fill in the data for you! Change the quantity of each item as you use them by tapping on the number to the right of each.
 
<img src="https://i.imgur.com/g0g2kTd.png" alt="hi" style="inline" img align="left" width="200" />
 
<img src="https://i.imgur.com/GmxLyua.png" alt="hi" style="inline" img align="center" width="200" />



### Shopping lists

Use shopping lists to keep track of what you need at the store. Click the red button at the bottom to create a new shopping list. Enter each of your items on your list, pressing enter between each one. Once you've bought the item, simply check it off by tapping on the buttons on the left.

<img src="https://i.imgur.com/imGhgkh.png" alt="hi" style="inline" img align="left"  width="200" />
 
<img src="https://i.imgur.com/bxQ1jmW.png" alt="hi" style="inline" img align="center" width="200" />



## About

Meet the Devs!

[Kyle Carhart](https://github.com/KMCGamer) |
[Will Wells](https://github.com/WillWells)

[Jacob Strom](https://github.com/jmstrom) |
[Bradley McClain](https://github.com/BradleyMcClain)

[Zak Slater](https://github.com/ZakSlater)



## Installation

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
5. Uncheck `Line comment at first column`.
6. Check `Add a space at comment start`.
7. Uncheck `Block comment at first column`.
8. CLick **Apply**.
9. Navigate to `Wrapping and Braces` tab.
10. Uncheck `Comment at first column`.
11. Check `Simple methods in one line`.
12. Click **Apply** and **OK**.

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

## Something broken?
Try some of these things:
1. Gradle clean script: `Gradle > :app > Tasks > build > clean`.
2. Sync gradle: `Tools > Android > Sync Project with Gradle Files`.

### Running Unit Tests
1. In Android Studio, with the project open, navigate to Cupboard/app/src/test. This will contain all unit test files. Open one (ex. SignInActivityTest.java).
2. Next to each test, there is a green circle with a play button (next to the line numbers).
3. Click the icon to run the corresponding test, or click the one at the top next to `public class fileName` to run all of the tests in the file.

### Running Instrumented Tests
1. In Android Studio, with the project open, navigate to Cupboard/app/java/com.thecupboardapp.cupboard (androidTest)
2. After building and syncing gradle, run each individual test by clicking the play button.
3. A successful test will yeild a full green bar


## Stetho for SQLite Database Viewing
1. Open Chrome and navigate to <a href="chrome://inspect/#devices" target="_blank">chrome://inspect/#devices</a>.
2. Click **inspect** for `com.thecupboardapp.cupboard`.
3. Navigate to **Resources** tab.
4. Navigate to **Web SQL** and find db file.

More info here: [Stetho](http://facebook.github.io/stetho/)
