package com.tere.playground;

import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.android.actormodel.commands.Command;
import com.android.actormodel.Mailbox;
import com.android.actormodel.messaging.MessageReader;

public class MainActivity extends AppCompatActivity {

    public static final int MSG_SHOW_TOAST = 1;

    private Mailbox mailbox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mailbox = new Mailbox(MainActivity.class);
        mailbox.addCommand(MSG_SHOW_TOAST, showToast());

        MainApplication.getActorSystem()
                .subscribe(actorSystem -> {
                    actorSystem.register().actor(mailbox);
                    actorSystem.register().actor(MainService.class);
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
                        .prepareMessage(MainService.MSG_PING)
                        .replyTo(MainActivity.class)
                        .serializable("pinging")
                        .send());
    }


    @Override
    protected void onDestroy() {
        MainApplication.getActorSystem().subscribe(actorSystem -> {
            actorSystem.unregister().actor(MainService.class);
            actorSystem.unregister().actor(mailbox);
        });

        super.onDestroy();
    }

}
