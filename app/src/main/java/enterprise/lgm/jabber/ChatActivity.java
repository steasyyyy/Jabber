package enterprise.lgm.jabber;


import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.internal.zzagy;

import java.io.IOException;
import java.util.ArrayList;
import enterprise.lgm.jabber.entities.Message;
import enterprise.lgm.jabber.entities.MessageAdapter;

public class ChatActivity extends AppCompatActivity {
    String friendname;
    JabberApplication app;
    public ArrayList<Message> messages;
    public static ChatActivity activity = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        this.app = (JabberApplication)getApplication();

        friendname = getIntent().getStringExtra("friendname");
        TextView textFriendName = (TextView)findViewById(R.id.friendName);
        textFriendName.setText(friendname);
        updateList();


        ImageButton sendMessageButton = (ImageButton) findViewById(R.id.sendMessageButton);
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText messageField = (EditText) findViewById(R.id.messageEditText);
                if(app.isConnectingToInternet()) {
                    try {
                        Server.getServer().sendMessage(app.getNickname(), friendname, messageField.getText().toString(), app.getPassword());
                        updateList();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        messageField.setText("");
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("nickname", app.getNickname());
        startActivity(intent);
    }

    public void updateList(){
        /*
        final ListView chatList = (ListView) findViewById(R.id.msgListView);
        ArrayList<Message> messages = null;
        //ArrayList<String> nachrichten = new ArrayList<String>();
        try {
            messages = Server.getServer().getMessage(app.getNickname(), friendname, app.getPassword());
            //for(Message m: messages){
            //    nachrichten.add(m.text +"  " +m.date);
           // }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // selbst geschrieben blau, zugesendete Nachricht rot
        final MessageAdapter adapter = new MessageAdapter(ChatActivity.this, R.layout.message_list, messages);
        chatList.setAdapter(adapter);

        scrollMyListViewToBottom(chatList);
        */
        new ChatTask().execute("");

    }

    private void scrollMyListViewToBottom(final ListView list) {
        list.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                list.setSelection(list.getAdapter().getCount() - 1);
            }
        });
    }

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    private class ChatTask extends AsyncTask<String, Void, String> {
        final ListView chatList = (ListView) findViewById(R.id.msgListView);
        ArrayList<Message> messages = null;

        @Override
        protected String doInBackground(String... params) {
            if(app.isConnectingToInternet()) {
                try {
                    messages = Server.getServer().getMessage(app.getNickname(), friendname, app.getPassword());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                zzagy.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // selbst geschrieben blau, zugesendete Nachricht rot
                        final MessageAdapter adapter = new MessageAdapter(ChatActivity.this, R.layout.message_list, messages);
                        chatList.setAdapter(adapter);

                        scrollMyListViewToBottom(chatList);

                    }
                });
            }
                return null;

        }
    }
}
