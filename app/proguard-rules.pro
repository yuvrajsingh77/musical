# Keep data classes
-keep class com.example.musical.data.model.** { *; }
-keep class com.example.musical.data.remote.dto.** { *; }

# Keep Firebase
-keep class com.google.firebase.** { *; }

# Keep Retrofit
-keepattributes Signature
-keepattributes Exceptions
-keep class retrofit2.** { *; }

# Keep Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
