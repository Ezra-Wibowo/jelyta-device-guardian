# Known Limitations & Platform Capabilities

To ensure complete transparency and full compliance with Google Play Developer Policies, the following technical limits apply:

1. **Non-Root System Modifications**: Modern Android OS strictly prevents user-space apps from forcibly killing non-owned background processes or clearing third-party app private data. The app's "One-Tap Boost" performs garbage collection (`System.gc()`) and clears its own runtime cache.
2. **CPU Temperature vs. Battery Temperature**: Due to hardware vendor security restrictions, direct CPU thermal zone access may not be exposed on non-rooted Android 10+ devices. Battery temperature (via standard `BatteryManager.EXTRA_TEMPERATURE`) is used as a reliable thermal indicator.
3. **Self-Healing Scope**: The AI Self-Healing feature provides diagnostic repair recommendations and optimizes internal app state. It does not overwrite OS core files or bypass Android system security sandbox boundaries.
