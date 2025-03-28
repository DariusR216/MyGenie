package com.flightpath.mygenie;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private EditText userInput;
    private Button sendButton;
    private TextView chatDisplay;
    private ProgressBar loadingIndicator; // Add a loading indicator
    private static final String OPENAI_API_KEY = BuildConfig.OPENAI_API_KEY;
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final MediaType JSON = MediaType.get("application/json");
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS) // Set connection timeout
            .readTimeout(30, TimeUnit.SECONDS)    // Set read timeout
            .writeTimeout(30, TimeUnit.SECONDS)   // Set write timeout
            .build();
    private ArrayList<Task> taskList;
    private ArrayAdapter<String> taskAdapter;
    private EditText taskInput;
    private Button addTaskButton;
    private ListView taskListView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        taskInput = findViewById(R.id.taskInput);
        addTaskButton = findViewById(R.id.addTaskButton);
        taskListView = findViewById(R.id.taskListView);

        // Initialize task list
        taskList = new ArrayList<>();
        taskAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        taskListView.setAdapter(taskAdapter);

        // Add task button click listener
        addTaskButton.setOnClickListener(view -> {
            String taskDescription = taskInput.getText().toString().trim();
            if (!taskDescription.isEmpty()) {
                Task newTask = new Task(taskDescription, false);
                taskList.add(newTask);
                taskAdapter.add(newTask.toString());
                taskInput.setText(""); // Clear input field
            }
        });

        // Toggle task completion on item click
        taskListView.setOnItemClickListener((parent, view, position, id) -> {
            Task selectedTask = taskList.get(position);
            selectedTask.toggleCompletion();
            taskAdapter.insert(selectedTask.toString(), position);
            taskAdapter.remove(taskAdapter.getItem(position + 1));
        });

        userInput = findViewById(R.id.userInput);
        sendButton = findViewById(R.id.sendButton);
        chatDisplay = findViewById(R.id.chatDisplay);
        loadingIndicator = findViewById(R.id.loadingIndicator); // Initialize the loading indicator

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = userInput.getText().toString().trim();
                if (!message.isEmpty()) {
                    chatDisplay.append("You: " + message + "\n");
                    userInput.setText("");
                    showLoading(true); // Show loading indicator
                    getAIResponse(message);
                }
            }
        });
    }

    /**
     * Displays or hides the loading indicator.
     * @param show true to show, false to hide.
     */
    private void showLoading(final boolean show) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (show) {
                    loadingIndicator.setVisibility(View.VISIBLE);
                } else {
                    loadingIndicator.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * Sends a request to the OpenAI API to get an AI response.
     * @param message the message to send to the AI
     */
    private void getAIResponse(String message) {
        String apiKey = BuildConfig.OPENAI_API_KEY; // Retrieve API Key

        OkHttpClient client = new OkHttpClient();
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model", "gpt-3.5-turbo"); // Change model if needed
            jsonBody.put("messages", new JSONArray()
                    .put(new JSONObject().put("role", "system").put("content", "You are a helpful assistant."))
                    .put(new JSONObject().put("role", "user").put("content", message))
            );
            jsonBody.put("temperature", 0.7);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey) // 🔥 Use API Key here
                .header("Content-Type", "application/json")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    loadingIndicator.setVisibility(View.GONE); // Hide loading on failure
                    chatDisplay.append("Error: Unable to connect\n");
                });
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(() -> loadingIndicator.setVisibility(View.GONE)); // Hide loading on success
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        String aiMessage = jsonResponse.getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");

                        runOnUiThread(() -> chatDisplay.append("AI: " + aiMessage + "\n"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * Displays an error message using a Toast.
     * @param message the message to show
     */
    private void showError(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}