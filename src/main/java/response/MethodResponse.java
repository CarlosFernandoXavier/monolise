package response;

import java.util.List;

public class MethodResponse {
    private List<String> names;

    public MethodResponse(List<String> names) {
        this.names = names;
    }

    public List<String> getNames() {
        return names;
    }
}
