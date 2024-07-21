
# FooTube

![FooTube Icon](app/src/main/res/drawable/logodark.png)

FooTube is a video sharing platform designed to provide a user-friendly experience for uploading, viewing, and interacting with videos. With features such as user authentication, video uploads, comments, likes, and dislikes, FooTube aims to create a seamless and enjoyable experience for all users.

## Features

- **User Authentication**: Secure sign-in and sign-up processes.
- **Video Uploads**: Upload and share your videos with the FooTube community.
- **Comment System**: Engage with videos by leaving comments.
- **Like and Dislike**: Express your opinions on videos.
- **Dark Mode**: Switch between light and dark themes for a comfortable viewing experience.
## Notice
- Wipe the data on the MongoDB and on the phone.
- Change the base Url in the "Strings" to your computer's IP or if you are using an emulator change to 10.0.2.2. Do this changes also to the  "@xml/network_security_config".
## Usage
### Sign Up:

- Open the FooTube app.
- Click on the "Sign Up" button.
- Fill in the required details and create your account.
### Sign In:

- Open the FooTube app.
- Click on the "Sign In" button.
- Enter your email and password to log in.
### Upload Video:

- Click on the plus icon in the bottom menu.
- Enter the video title, description, and select a thumbnail.
- Choose the video file from your device and click "Upload".
- Refresh the page to see the video on the home page.
- you can edit and delete yout own comments.

### View Videos:

- Browse through the video feed on the home screen.
- Click on any video to start playing it.
### Like and Dislike Videos:

- While watching a video, click the thumbs up (like) or thumbs down (dislike) button to express your opinion.
### Comment on Videos:

- Scroll down to the comment section below the video.
- Enter your comment in the input box and click "Add comment".
- you can edit and delete yout own comments.
### Dark Mode:

- Access the side navigation drawer.
- Press the "Night Mode" button for dark mode to enable or disable it.

## Running the Client-side (Android App)
Connect an Android Device or Start an Emulator:
Connect your Android device via USB, or start an Android emulator from AVD Manager in Android Studio.

Note that for now, the app is compatible with an emulator. If you are interested in using the app on a real phone, you need to change the "baseUrl" in the "Strings" file and the XML file "network_security_config", change it to your computer's IP.

## Run the App:
Click the Run button in Android Studio.
Grant Necessary Permissions:
Ensure the app has the necessary permissions to access the internet and read/write to storage.
