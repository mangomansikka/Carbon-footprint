# Canopy – User Manual

## 1. Introduction
Canopy is a mobile application designed to visualize commuting-related carbon emissions.

* The application collects commuting data **automatically or manually**.
* It displays your environmental impact through a virtual tree and different graphs.
* **Goal:** To encourage users to adopt more sustainable commuting habits by visualizing their impact in an engaging way.

---

## 2. System Requirements
To use the Canopy application, the following requirements must be met:

* **Device:** Android smartphone
* **OS Version:** Android 10 or newer (recommended)
* **Settings:** Location services enabled
* **Permissions:** Location permission and activity recognition permission are required for automatic tracking

---

## 3. Installation
1.  **Install** Android Studio.
2.  **Clone the project repository:**
    `git clone https://github.com/mangomansikka/Carbon-footprint`
3.  **Open** the project in Android Studio.
4.  **Connect** an Android device
5.  **Run** the application using the **Run ▶** button.
6.  The application will then install on the device.

---

## 4. Application Overview
The main purpose of the application is to track commuting distance and estimate the environmental impact of the user's travel.

**Main features:**
* Automatic trip detection
* Manual trip input
* Distance tracking
* Transportation type detection
* Emission statistics
* Virtual tree representing your environmental impact

---

## 5. Using the Application

### 5.1 Starting Location Tracking
1. Open the application.
   
<img width="400" height="850" alt="Screenshot 2026-05-03 184043" src="https://github.com/user-attachments/assets/418ebd91-48fb-4f54-8b29-8a966defa5e9" />

2. Press the **green plus button** in the middle to open the tracking page.
3. To start your trip press the **Start Trip** button.
   
<img width="400" height="850" alt="Screenshot 2026-05-03 184109" src="https://github.com/user-attachments/assets/d54d0bf6-59e7-46d1-8039-0ca33c861c37" />

While tracking is active, the application calculates:
* Movement distance
* Transportation type
* Estimated carbon emissions

### 5.2 Stopping Tracking
1. Press the **End trip** button.

<img width="400" height="850" alt="Screenshot 2026-05-03 184731" src="https://github.com/user-attachments/assets/6d90fe86-9dba-4608-a305-f1c9ad9c7090" />

2. The trip will be saved and you will see your trips statistics.

### 5.3 Manual trip entry
The application also allows manual trip entry, to do this:
1. Press the **Add Manual Entry** in the tracking page

<img width="400" height="850" alt="Screenshot 2026-05-03 184745" src="https://github.com/user-attachments/assets/d47ef9d7-545b-4e1b-b60a-03e58d4e30cf" />

2. Select your transport mode and input your trips lenght as kilometers
3. Select the date of your trip and which campus the trip gets assigned to.
> **Note:** If a trip is campus to campus, assign the end campus. If it is a trip home from campus, assign that campus.
3. Press **Save trip** and your trip will be saved

<img width="400" height="850" alt="Screenshot 2026-05-03 185126" src="https://github.com/user-attachments/assets/3d64a21c-18e2-44a6-b0d9-5c77908040ef" />

### 5.4 Viewing Environmental Impact
The application visualizes commuting impact through:
* **Interactive Tree:** to see your Interactive tree that reacts to your emissions, press the **leaf button** in the bottom bar

<img width="400" height="850" alt="Screenshot 2026-05-03 191558" src="https://github.com/user-attachments/assets/6cb381e4-761c-4981-b775-18f5d60ed3ff" />

* **Diagrams:** to see your emissions per vehicle type, press the **Pie chart button** in the bottom bar

<img width="400" height="850" alt="Screenshot 2026-05-03 185219" src="https://github.com/user-attachments/assets/158c5da9-7d40-4b4c-a7e7-3243e2fb26bb" />

* **Graphs:** to see your monthly/yearly emissions, press the **Graphs button** in the bottom bar

<img width="400" height="850" alt="Screenshot 2026-05-03 191434" src="https://github.com/user-attachments/assets/8f60e0f7-5609-4b08-a94f-b44fe06c2941" />

---

## 6. Deletion of trips
The application allows trips to be deleted, to do this:
1. Open the calendar page by pressing the **Graphs button** in the bottom bar, and then pressing the **Calendar button**

<img width="400" height="850" alt="Screenshot 2026-05-03 195514" src="https://github.com/user-attachments/assets/a67e1620-b106-492f-b1ba-1e4a7eac9b50" />

2. Press the date where you want the trip to be deleted from
3. Press the red delete button to delete the trip

<img width="400" height="850" alt="Screenshot 2026-05-03 191937" src="https://github.com/user-attachments/assets/944618b9-e86e-4e1c-862a-48a047c6506e" />

> **Note:** Make sure to delete any unwanted trips before exporting data.

---

## 7. Exporting data
The application allows data to be exported to a csv file, to do this:

<img width="400" height="850" alt="Screenshot 2026-05-03 184053" src="https://github.com/user-attachments/assets/c59ad0ac-b3f9-446f-b5a5-8540f047dfb9" />


> **Note:** If you are a member of staff, make sure to change your role in the Home page here before exporting data.

1. Open the Diagram page by pressing the **Pie chart button**.

<img width="400" height="850" alt="Screenshot 2026-05-03 185219" src="https://github.com/user-attachments/assets/28f1c959-5e51-4513-b87e-e0fdbdcc8127" />

2. Press the **Export Data to CSV** button.

<img width="400" height="850" alt="Screenshot 2026-05-03 191131" src="https://github.com/user-attachments/assets/d56da3cd-f3d1-42d1-b4a8-b8c3b65db01e" />

3. This will allow you to share a csv file with your data through email or your preferred way.

---

## 8. Permissions
The application requires the following permissions:
* **Location permission:** Used to track movement distance and detect trips.

<img width="400" height="850" alt="Screenshot 2026-04-19 175417" src="https://github.com/user-attachments/assets/cdd3ec38-dd09-440a-89d4-d52d1b9a8a02" />

* **Activity permission:** Used to detect motion type (walking, cycling).

<img width="400" height="850" alt="Screenshot 2026-04-19 175431" src="https://github.com/user-attachments/assets/253793ff-cd7b-4324-8589-1c07fc6a1cf2" /> 

> **Note:** Without location and activity permissions, the application cannot automatically detect commuting.

---

## 9. Troubleshooting

#### Location tracking does not start
Make sure that:
* Location services are enabled on the device.
* The application has permission to access location data.

#### Distance is not detected
* Ensure that location tracking has been started.
* Ensure that the device has a GPS signal.

---

## 10. Future Improvements
The application is still under development. Future versions may include:
* Authentication (Login and sign up)
* Improved trip detection
* Data comparison to others
* Expanded visualization features

---

## 11. Contact
This project was developed as part of:
**Metropolia University of Applied Sciences** – *Multidisciplinary Innovation Project Course*.
