# Don't forget to keep your data classes that are stored in CoroutineCache
-keep class com.epam.example.coroutinescache.Data { *; }


# ProGuard instructions that are required for CoroutineCache to work properly
-keepclassmembers enum com.epam.coroutinecache.core.Source {
    public *;
}