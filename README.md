# Bakalarka

this project is demp application for Android focused on motivateing young violinists. Contails Frequency Analysis using modified FFT and NDFT algorithms and HPS and Window Method to determine final frequency. Works with 

## Install

project should be runnable using Android Studio :

Android Studio Flamingo | 2022.2.1 Beta 3


if you are using different android studio you might have to chanege version of android studio defined in /MusicApps/grade/libs.versions.toml following line with your related Android Studio Verson -> https://developer.android.com/build/releases/gradle-plugin

```yaml
androidStudio       = "8.0.0-beta03" # this version depends on android studio version
```

alternativly you can use gradle wo android studio (not guarantee it works)

``` bash
cd MusicApps
```

``` bash
graldle assemble 
```

using gradle build might fail because it runs default AndroidStudio linter that im not using


this should create .apk file in MusicApps/violin/build/outputs/apk/release

## Switching Testing Frequency Reading Enviroment and Violin Frequency Reading Enviroment

- ViolinSingleFrequencyReader uses HPS transform therefore you need octave oscilating instrument to not be cleared out.

- SoundGeneratorFrequencyReader uses Window Method and special lookup of frequency domain to find a frequency. SGFR can be used almost for everything but has worse frequency domain clearing.


switch part of DI in file called SoundModule (DoubleShift in Android Studio and type in "SoundM") 

``` Kotlin 
factory<SingleFrequencyReader> {
        //ViolinSingleFrequencyReader(pcmAudioRecorder = get())
        SoundGeneratorFrequencyReader(pcmAudioRecorder = get())
}
```

## Static analysis

Double control in Android Studio and type gradle detekt

or

``` bash
graldle detekt 
```

## Following reports are in GitLab CI Pipeline Artefacts

- Lint for Android 
- Detekt - linter for Kotlin
- debug.apk
- release.apk
- tests-results
