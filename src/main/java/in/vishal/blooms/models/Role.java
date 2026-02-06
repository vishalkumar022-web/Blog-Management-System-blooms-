package in.vishal.blooms.models;
// USER - normal user
// ADMIN - admin user
public enum Role {
    USER("user"),
    ADMIN("admin");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
