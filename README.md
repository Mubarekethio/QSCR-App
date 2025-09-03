# Qafaraf and Amharic Speech Command Recognition App (QSCRA)

[![Android](https://img.shields.io/badge/Platform-Android-green?logo=android)](https://developer.android.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

---

## Table of Contents
1. [Introduction](#introduction)  
2. [Installation Guide](#installation-guide)  
3. [Getting Started](#getting-started)  
4. [Features Documentation](#features-documentation)  
   - 4.1 [Home Voice Command Recognition](#home-voice-command-recognition)  
   - 4.2 [Settings Configuration](#settings-configuration)  
   - 4.3 [Bluetooth Connection Management](#bluetooth-connection-management)  
   - 4.4 [About App Information](#about-app-information)  
5. [Architecture Overview](#architecture-overview)  
6. [API Documentation](#api-documentation)  
7. [Troubleshooting](#troubleshooting)  
8. [FAQs](#faqs-frequently-asked-questions)  
9. [Updates and Version History](#updates-and-version-history)  
10. [Support and Contact Information](#support-and-contact-information)  
11. [Legal Information](#legal-information)  
12. [Appendix](#appendix)  

---

## 1. Introduction
Welcome to the documentation for **Qafaraf and Amharic Speech Command Recognition App (QSCRA)**, developed by Mubarek Kebede.  

QSCRA enables users, especially individuals with physical disabilities, to control wheelchairs using **voice commands** in **Qafaraf** and **Amharic** languages through machine learning and speech recognition technologies.

### Purpose and Goals
- Improve accessibility and mobility for individuals with physical disabilities.  
- Provide intuitive and efficient wheelchair control via voice commands.  
- Support local languages to ensure inclusivity.  

### Target Audience
- **Individuals with Physical Disabilities**: Enhance independence and autonomy.  
- **Caregivers & Healthcare Professionals**: Assist patients with mobility tasks.  
- **Developers & Researchers**: Explore ML and assistive technologies.  
- **Accessibility Advocates**: Promote inclusive technology adoption.  

---

## 2. Installation Guide

### System Requirements
- **Android Device**: Android 5.0 (Lollipop) or higher.  
- **API Level**: 21 or higher for TensorFlow support.  
- **Storage**: Sufficient space for app and TensorFlow library.  
- **Memory (RAM)**: Adequate RAM recommended for performance.  
- **Processor**: CPU compatible with NNAPI for hardware acceleration.  

### Download & Installation
#### From GitHub
1. Visit [QSCRA GitHub Releases](https://github.com/Mubarekethio/QSCR-App/releases/tag/v2.0).
3. Download the APK [here](https://github.com/Mubarekethio/QSCR-App/releases/download/v2.0/QSCR.apk) to your device.  
4. Open the APK via your file manager and follow on-screen prompts.  
5. Launch the app from the home screen or app drawer.  

#### From Other Sources
1. Enable installation from unknown sources: `Settings > Security > Unknown sources`.  
2. Open the downloaded APK and follow installation prompts.  
3. Launch the app from your home screen or app drawer.  

### Permissions
- **Microphone Access**: Required for voice command recognition.  
- **Bluetooth Access**: Required for wheelchair communication.  

---

## 3. Getting Started

### Initial Setup
1. Install the QSCRA app following the installation guide.  
2. Grant necessary permissions on first launch.  

### User Interface Overview
- **Microphone Icon**: Initiates voice input for Qafaraf or Amharic commands.  
- **Home (Audio) Fragment**: Displays predicted commands and progress bar.  
- **Settings Fragment**: Configure language, threshold, delegate options, and processing parameters.  
- **Connect Fragment**: Manage Bluetooth connections with wheelchair hardware.  
- **About Fragment**: View app version, developer info, and acknowledgments.  

---

## 4. Features Documentation

### 4.1 Home Voice Command Recognition
- Recognizes commands like "Move Forward", "Turn Left", or "Stop".  
- Initiate by tapping the **microphone icon**.  
- Predicted word and confidence percentage are displayed with a progress bar.  

### 4.2 Settings Configuration
- **Language Selection**: Qafaraf or Amharic.  
- **Threshold Adjustment**: Accuracy level for recognition.  
- **Max Result**: Max returned results for voice recognition.  
- **Thread Count**: Number of threads for processing.  
- **Overlap Time**: Audio processing overlap duration.  
- **Delegation Options**: CPU or NNAPI for model inference.  

### 4.3 Bluetooth Connection Management
- Scan, pair, and manage Bluetooth devices with wheelchair hardware.  
- Connection status is displayed on the home screen.  

### 4.4 About App Information
- View app version, developer credits, and acknowledgments.  

---

## 5. Architecture Overview

### Components
**Activities**:  
- `MainActivity`: Hosts navigation and manages fragments.  

**Fragments**:  
- `HomeFragment`: Voice command recognition and prediction display.  
- `SettingsFragment`: App customization.  
- `ConnectFragment`: Bluetooth management.  
- `AboutFragment`: App information.  

**Services**:  
- `BluetoothService`: Manages Bluetooth communication.  
- `VoiceRecognitionService`: Handles audio preprocessing and ML inference.  

### Third-Party Libraries
- **TensorFlow Lite**: Machine learning inference.  
- **Android Bluetooth API**: Communication with wheelchair.  
- **Android Navigation Component**: Fragment navigation.  
- **Material Design Components**: UI elements and styling.  

---

## 6. API Documentation
1. **Bluetooth API**: Establishes communication with wheelchair hardware.  
2. **TensorFlow Lite API**: Runs voice command models locally.  
3. **Android Location API**: Optional, if location-based features exist.  
4. **Third-Party APIs**: Optional external services (with API keys or OAuth).  

---

## 7. Troubleshooting
- **Bluetooth Connection Failure**: Check device discoverability, permissions, and restart devices.  
- **Voice Recognition Errors**: Verify language settings, microphone functionality, and background noise levels.  
- **App Crashes**: Update app, clear cache, or reinstall.  

**Error Messages**
- `"Bluetooth connection failed"`: Bluetooth module not active or paired.  
- `"Voice command not recognized"`: Speak clearly, adjust threshold or overlap.  

**Reporting Bugs**
- **In-App Feedback**, **Email Support**: `support@qscra.com`  
- **App Store Reviews**  

---

## 8. FAQs (Frequently Asked Questions)
**Q1:** How do I initiate voice commands?  
**A:** Tap the microphone icon in the bottom navigation bar.  

**Q2:** Can I customize the app's settings?  
**A:** Yes, in the Settings fragment.  

**Q3:** How do I connect Bluetooth to the wheelchair?  
**A:** Use the Connect fragment to scan, pair, and manage devices.  

**Q4:** What languages are supported?  
**A:** Qafaraf and Amharic.  

**Q5:** How accurate is voice recognition?  
**A:** Accuracy depends on noise, audio quality, and clarity. Adjust settings for better results.  

**Q6:** How to report feedback?  
**A:** Use in-app feedback, email, or app store reviews.  

---

## 9. Updates and Version History
- **v1.0.0**: Initial release with voice recognition and Bluetooth.  
- **v1.1.0**: Improved voice recognition, UI updates, Bluetooth stability.  
- **v1.2.0**: Real-time feedback, volume adjustment, model optimization.  

---


## 10. Legal Information
- **Terms of Service**: Usage rules and user responsibilities.  
- **Privacy Policy**: How personal data is collected and used.  
- **Open-Source Licensing**: Attribution for incorporated libraries.  
- **Disclaimer & Limitation of Liability**: App provided "as is".  
- **Governing Law**: [Your Country/Region].  
- **Contact Legal**: `legal@qscra.com`  

---

## Appendix
- **Glossary**: Technical terms definitions.  
- **References**: Bibliography and additional resources.  
- **Acknowledgments**: Contributors and community support.  

---

## GitHub Links
- [Download APK](https://github.com/Mubarekethio/QSCR-App/releases/download/v2.0/QSCR.apk)  
- [Source Code Repository](https://github.com/Mubarekethio/QSCR-App)
