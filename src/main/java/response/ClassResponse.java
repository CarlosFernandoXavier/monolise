package response;

public class ClassResponse {
    private String name;
    private MethodResponse methods;

    public ClassResponse(String name, MethodResponse methods) {
        this.name = name;
        this.methods = methods;
    }

    public String getName() {
        return name;
    }

    public MethodResponse getMethods() {
        return methods;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMethods(MethodResponse methods) {
        this.methods = methods;
    }
}
