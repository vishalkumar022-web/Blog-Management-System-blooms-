package in.vishal.blooms.models;

public enum Status {

    PUBLISHED("Published"),
    INREVIEW("In Review"),
    REJECTED ("Rejected"),;

    private final String displayName;

    private Status(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}