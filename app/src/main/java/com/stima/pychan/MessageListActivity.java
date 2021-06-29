package com.stima.pychan;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static com.stima.pychan.Message.StringMatching;

public class MessageListActivity extends AppCompatActivity {
    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    private ArrayList<Message> messageList;
    private User user;
    private User pychanUser;

    private Button sendButton;
    private EditText userChatInput;

    private static ArrayList<String> dataPertanyaan;
    private static ArrayList<String> dataSynonym;
    private static ArrayList<String> dataStopWords;

    private static String namaFilePertanyaan = "raw/datapertanyaan.txt";
    private static String namaFileSynonym = "raw/datasynonym.txt";
    private static String namaFileStopWords = "raw/datastopwords.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dataPertanyaan = getDataFromFile(getApplicationContext(), R.raw.datapertanyaan);
        dataStopWords = getDataFromFile(getApplicationContext(), R.raw.datastopwords);
        dataSynonym = getDataFromFile(getApplicationContext(), R.raw.datasynonym);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        user = new User("Saad Ahmed");
        pychanUser = new User("Saad");

        messageList = new ArrayList<Message>();
        messageList.add(new Message("Hello, I am Help Mate!", pychanUser));
        messageList.add(new Message("I am a chatbot who likes to answer About Sindh University", pychanUser));
        messageList.add(new Message("Uh, before that, first acquaintance, please! What is your name?", pychanUser));

        sendButton = findViewById(R.id.button_chatbox_send);
        userChatInput = findViewById(R.id.edittext_chatbox);

        // SETUP RECYCLERVIEW
        mMessageRecycler = findViewById(R.id.reyclerview_message_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mMessageRecycler.setLayoutManager(layoutManager);
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mMessageRecycler.getContext(), layoutManager.getOrientation());
//        mMessageRecycler.addItemDecoration(dividerItemDecoration);
        mMessageAdapter = new MessageListAdapter(this, messageList);
        mMessageRecycler.setAdapter(mMessageAdapter);

        setOnClick();
    }

    private void setOnClick(){
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String content = userChatInput.getText().toString();
                if (content.trim().length() > 0) {
                    if (messageList.size() > 3) {
                        messageList.add(new Message(content, user));
                        userChatInput.getText().clear();
                        mMessageAdapter.notifyDataSetChanged();
                        // Auto scroll when new message is created
                        mMessageRecycler.post(new Runnable() {
                            @Override
                            public void run() {
                                mMessageAdapter.notifyDataSetChanged();
                                // Call smooth scroll
                                mMessageRecycler.smoothScrollToPosition(mMessageAdapter.getItemCount());
                            }
                        });
                        new Thread(new Runnable() {
                            public void run() {
                                String tempContent = new String(content);
                                messageList.add(new Message(StringMatching(tempContent), new User("Pychan")));
                                mMessageRecycler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Call smooth scroll
                                        mMessageRecycler.smoothScrollToPosition(mMessageAdapter.getItemCount());
                                        mMessageAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        }).start();
                        // Auto scroll when new message is created
                    } else {
                        messageList.add(new Message(content, user));
                        userChatInput.getText().clear();
                        user.setNickname(content);
                        mMessageAdapter.notifyDataSetChanged();
                        messageList.add(new Message("Hello, " + user.getNickname() + "! greetings!", pychanUser));
                        messageList.add(new Message("If you have questions, just ask straight away!", pychanUser));
                    }
                }

            }
        });
    }

    //Prosedur pembacaan file dan memindahkan ke memori agar data dari file tersimpan
    public static ArrayList<String> getDataFromFile(Context ctx, int resId){

        ArrayList<String> data = new ArrayList<String>();

        InputStream inputStream = ctx.getResources().openRawResource(resId);

        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader buffreader = new BufferedReader(inputreader);

        String temp = null;
        StringBuilder text = new StringBuilder();
            try{
                while((temp = buffreader.readLine()) != null){
                    data.add(temp);
            }
            System.out.println("SUCCESSFUL");
//            in.close();
        }catch(Exception e){
            System.out.println("Failed to read file " + resId);
//            System.exit(1);
            e.printStackTrace();
        }

        return data;
    }

    public static ArrayList<String> getDataPertanyaan(){
        return dataPertanyaan;
    }

    public static ArrayList<String> getDataSynonym(){
        return dataSynonym;
    }

    public static ArrayList<String> getDataStopWords(){
        return dataStopWords;
    }
}
