# CoroutinesCache

## Preview
The goal of this library is simple: caching your data models like **Picasso** caches your images, with no effort at all.

Every Android application is a client application, which means it does not make sense to create and maintain a database just for caching data.

Plus, the fact that you have some sort of legendary database for persisting your data does not solves by itself the real challenge: to be able to configure your caching needs in a flexible and simple way.

Inspired by Retrofit api, CoroutinesCache is a reactive caching library for Android and Kotlin which turns your caching needs.

To get full information about how library works follow next article:

https://proandroiddev.com/caching-with-kotlin-coroutines-7d819276c820

#### Note

Coroutines Cache works only on `kotlin-coroutines:0.26.1` and above.

## Getting started

Library is located in jcenter, add jcenter repository in main gradle file

```kotlin
allprojects {
  repositories {
    ...
    jcenter()
  }
}
```

Grab via Gradle:

```kotlin
  implementation 'com.epam.coroutinecache:coroutinecache:0.9.6'
```
or Maven:

```kotlin
  <dependency>
    <groupId>com.epam.coroutinecache</groupId>
    <artifactId>coroutinecache</artifactId>
    <version>0.9.6</version>
    <type>pom</type>
  </dependency>
```



## Usage

To start use cache you need to create CoroutinesCache object: 

```kotlin
val coroutinesCache = CoroutinesCache(cacheParams: CacheParams, scope: CoroutineScope)
```

`CacheParams` defines cache location, json mapper and persistence size in Mb.

First param of `CacheParams` is max size that could be saved in persistence in Mb.

Second param is `JsonMapper` interface that provides one of several implementation: `GsonMapper`, `JacksonMapper`, `MoshiMapper`. For each of those mappers you need to add appropriate dependency in `build.gradle` file.

Last param is directory where cache files will be stored. Be sure that directory exists and you has write permission there, otherwise you will get `IllegalStateException`

```kotlin
class CacheParams (maxPersistenceCacheMB: Int, mapper: JsonMapper, directory: File)
```

Next step you need to create an interface with functions that will describe data, which should be stored. For each function you can add params by next annotations:

1. `@ProviderKey(key: String, entryClass: EntryClass)` - **key** that will be used for saving data, **entryClass** it is an another annotation `@EntryClass(rawType: KClass<*>, vararg typeParams: EntryClass = [])` that used to specify value's type needed for serialization/deserialization (to avoid obtaining type through reflection). **typeParams** used when you need parameterized type, such annotation for list should looks like: `EntryClass(List::class, EntryClass(Data::class))`
2. `@LifeTime(value: Long, timeUnit: TimeUnit)` - **value** describes how long record should be stored in memory or persistence. If this annotation isn't set, record will be stored without time limit
3. `@Expirable` - Set **expirable** param to true. If this annotation isn't set it means that record could be deleted from persistence, even it hasn't reached life limit in persistence low memory case.
4. `@UseIfExpired` - If this annotation is set it means that data will be retrieved from cache even if record reached its lifetime. Could be used only once, after getting, record will be deleted from cache.

**Note:** In cases when data can be requested without any parameters you may use a method that should be a suspend function containing only one param that is also a suspend function without params that returns type **T**. It's result will be stored in the cache. And function's return value also should be **<T>**
In case, when you data request call requires some parameters and also you want to store data with different keys that depend on call parameters, then you should use a suspend method that takes an implementation of `ParameterizedDataProvider` interface. In that case data will be stored under modified key that is controlled by your implementation of `parameterizeKey` method.

To connect interface and CoroutinesCache and your interface just call `CoroutinesCache.using(YourInterface::class.java)`. This method will return interface instance. To save and get data from cache just call methods from returned instance of your interface.
## Example 1 (no parameters)

**CacheProviders**
```kotlin
interface CacheProviders {
    @ProviderKey("TestKey", EntryClass(Data::class))
    @LifeTime(value = 1L, unit = TimeUnit.MINUTES)
    @Expirable
    @UseIfExpired
    suspend fun getData(dataProvider: suspend () -> Data): Data
}
```

**Repository**
```kotlin
class Repository (private val cacheDirectory: File) {
     private val coroutinesCache = CoroutinesCache(CacheParams(10, GsonMapper(), cacheDirectory))
     private val restApi = Retrofit...create(RestApi::class.java)
     private val cacheProviders = coroutinesCache.using(CacheProviders::class.java)
     
     suspend fun getData(): Data = cacheProviders.getData(restApi::getData)
}
```
**MainActivity**
```kotlin
class MainActivity : AppCompatActivity() {
    private val persistence by lazy { Repository(cacheDir) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        GlobalScope.launch (Dispatchers.Main) {
            val data = persistence.getData()
            messageView.text = data.toString()
        }
    }
}
````

## Example 2 (with parameters)

**CacheProviders**
```kotlin
interface CacheProviders {
    @ProviderKey("BaseKey", EntryClass(Data::class))
    @LifeTime(value = 1L, unit = TimeUnit.MINUTES)
    @Expirable
    @UseIfExpired
    suspend fun getParameterizedData(provider: ParameterizedDataProvider<Data>): Data
}
```

**Repository**
```kotlin
class Repository (private val cacheDirectory: File) {
     private val coroutinesCache = CoroutinesCache(CacheParams(10, GsonMapper(), cacheDirectory))
     private val restApi = Retrofit...create(RestApi::class.java)
     private val cacheProviders = coroutinesCache.using(CacheProviders::class.java)

     suspend fun getParametrizedData(search: String): Data = cacheProviders.getParameterizedData(DataProviderImpl())

     private inner class DataProviderImpl(private val search: String) : ParameterizedDataProvider<Data> {

          override suspend fun getData(): Data = restApi.getParameterizedData(search)

          override fun parameterizeKey(baseKey: String): String = "${baseKey}_$search"
     }
}
```
**MainActivity**
```kotlin
class MainActivity : AppCompatActivity() {
    private val persistence by lazy { Repository(cacheDir) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        GlobalScope.launch (Dispatchers.Main) {
            val data = persistence.getParameterizedData("Hello world!")
            messageView.text = data.toString()
        }
    }
}
````

To see full example, check app module in this repository. 

## ProGuard
Do not forget to keep your own data classes in proguard rules. For example, add this line for 'Data' class that is being cached with this library:
```
# Keep all of your data classes that are stored in CoroutineCache
-keep class com.epam.example.coroutinescache.Data { *; }
```

Also, please add following instructions so that CoroutineCache may work properly:
```
# ProGuard instructions that are required for CoroutineCache to work properly
-keepclassmembers enum com.epam.coroutinecache.core.Source {
    public *;
}
```