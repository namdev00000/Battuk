# Add project specific ProGuard rules here.
# Room
-keep class androidx.room.** { *; }
-keep @androidx.room.Entity class * { *; }
-dontwarn org.jetbrains.annotations.**
