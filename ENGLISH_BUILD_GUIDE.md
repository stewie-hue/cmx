# FlymeBarSizer — English translation build

This source snapshot includes an English translation pass for the app interface and a GitHub Actions workflow to build a debug APK without a PC.

## Build using your phone and GitHub
1. Create a GitHub repository and upload the contents of this folder (not the parent folder).
2. Open the repository's **Actions** tab.
3. Choose **Build English FlymeBarSizer APK** and tap **Run workflow**. Pushes to `main` or `master` also start a build.
4. When the run finishes, open it and download the `FlymeBarSizer-English-debug` artifact.
5. Extract that artifact ZIP to get the APK.

## Important
- This is a debug APK, not a signed release build.
- Keep the original module APK so you can restore it if the translated build does not work.
- A differently signed build may not install over the original. Back up settings if possible, disable the module, uninstall the old app, install the new APK, then re-enable it.
- This translation is source-level and has not been runtime-tested on every Flyme/Android version.
