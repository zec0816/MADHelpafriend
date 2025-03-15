# Help a Friend 🚀

## Introduction
**Help a Friend** is a mobile application designed to assist persons with disabilities (**OKU**) by connecting them with volunteers for mobility support, emergency help, and community engagement. The app includes real-time location tracking, forums, emergency hotlines, and accessibility features.

The backend for this project is available here:  
[Help a Friend Backend](https://github.com/zec0816/helpafriend)

---

## Features ✨
- **User Authentication:** Signup, login, reset password, logout, and delete account.
- **Real-time Assistance Requests:** OKU users can request help, and volunteers can accept requests.
- **Live Location Tracking:** Uses GPS to match OKU users with nearby volunteers.
- **Community Forum:** Users can post discussions, comment, and like posts.
- **Emergency Hotline:** Quick access to emergency services and nearby emergency centers.
- **User Profile & Settings:** Customize profile, enable dark mode, and adjust accessibility settings.

---

## Setup Instructions ⚙️

### 1. Clone the Repository
```sh
git clone https://github.com/zectest0816/MADPracticalSubmission.git
cd MADPracticalSubmission
```

### 2. Obtain a Google Maps API Key 
The app requires a **Google Maps API key** for location tracking.

#### Steps to get the API key:
1. Go to [Google Cloud Console](https://console.cloud.google.com/).
2. Create a new project or select an existing one.
3. Navigate to **APIs & Services** > **Credentials**.
4. Click **Create Credentials** > **API Key**.
5. Enable the following APIs:
   - Maps SDK for Android
   - Places API
   - Geolocation API
6. Copy the generated API key.
7. Open `AndroidManifest.xml` and replace `YOUR_API_KEY_HERE` with your API key:
   ```xml
   <meta-data
       android:name="com.google.android.geo.API_KEY"
       android:value="YOUR_API_KEY_HERE"/>
   ```

---

### 3. Set Up MySQL Database (XAMPP) 
The app connects to a MySQL database hosted on XAMPP.

#### Steps to configure MySQL:
1. Download and install **XAMPP** from [https://www.apachefriends.org/](https://www.apachefriends.org/).
2. Start **Apache** and **MySQL** from the XAMPP Control Panel.
3. Open `phpMyAdmin` (`http://localhost/phpmyadmin`).
4. Create a new database, e.g., `helpafriend_db`.
5. Import the SQL file from the backend repository:
   ```sh
   mysql -u root -p helpafriend_db < helpafriend.sql
   ```
6. Retrieve your local IP address:
   - Open **Command Prompt** and type:
     ```sh
     ipconfig
     ```
   - Look for **IPv4 Address** under your active network connection.

7. Update `Db_Contract.java` with your local IP:
   ```java
   public static final String BASE_URL = "http://YOUR_IP_ADDRESS/helpafriend/";
   ```
   Replace `YOUR_IP_ADDRESS` with the IPv4 address from Step 6.

---

### 4. Run the App 
1. Open the project in **Android Studio**.
2. Connect an **Android device** (or use an emulator).
3. Build and run the app.
