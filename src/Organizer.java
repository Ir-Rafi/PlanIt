public class Organizer {
    private String name;
    private String phone;
    private String code; // new field

    public Organizer(String name, String phone, String code) {
        this.name = name;
        this.phone = phone;
        this.code = code;
    }

    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getCode() { return code; }

    @Override
    public String toString() {
        return name + " (" + phone + ") Code: " + code;
    }
}
