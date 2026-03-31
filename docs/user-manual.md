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
   
<img width="408" height="911" alt="Screenshot 2026-03-31 153634" src="https://github.com/user-attachments/assets/3a6ce82f-0449-4d06-b911-523853c2f323" />

2. Press the **Start tracking** button or the **green button** in the middle to open the tracking page.
3. To start your trip press the **Start Trip** button.
   
<img width="409" height="910" alt="Screenshot 2026-03-31 154124" src="https://github.com/user-attachments/assets/b82125e0-6e85-4380-971c-04cf5e0fd5a5" />

While tracking is active, the application calculates:
* Movement distance
* Transportation type
* Estimated carbon emissions

### 5.2 Stopping Tracking
1. Press the **End trip** button.

<img width="410" height="911" alt="Screenshot 2026-03-31 154331" src="https://github.com/user-attachments/assets/0b710c8b-f7b1-4cc3-bd7a-fa9a4cbbbea1" />

2. The trip will be saved and you will see your trips statistics.

<img width="409" height="914" alt="Screenshot 2026-03-31 154343" src="https://github.com/user-attachments/assets/c5b34920-c9b0-4bad-af25-5a1689c91939" />

### 5.3 Manual trip entry
The application also allows manual trip entry, to do this:
1. Press the **Pen button** in the bottom bar

<img width="408" height="911" alt="Screenshot 2026-03-31 153119" src="https://github.com/user-attachments/assets/1187592d-3566-4394-8db4-88147da32809" />

2. Select your transport mode and input your trips lenght as meters
3. Press **Save trip** and your trip will be saved

<img width="409" height="911" alt="Screenshot 2026-03-31 153208" src="https://github.com/user-attachments/assets/c764e3d5-56b1-4359-8ed2-2e51dd6f72db" />


### 5.4 Viewing Environmental Impact
The application visualizes commuting impact through:
* **Diagrams:** to see your emissions per vehicle type, press the **Trophy button** in the bottom bar

<img width="408" height="912" alt="Screenshot 2026-03-31 153403" src="https://github.com/user-attachments/assets/708256a9-fccc-41a6-8b95-fc4e33b4dd46" />

* **Graphs:** to see your monthly/yearly emissions, press the **Graphs button** in the bottom bar

<img width="410" height="914" alt="Screenshot 2026-03-31 153430" src="https://github.com/user-attachments/assets/c3649ce7-adf5-4c78-94f6-2ab0585fccc8" />

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
