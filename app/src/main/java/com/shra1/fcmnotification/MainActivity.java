package com.shra1.fcmnotification;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shra1.fcmnotification.models.User;
import com.shra1.fcmnotification.services.GetToken;
import com.shra1.fcmnotification.utils.AmPresent;
import com.shra1.fcmnotification.utils.SharedPreferenceManager;
import com.trenzlr.firebasenotificationhelper.FirebaseNotiCallBack;
import com.trenzlr.firebasenotificationhelper.FirebaseNotificationHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.trenzlr.firebasenotificationhelper.Constants.KEY_TEXT;
import static com.trenzlr.firebasenotificationhelper.Constants.KEY_TITLE;

public class MainActivity extends AppCompatActivity {

    public static final String SENDER_ID = "260786374693";
    ProgressDialog p;
    Context c;
    SharedPreferenceManager sharedPreferenceManager;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    List<User> users;
    private EditText etMAMessage;
    private Spinner spMAUsers;
    private Button bMASend;
    private EditText etMAToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        c = this;

        sharedPreferenceManager = SharedPreferenceManager.getInstance(c);

        p = new ProgressDialog(c);
        p.setMessage("Please wait");

        initViews();

        if (internetCheck()) {
            getMyToken();
        } else {
            Toast.makeText(c, "Internet Not Available!", Toast.LENGTH_SHORT).show();
            finish();
        }


        bMASend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = etMAMessage.getText().toString().trim();

                final String to = ((User) spMAUsers.getSelectedItem()).getFcmToken();

                p.setMessage("Sending message");
                p.show();
                FirebaseNotificationHelper.initialize(getString(R.string.server_key))
                        .defaultJson(false, getJsonBody(message))
                        .title("Test")
                        .message(message)
                        .setCallBack(new FirebaseNotiCallBack() {
                            @Override
                            public void success(String s) {
                                dismissP();
                            }

                            @Override
                            public void fail(String s) {
                                dismissP();
                            }
                        })
                        .receiverFirebaseToken(to)
                        .send();

            }
        });

    }

    private void getMyToken() {
        new GetToken(new GetToken.GetTokenCallback() {
            @Override
            public void onSuccessfull(String token) {
                etMAToken.setText(token);

                final User me = new User(Build.MANUFACTURER, token);

                p.setMessage("getting users");
                p.show();
                ref.addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                dismissP();
                                users = new ArrayList<>();

                                for (DataSnapshot s : dataSnapshot.getChildren()) {
                                    User u = s.getValue(User.class);
                                    users.add(u);
                                }

                                /*if (amIPresent(users, me)) {

                                    Toast.makeText(c, "i EXISTS in database", Toast.LENGTH_SHORT).show();

                                } else {
                                    p.setMessage("adding me into database");
                                    p.show();
                                    ref.push()
                                            .setValue(me)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    dismissP();
                                                    getMyToken();
                                                }
                                            });
                                }*/

                                switch (amIPresent(users, me)) {
                                    case PRESENT:
                                        Toast.makeText(c, "i EXISTS in database", Toast.LENGTH_SHORT).show();
                                        break;

                                    case NOT_PRESENT:
                                        p.setMessage("adding me into database");
                                        p.show();
                                        ref.push()
                                                .setValue(me)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        dismissP();
                                                        getMyToken();
                                                    }
                                                });
                                        break;

                                    case PRESENT_BUT_TOKEN_CHANGE:
                                        break;
                                }

                                ArrayAdapter arrayAdapter = new
                                        ArrayAdapter(c,
                                        android.R.layout.simple_list_item_1,
                                        users);

                                spMAUsers.setAdapter(arrayAdapter);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        }
                );

            }

            @Override
            public void onStart() {
                p.setMessage("Getting my fcm id");
                p.show();
            }

            @Override
            public void onEnd() {
                dismissP();
            }
        }).execute();
    }

    /*private boolean amIPresent(List<User> users, User me) {
        for (User u : users) {
            if (u.getName().equals(me.getName())) {
                return true;
            }
        }
        return false;
    }*/

    private AmPresent amIPresent(List<User> users, User me) {
        for (User u : users) {
            if (u.getName().equals(me.getName())) {
                if (u.getFcmToken().equals(me.getFcmToken())) {
                    return AmPresent.PRESENT;
                } else {
                    return AmPresent.PRESENT_BUT_TOKEN_CHANGE;
                }
            }
        }
        return AmPresent.NOT_PRESENT;
    }

    private void dismissP() {
        if (p.isShowing()) p.dismiss();
    }


    private boolean internetCheck() {
        ConnectivityManager connectivity = (ConnectivityManager) c
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    private String getJsonBody(String message) {
        JSONObject jsonObjectData = new JSONObject();
        try {
            jsonObjectData.put(KEY_TITLE, "Custom Title");
            jsonObjectData.put(KEY_TEXT, message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObjectData.toString();
    }

    private void initViews() {
        etMAMessage = findViewById(R.id.etMAMessage);
        spMAUsers = findViewById(R.id.spMAUsers);
        bMASend = findViewById(R.id.bMASend);
        etMAToken = findViewById(R.id.etMAToken);
    }
}
