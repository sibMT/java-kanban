package DataTransferObject;

public record EpicDTO(
        String taskName,
        String description
) {
    @Override
    public String taskName() {
        return taskName;
    }

    @Override
    public String description() {
        return description;
    }
}