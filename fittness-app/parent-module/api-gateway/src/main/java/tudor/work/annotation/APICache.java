package tudor.work.annotation;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class APICache {
    private int value = 60;
    private String key = "";
}