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
   
<img width="406" height="912" alt="Screenshot 2026-03-26 192109" src="https://github.com/user-attachments/assets/ceb6870c-5a26-48eb-bfa9-61f4bee368b9" />

2. Press the **Start tracking** button or the **green button** in the middle to open the tracking page.
3. To start your trip press the **Start Trip** button.
   
<img width="406" height="909" alt="Screenshot 2026-03-26 192157" src="https://github.com/user-attachments/assets/a788faa1-c57b-4c86-bd8c-5d370b7dfba3" />

While tracking is active, the application calculates:
* Movement distance
* Transportation type
* Estimated carbon emissions

### 5.2 Stopping Tracking
1. Press the **End trip** button.

<img width="405" height="909" alt="Screenshot 2026-03-26 192225" src="https://github.com/user-attachments/assets/dadca799-6575-4719-b07a-80bb8cd73bcf" />

2. The trip will be saved and you will see your trips statistics.

<img width="409" height="910" alt="Screenshot 2026-03-26 192244" src="https://github.com/user-attachments/assets/830b4231-feeb-4e0f-9017-02d352a4aee2" />

### 5.3 Viewing Environmental Impact
The application visualizes commuting impact through:
* **Diagrams:** to see your emissions per vehicle type, press the **trophy button** in the bottom bar

<img width="407" height="911" alt="Screenshot 2026-03-26 192128" src="https://github.com/user-attachments/assets/8de1e739-d212-4d29-8a22-15369a97f63f" />

* **Graphs:** to see your monthly/yearly emissions, press the **Graphs button** in the bottom bar

<img width="410" height="910" alt="Screenshot 2026-03-26 192258" src="https://github.com/user-attachments/assets/24625a0d-2be7-432f-8b56-4f3b4366227a" />

---

## 6. Permissions
The application requires the following permissions:
* **Location permission:** Used to track movement distance and detect trips.
> **Note:** Without location permission, the application cannot automatically detect commuting.

---

## 7. Troubleshooting

#### Location tracking does not start
Make sure that:
* Location services are enabled on the device.
* The application has permission to access location data.

#### Distance is not detected
* Ensure that location tracking has been started.
* Ensure that the device has a GPS signal.

---

## 8. Future Improvements
The application is still under development. Future versions may include:
* Authentication (Login and sign up)
* Improved trip detection
* Data comparison to others
* Expanded visualization features

---

## 9. Contact
This project was developed as part of:
**Metropolia University of Applied Sciences** – *Multidisciplinary Innovation Project Course*.
