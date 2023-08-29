package clevertec.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 
 * Config that contains {@link java.util.Map} of property name to property
 * value.
 * <p>
 * Config represent <b>application.properties</b>, path is
 * projectRoot/app/src/resource
 * 
 */
public class Config {

    private static final Map<String, String> nameToProperty;

    private static final String SEPARATOR = "=";

    static {
        try {
            // FIXME: test this in docker
            // In tests working dir - clevertec-bank-task/app
            // In app - clevertec-bank-task/app
            // System.getProperty("user.dir");

            nameToProperty = Files
                    .readAllLines(Path.of(
                            "/home/penton/projects/java/clevertec-bank-task/app/src/main/resources/applicaiton.properties"))
                    .stream()
                    .map(String::strip)
                    .map(s -> s.split(SEPARATOR))
                    .collect(Collectors.toMap(arr -> arr[0], arr -> arr[1]));
        } catch (IOException e) {
            throw new RuntimeException("Can't initialize config", e);
        }
    }

    private Config() {
    }

    /**
     * 
     * @param name - name of property
     * @return value of property
     */
    public static String getProperty(String name) {
        return nameToProperty.get(name);
    }

    /**
     * 
     * @param <To> - the type of variable we want to cast to
     * @param name - name of property
     * @param cast - function which will perform casting
     * @see Config#getProperty(String)
     * @return - casted value of property
     */
    public static <To> To getProperty(String name, Function<String, To> cast) {
        return cast.apply(getProperty(name));
    }
}
