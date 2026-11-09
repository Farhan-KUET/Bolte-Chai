# ProGuard rules for BolteChai
# Keep Room entities and DAOs
-keep class com.farhan.boltechai.data.db.entity.** { *; }
-keep class com.farhan.boltechai.data.db.dao.** { *; }

# Keep Firebase model classes
-keep class com.farhan.boltechai.data.firebase.** { *; }

# Keep Gson serialization
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }

# Keep ContentProvider
-keep class com.farhan.boltechai.provider.** { *; }

# Keep BroadcastReceivers
-keep class com.farhan.boltechai.receiver.** { *; }

# Keep data models
-keep class com.farhan.boltechai.data.model.** { *; }

