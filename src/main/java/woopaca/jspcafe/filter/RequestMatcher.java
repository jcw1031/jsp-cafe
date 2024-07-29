package woopaca.jspcafe.filter;

import java.util.regex.Pattern;

public record RequestMatcher(String method, String path) {

    public boolean matches(String method, String path) {
        if (!this.method.equalsIgnoreCase(method)) {
            return false;
        }

        String regex = this.path.replace("*", ".*");
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(path).matches();
    }
}
