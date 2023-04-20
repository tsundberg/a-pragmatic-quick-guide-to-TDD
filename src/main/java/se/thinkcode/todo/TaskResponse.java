package se.thinkcode.todo;

import java.util.ArrayList;
import java.util.List;

public record TaskResponse(String user, String chore) {
    public static List<TaskResponse> fromModel(List<Task> tasks) {
        List<TaskResponse> res = new ArrayList<>();

        for (Task task : tasks) {
            String user = task.user().user();
            String chore = task.chore().chore();
            TaskResponse response = new TaskResponse(user, chore);

            res.add(response);
        }

        return res;
    }
}
