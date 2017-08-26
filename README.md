# AndroidActorModel
An Implementation of the Actor-Model in pure Android components, also supports inter-process communication between Actors

# Introduction
Android's Architecture in it's core is designed in a way similar to Actor-Model, where there are Handlers running on different Loopers, and every Looper queues Messages delivered to it's Handler, and this Handler can reply to the Sender Handler through a Messenger ... and this can be done across multiple processes.

# Actor-Model References
This is a link that can help understanding the Actor-Model : http://www.brianstorti.com/the-actor-model/

# Getting Started
in the Application Class, you will need to initialize the ActorSystem in the Applications onCreate() as follows :

    private ActorSystem actorSystem;
    public void onCreate() {
        ...
        ActorSystem.with(this, actorSystem -> this.actorSystem = actorSystem);
    }

The Actor-Model is reactive by nature, there are no blocking methods, if a method will return a value, this will be done through Continuation Passing Style (Callbacks), for example in this example, you can use RxJava2's "ReplaySubject" to save the returned instance, and then subscribe to this "ReplaySubject" across the application to emit this Actor-System in the "onNext()" when ever it is ready

So let's modify the code to use RxJava2 Observable :

    private static MainApplication instance;
    private final Subject<ActorSystem> actorSystem = ReplaySubject.create(1);

    @Override
    public void onCreate() {
        super.onCreate();
        ActorSystem.with(this, actorSystem::onNext);
        instance = this;

    }

    public static Observable<ActorSystem> getActorSystem() {
        return instance.actorSystem;
    }


# Creating the First Actor Service
For Any Service that will be used as an Actor, it should extend "ActorService" and add functions that will be executed when ever a "Message" with a specefic ID (or Message.what) is received, for example :

    public class MainService extends ActorService {

    public static final int MSG_PING = 1;

    public MainService() {
        onMessageReceived(MSG_PING, replyPing()); 			// here we add the replyPing() function to be executed when the received Message.what is MSG_PING
    }

    private Command<Message> replyPing() {
        return message -> {
        
            // this function will be trigerred when the incoming Message.what is MSG_PING
        
            Message newMessage = MessageBuilder  			// start building a new Message
                    .prepareMessage(MainActivity.MSG_SHOW_TOAST) 	// set it's Message.what
                    .serializable("Service Pinged") 			// add a Serializable as an extra to this Message
                    .build(); 						// create the "android.os.Message" to be sent
                    
            MessageReader messageReader = MessageReader.with(message); 	// read the incoming message in a MessageReader Object
            messageReader.getReplyTo().send(newMessage); 		// we reply with this "newMessage" to the Actor who sent the MSG_PING Message
        };
    }
  }
  
ActorService sub-classes execute there functions in the Main Thread, it is safe to make them start in a different process, since the communication style will stay the same ... you can add this to the manifest :
  
    <service android:name=".MainService" android:process=":main" />
    
For Services, there is no more that could be done, all the steps required are 
    1- extend ActorService
    2- add the Command that will be executed when a Message is received through "onMessageReceived()"

# Creating the First Actor Activity / Fragment / ...
for Actors other than Services, they do not extend another classes, all they need to do is to create a "Mailbox" (which is a Handler) that will run on a "Looper", and you can add "Command" instances (functions) to be executed when this Mailbox receives Messages with certain Ids (or Message.what)

also those Mailboxes need to be registered and unregistered to be active, also ActorServices require to be registered and unregistered ... an example for an Actor Activity communicating with our "MainService" will be as follows :

    public static final int MSG_SHOW_TOAST = 1;
    private Mailbox mailbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ...
        mailbox = new Mailbox(MainActivity.class, Looper.getMainLooper());	// create a Mailbox with the address MainActivity.class, and will run on the Main Looper
        mailbox.addCommand(MSG_SHOW_TOAST, showToast()); 			// here we add the showToast() function to be executed when the received Message.what is MSG_SHOW_TOAST

        MainApplication.getActorSystem()
                .subscribe(actorSystem -> {
                    actorSystem.register().actor(mailbox); 			// register the Mailbox of this Activity in the Actor System
                    actorSystem.register().actor(MainService.class); 		// register the MainService to communicate with it, this calls bindService() internally
                });

    }

    private Command<Message> showToast() {
        return message -> Toast.makeText(MainActivity.this,
                MessageReader.with(message).getSerializable().toString(),
                Toast.LENGTH_SHORT).show();
    }

    public void onResume() {
        super.onResume();
        MainApplication.getActorSystem()
                .subscribe(actorSystem -> actorSystem.actorOf(MainService.class)
                        .prepareMessage(MainService.MSG_PING) 	 		// prepare a Message with MainService.MSG_PING in it's Message.what
                        .replyTo(MainActivity.class) 				// tell the receiver that it can reply to the Actor's Mailbox with the address MainActivity.class
                        .serializable("pinging") 				// add a Serializable extra to the message
                        .send());						// send the message in a non-blocking manner
    }


    @Override
    protected void onDestroy() {
        MainApplication.getActorSystem().subscribe(actorSystem -> {
            actorSystem.unregister().actor(MainService.class); 			// unregister the MainService.class Actor, this invokes the unbindService() method
            actorSystem.unregister().actor(mailbox);  				// unregister the Mailbox of this Activity
        });
        super.onDestroy();
    }

For registering and unregistering ActorServices, it is safe to register and unregister multiple times, since all these calls will not resultin any new instances, it's Android's job to handle the bind / unbind multiple calls to the same Service

# Messaging between Actors
There are multiple classes that helps creating and reading "android.os.Message" without caring about Parcelable or Serializable Objects delivery between multiple processes, the "MessageBiulder" and "MessageSender" are 2 classes that Boxes a Message that is guaranteed to be delivered to another process even with Serializable Extras, and the "MessageReader" is a class that Unboxes the Message created by "MessageBuilder" and "MessageSender" ... in the end, these are just wrapper classes around the "android.os.Message"

# Gradle dependency

Step 1. Add it in your root build.gradle at the end of repositories:

    allprojects {
        repositories {
          ...
          maven { url 'https://jitpack.io' }
        }
      }
  
Step 2. Add the dependency
	
    dependencies {
            compile 'com.github.Ahmed-Adel-Ismail:AndroidActorModel:0.0.1'
    }
  
  
