# android-service
Examples about android service.

## These are the three different types of services:

### Foreground
--------------
A foreground service performs some operation that is noticeable to the user. For example, an audio app would use a foreground service to play an audio track. Foreground services must display a Notification. Foreground services continue running even when the user isn't interacting with the app.

Here is an example:

```kotlin 
val pendingIntent = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), 0)

val notification = NotificationCompat.Builder(this, DEFAULT_CHANNEL_ID)
                    .setContentTitle("Title")
                    .setContentText("Text")
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentIntent(pendingIntent)
                    .setTicker("Ticker")
                    .build()

startForeground(ONGOING_NOTIFICATION_ID, notification)
```
Background
--------------
A background service performs an operation that isn't directly noticed by the user. For example, if an app used a service to compact its storage, that would usually be a background service.

    `Note: If your app targets API level 26 or higher, the system imposes [restrictions on running background services](https://developer.android.com/about/versions/oreo/background) when the app itself isn't in the foreground. In most cases like this, your app should use a [scheduled job](https://developer.android.com/topic/performance/scheduling) instead.`

Here is an example:

```kotlin
/**
 * A constructor is required, and must call the super IntentService(String)
 * constructor with a name for the worker thread.
 */
class BackgroundService : IntentService("BackgroundService") {
    
    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     */
    override fun onHandleIntent(intent: Intent?) {
        Log.d("BackgroundService", "onHandleIntent run on ${Thread.currentThread().name}")
        sendBroadcast(Intent(NOTIFICATION).also { it.putExtra(STATE, START) })
        Thread.sleep(TimeUnit.SECONDS.toMillis(2))
        sendBroadcast(Intent(NOTIFICATION).also { it.putExtra(STATE, END) })
    }
}
```

Bound
---------
A service is bound when an application component binds to it by calling bindService(). A bound service offers a client-server interface that allows components to interact with the service, send requests, receive results, and even do so across processes with interprocess communication (IPC). A bound service runs only as long as another application component is bound to it. Multiple components can bind to the service at once, but when all of them unbind, the service is destroyed.

There are three ways you can define the interface:

#### [Extending the Binder class](https://developer.android.com/guide/components/bound-services#Binder)

If your service is private to your own application and runs in the same process as the client (which is common), you should create your interface by extending the Binder class and returning an instance of it from onBind(). The client receives the Binder and can use it to directly access public methods available in either the Binder implementation or the Service.

This is the preferred technique when your service is merely a background worker for your own application. The only reason you would not create your interface this way is because your service is used by other applications or across separate processes.

`Note: This works only if the client and service are in the same application and process, which is most common. For example, this would work well for a music application that needs to bind an activity to its own service that's playing music in the background.`

Here's how to set it up:

1. In your service, create an instance of Binder that does one of the following:
* Contains public methods that the client can call.
* Returns the current Service instance, which has public methods the client can call.
* Returns an instance of another class hosted by the service with public methods the client can call.
2. Return this instance of Binder from the onBind() callback method.
3. In the client, receive the Binder from the onServiceConnected() callback method and make calls to the bound service using the methods provided.

`Note: The service and client must be in the same application so that the client can cast the returned object and properly call its APIs. The service and client must also be in the same process, because this technique does not perform any marshaling across processes.`

Here is an example:

```kotlin
class LocalService : Service() {

    // Binder given to clients
    private val mBinder = LocalBinder()
    // Random number generator
    private val mGenerator = Random()

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    inner class LocalBinder : Binder() {
        internal// Return this instance of LocalService so clients can call public methods
        val service: LocalService
            get() = this@LocalService
    }

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    /** method for clients  */
    fun getRandomNumber(): Int {
        return mGenerator.nextInt(100)
    }
}
```

#### Using a Messenger

If you need your interface to work across different processes, you can create an interface for the service with a Messenger. In this manner, the service defines a Handler that responds to different types of Message objects. This Handler is the basis for a Messenger that can then share an IBinder with the client, allowing the client to send commands to the service using Message objects. Additionally, the client can define a Messenger of its own, so the service can send messages back.

This is the simplest way to perform interprocess communication (IPC), because the Messenger queues all requests into a single thread so that you don't have to design your service to be thread-safe.

Here's a summary of how to use a Messenger:

1. The service implements a Handler that receives a callback for each call from a client.
2. The service uses the Handler to create a Messenger object (which is a reference to the Handler).
3. The Messenger creates an IBinder that the service returns to clients from onBind().
4. Clients use the IBinder to instantiate the Messenger (that references the service's Handler), which the client uses to send Message objects to the service.
5. The service receives each Message in its Handler—specifically, in the handleMessage() method.

Here's a simple example service that uses a Messenger interface:

```kotlin
class MessengerService : Service() {

    /**
     * Handler of incoming messages from clients.
     */
    @SuppressLint("HandlerLeak")
    internal inner class IncomingHandler : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_SAY_HELLO -> Toast.makeText(applicationContext, "hello!", Toast.LENGTH_SHORT).show()
                else -> super.handleMessage(msg)
            }
        }
    }

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    private val mMessenger = Messenger(IncomingHandler())

    /**
     * When binding to the service, we return an interface to our messenger
     * for sending messages to the service.
     */
    override fun onBind(intent: Intent): IBinder? {
        Toast.makeText(applicationContext, "binding", Toast.LENGTH_SHORT).show()
        return mMessenger.binder
    }

    companion object {
        /** Command to the service to display a message  */
        const val MSG_SAY_HELLO = 1
    }
}
```

#### Using AIDL

Android Interface Definition Language (AIDL) decomposes objects into primitives that the operating system can understand and marshals them across processes to perform IPC. The previous technique, using a Messenger, is actually based on AIDL as its underlying structure. As mentioned above, the Messenger creates a queue of all the client requests in a single thread, so the service receives requests one at a time. If, however, you want your service to handle multiple requests simultaneously, then you can use AIDL directly. In this case, your service must be thread-safe and capable of multi-threading.

To use AIDL directly, you must create an .aidl file that defines the programming interface. The Android SDK tools use this file to generate an abstract class that implements the interface and handles IPC, which you can then extend within your service.

`Note: Most applications shouldn't use AIDL to create a bound service, because it may require multithreading capabilities and can result in a more complicated implementation. As such, AIDL is not suitable for most applications and this document does not discuss how to use it for your service. If you're certain that you need to use AIDL directly, see the AIDL document.`

To create a bounded service using AIDL, follow these steps:

1. Create the .aidl file
This file defines the programming interface with method signatures.
1. Implement the interface
The Android SDK tools generate an interface in the Java programming language, based on your .aidl file. This interface has an inner abstract class named Stub that extends Binder and implements methods from your AIDL interface. You must extend the Stub class and implement the methods.
3. Expose the interface to clients
Implement a Service and override onBind() to return your implementation of the Stub class.
