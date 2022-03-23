package com.codesoom.assignment.services;

import com.codesoom.assignment.models.Task;
import com.codesoom.assignment.models.TaskDto;

import java.util.ArrayList;
import java.util.List;

public class TaskService {

    private  List<Task> tasks = new ArrayList<>();

    public List<Task> getTasks() {
        return tasks;
    }

    /**
     * 증가된 id를 반환한다.
     * */
    public Long generateId() {
        if (tasks.isEmpty() || tasks == null) {
            return 1L;
        }

        Long maxId = Long.MIN_VALUE;
        for (Task task : tasks) {
            maxId = task.getId() > maxId ? task.getId() : maxId;
        }
        return maxId + 1L;
    }

    /**
     * 새로운 할 일을 추가한다.
     * */
    public Task addTask(TaskDto taskDto) {
        Task newTask = taskDto.toTask(generateId());
        tasks.add(newTask);
        return newTask;
    }

    /**
     * id로 할 일을 찾아 반환한다.
     * */
    public Task findTaskById(Long id) {
        return tasks.stream()
                .filter(task -> task.getId() == id)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    /**
     * 할 일의 제목을 수정한다.
     * */
    public Task updateTaskById(Long id, TaskDto taskDto) {
        return findTaskById(id).updateTitle(taskDto.getTitle());
    }

    /**
     * id로 할 일을 삭제한다.
     * */
    public void deleteTaskById(Long id) {
        tasks.remove(findTaskById(id));
    }

}
