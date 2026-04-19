# Canopy – User Manual

## 1. Introduction
Canopy is a mobile application designed to visualize commuting-related carbon emissions.

* The application collects commuting data **automatically or manually**.
* It displays the collective environmental impact through a **shared virtual tree**.
* **Goal:** To encourage users to adopt more sustainable commuting habits by visualizing their impact in an engaging way.

---

## 2. System Requirements
To use the Canopy application, the following requirements must be met:

* **Device:** Android smartphone/emulator
* **OS Version:** Android 10 or newer (recommended)
* **Settings:** Location services enabled

---

## 3. Installation
1.  **Install** Android Studio.
2.  **Clone the project repository:**
    `git clone https://github.com/mangomansikka/Carbon-footprint`
3.  **Open** the project in Android Studio.
4.  **Connect** an Android device or start an emulator.
5.  **Run** the application using the **Run ▶** button.
6.  The application will then install on the device.

---

## 4. Application Overview
The main purpose of the application is to track commuting distance and estimate the environmental impact of the user's travel.

**Main features:**
* Automatic trip detection
* Distance tracking
* Transportation type detection
* Emission statistics
* Virtual tree representing your environmental impact

---

## 5. Using the Application

### 5.1 Starting Location Tracking
1. Open the application.
   
<img width="410" height="863" alt="Screenshot 2026-04-19 175336" src="https://github.com/user-attachments/assets/51788783-6e8b-4e84-a6f9-bbcdbb941a13" />

2. Press the **green plus button** in the middle to open the tracking page.
3. To start your trip press the **Start Trip** button.
   
<img width="409" height="862" alt="Screenshot 2026-04-19 175354" src="https://github.com/user-attachments/assets/8cf4fee7-9074-454f-b9b4-886e5bf29d83" />

While tracking is active, the application calculates:
* Movement distance
* Transportation type
* Estimated carbon emissions

### 5.2 Stopping Tracking
1. Press the **End trip** button.

<img width="409" height="863" alt="Screenshot 2026-04-19 175713" src="https://github.com/user-attachments/assets/603af68d-a769-46a3-9195-c3fce61aab52" />

2. The trip will be saved and you will see your trips statistics.

### 5.3 Manual trip entry
The application also allows manual trip entry, to do this:
1. Press the **Add Manual Entry** in the tracking page

<img width="409" height="861" alt="image" src="https://github.com/user-attachments/assets/af7920b4-9c66-4602-8836-0138b4fc321a" />

2. Select your transport mode and input your trips lenght as meters
3. Press **Save trip** and your trip will be saved

<img width="407" height="863" alt="Screenshot 2026-04-19 180512" src="https://github.com/user-attachments/assets/10ba5eed-814c-47b0-be71-cd2ff3250a47" />

### 5.4 Viewing Environmental Impact
The application visualizes commuting impact through:
* **Interactive Tree:** to see your Interactive tree that reacts to your emissions, press the **leaf button** in the bottom bar

<img width="407" height="862" alt="image" src="https://github.com/user-attachments/assets/7ed5edad-507e-4786-880f-9a3aed92176b" />

* **Diagrams:** to see your emissions per vehicle type, press the **Trophy button** in the bottom bar

<img width="410" height="863" alt="Screenshot 2026-04-19 183430" src="https://github.com/user-attachments/assets/85a72449-e6a6-4b58-b95c-0ea8f9998c0b" />

* **Graphs:** to see your monthly/yearly emissions, press the **Graphs button** in the bottom bar

<img width="407" height="861" alt="image" src="https://github.com/user-attachments/assets/5eeb8826-0748-4a5d-9399-e08aab14a71f" />

---

## 6. Exporting data
The application allows data to be exported to a csv file, to do this:
1. Open the Diagram page by pressing the **Trophy button**.

<img width="410" height="863" alt="image" src="https://github.com/user-attachments/assets/28a61acd-3be1-4d37-885b-9d82ccae5dd5" />

2. Press the **Export Data to CSV** button.

<img width="411" height="853" alt="image" src="https://github.com/user-attachments/assets/372c4cbc-ef3e-40e5-821d-9b4131200339" />

3. This will allow you to share a csv file with your data through email or your preferred way.

---

## 7. Permissions
The application requires the following permissions:
* **Location permission:** Used to track movement distance and detect trips.

<img width="410" height="861" alt="Screenshot 2026-04-19 175417" src="https://github.com/user-attachments/assets/cdd3ec38-dd09-440a-89d4-d52d1b9a8a02" />

* **Activity permission:** Used to detect motion type (walking, cycling).

<img width="409" height="863" alt="Screenshot 2026-04-19 175431" src="https://github.com/user-attachments/assets/253793ff-cd7b-4324-8589-1c07fc6a1cf2" /> 

> **Note:** Without location and activity permissions, the application cannot automatically detect commuting.

---

## 8. Troubleshooting

#### Location tracking does not start
Make sure that:
* Location services are enabled on the device.
* The application has permission to access location data.

#### Distance is not detected
* Ensure that location tracking has been started.
* Ensure that the device has a GPS signal.

---

## 9. Future Improvements
The application is still under development. Future versions may include:
* Authentication (Login and sign up)
* Improved trip detection
* Data comparison to others
* Expanded visualization features

---

## 10. Contact
This project was developed as part of:
**Metropolia University of Applied Sciences** – *Multidisciplinary Innovation Project Course*.
