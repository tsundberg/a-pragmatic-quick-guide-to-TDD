package se.thinkcode.todo;

public record TaskRequest(String user, String chore) {
    public Task toModel() {
        User u = new User(user);
        Chore c = new Chore(chore);

        return new Task(u, c);
    }
}
