package com.codesoom.assignment;

import com.codesoom.assignment.models.Task;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TaskHttpHandler implements HttpHandler {

    private List<Task> tasks = new ArrayList<>();
    private ObjectMapper objectMapper = new ObjectMapper();

    public TaskHttpHandler() {
        addTemporaryTasks();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        URI requestURI = httpExchange.getRequestURI();
        String path = requestURI.getPath();
        String method = httpExchange.getRequestMethod();

        System.out.println(method + " " + path);

        InputStream httpRequestBody = httpExchange.getRequestBody();
        String body = new BufferedReader(new InputStreamReader(httpRequestBody))
            .lines()
            .collect(Collectors.joining("\n"));

        String content = "Hello, World!";
        int httpStatusCode = 200;

        if (method.equals("GET") && path.equals("/tasks")) {
            content = tasksToJson();
        }

        if (method.equals("GET") && Pattern.matches("/tasks/[0-9]+$", path)) {
            long taskId = getTaskIdFromPath(path);

            String contentFromTaskId = getContentFromTaskId(taskId);
            if (contentFromTaskId == null) {
                content = "Can't find task from your id.";
                httpStatusCode = 404;
            } else {
                content = contentFromTaskId;
            }
        }

        if (method.equals("POST") && path.equals("/tasks")) {
            if (!body.isEmpty()) {
                tasks.add(toTask(body));
                httpStatusCode = 201;
            }
        }

        httpExchange.sendResponseHeaders(httpStatusCode, content.getBytes().length);

        OutputStream responseBody = httpExchange.getResponseBody();
        responseBody.write(content.getBytes());
        responseBody.flush();
        responseBody.close();
    }

    private Task toTask(String content) throws JsonProcessingException {
        Long newTaskId = ((long) (tasks.size() + 1));

        Task task = objectMapper.readValue(content, Task.class);
        task.setId(newTaskId);

        return task;
    }

    private String getContentFromTaskId(long taskId) {
        return tasks.stream()
            .filter(task -> task.isMatchId(taskId))
            .map(Task::getTitle)
            .findFirst()
            .orElse(null);
    }

    private long getTaskIdFromPath(String path) {
        return Arrays.stream(path.split("/"))
            .skip(2)
            .mapToLong(Long::parseLong)
            .findFirst()
            .getAsLong();
    }

    private String tasksToJson() throws IOException {
        OutputStream outputStream = new ByteArrayOutputStream();

        objectMapper.writeValue(outputStream, tasks);

        return outputStream.toString();
    }

    private void addTemporaryTasks() {
        Task task1 = new Task(1L, "task one");
        Task task2 = new Task(2L, "task two");
        Task task3 = new Task(3L, "task three");

        tasks.add(task1);
        tasks.add(task2);
        tasks.add(task3);
    }

}
