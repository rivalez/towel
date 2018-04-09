package priority.queue;

public class Package {
    private int id;
    private String toWhom;
    private byte[] content = {0,0,0,1};

    public Package(int id, String toWhom) {
        this.id = id;
        this.toWhom = toWhom;
    }

    public int getId() {
        return id;
    }

    public String getToWhom() {
        return toWhom;
    }
}
