# 🛰️ Doomsday Threat Monitor (NASA Asteroid Radar)

> *A real-time Android radar application built with Kotlin and Jetpack Compose that tracks near-Earth asteroids using live telemetry from NASA's NeoWs API.*

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/Language-Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" />
  <img src="https://img.shields.io/badge/UI-Jetpack_Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" />
  <img src="https://img.shields.io/badge/API-NASA_NeoWs-0B3D91?style=for-the-badge&logo=nasa&logoColor=white" />
</p>

---

## 🚀 About The Project

Most mobile dev portfolios feature a static list for NASA's Astronomy Picture of the Day. **Doomsday Radar** takes a different approach by building an immersive, cybernetic **Orbital Trajectory Map** that visualizes real-time asteroid telemetry streaming straight from NASA. 

The application features custom low-level mathematical canvas rendering, asynchronous networking via Retrofit, and dynamic threat analysis.

---

## ✨ Key Features

* **Real-Time NASA Integration:** Fetches live Near-Earth Objects (NEOs) dynamically for the current date using NASA's **NeoWs (Near Earth Object Web Service)** API.
* **Custom Sci-Fi Canvas HUD:** Built entirely using Jetpack Compose `Canvas`—features a continuously spinning radar sweep beam, concentric telemetry distance rings, and an off-center holographic Earth.
* **Curved Orbital Trajectory Paths:** Calculates elliptical trajectory arcs and hazard corridors (`drawArc`) to map out incoming flight paths instead of flat, boring target rings.
* **Fun Scale Comparative Generator:** Automatically translates raw metric dimensions into intuitive, playful scales (e.g., *~120m: "Bigger than a Football Stadium!"* or *~370m: "Taller than the Eiffel Tower!"*).
* **Collision Risk Analysis:** Real-time threat classification parsing safety corridors versus critical hazard zones.

---

## 🛠️ Tech Stack & Architecture

* **Language:** **Kotlin** (Leveraging Coroutines & `suspend` functions for asynchronous networking).
* **UI Framework:** **Jetpack Compose** (Modern declarative UI with custom `Canvas` graphics and `rememberInfiniteTransition` animations).
* **Networking:** **Retrofit 2** + **Gson Converter** (Seamlessly maps NASA's raw JSON response into structured Kotlin data models).
* **Concurrency:** **Kotlin Coroutines & Dispatchers.IO** (Offloads network calls safely off the main UI thread).

---

## 📱 How It Works Under the Hood

1. **The Network Layer (`NasaApi.kt`):** Retrofit connects to `api.nasa.gov`, parses today's date automatically via `SimpleDateFormat`, and maps the incoming JSON payload into clean Kotlin data classes.
2. **The Graphics Engine (`MainActivity.kt`):** The custom `OrbitalTrajectoryCanvas` calculates responsive `(X, Y)` coordinates dynamically using screen dimensions (`size.minDimension`) and trigonometric offsets (`cos` / `sin`) so elements scale cleanly across different phone resolutions.
3. **State Management:** Uses Jetpack Compose state holders (`mutableStateOf`, `mutableIntStateOf`) to drive the interactive trajectory simulator and update HUD telemetry headers instantly.

---

## 📦 Getting Started

1. Clone the repository:
   ```bash
   git clone [https://github.com/aditi578/DoomsdayRadar.git](https://github.com/aditi578/DoomsdayRadar.git)
