# IronPulse - Next-Gen Performance & AI Companion

## Overview
IronPulse combines an Android mobile gym & performance management application with a responsive web showcase landing page featuring cyberpunk glassmorphism, animated telemetry gauges, interactive APK download simulations, and technical specifications.

---

## 🚀 Deploying to Vercel with GitHub

Deploying this showcase page to Vercel via GitHub is fast and requires zero extra configuration because `index.html` and `vercel.json` are already set up:

### Method 1: Using AI Studio Built-in GitHub Export
1. In Google AI Studio, open the **Settings** or project options menu in the top-right corner.
2. Select **Push to GitHub** or **Export as Repository**.
3. Once the repository is created on your GitHub account, visit [vercel.com](https://vercel.com).
4. Log into Vercel and click **Add New...** > **Project**.
5. Select your imported GitHub repository.
6. Keep the default settings (Framework Preset: **Other**, Root Directory: `./`).
7. Click **Deploy**. Vercel will instantly publish your site with a live HTTPS URL!

---

### Method 2: Manual Git CLI Push
If you download the project ZIP or clone it locally:

```bash
# 1. Initialize git (if not already initialized)
git init
git add .
git commit -m "Initial commit of IronPulse Android app and Web Showcase"

# 2. Add your GitHub remote repository
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/YOUR_REPOSITORY_NAME.git

# 3. Push to GitHub
git push -u origin main
```

Then on Vercel:
1. Go to [vercel.com/new](https://vercel.com/new).
2. Choose your repository and click **Import**.
3. Click **Deploy**.

---

## 📱 Features Included
- **Web Showcase (`index.html` / `public/index.html`)**:
  - Cyberpunk dark neon UI (Inter & JetBrains Mono typography, custom CSS glow & glassmorphism)
  - Interactive live phone mockup with animated optimization telemetry gauge
  - Technical APK specifications & step-by-step installation walkthrough
  - Interactive APK download modal with animated progress simulation and real APK stub generation
  - Dynamic live counter simulating download telemetry

- **Android Mobile Application (`/app`)**:
  - **Universal OS Compatibility**:
    - **Min SDK**: API 23 (Android 6.0 Marshmallow) — works on older devices
    - **Target SDK**: API 36 (Android 16 / latest) — fully optimized for modern Android
    - **MultiDex Enabled**: prevents method limit issues on older OS releases
    - **Vector Drawables Support**: crisp vector rendering across all API levels
    - **Predictive Back Gesture Support**: `enableOnBackInvokedCallback="true"` for Android 13+
    - **Hardware Acceleration**: enabled for high-frame-rate rendering on all hardware tiers
    - **Adaptive Insets & Edge-to-Edge**: proper WindowInsets handling avoiding camera cutouts & gesture pill overlap
  - Member tracking, billing & subscription management
  - Automated class scheduling & coach roster approvals
  - Workout & PR activity logger
  - Jetpack Compose M3 responsive UI with Room database persistence
