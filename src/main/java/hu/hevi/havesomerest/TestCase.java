package hu.hevi.havesomerest;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.nio.file.Path;

@Data
public class TestCase {

    public static final int STATUS_CODE_LENGTH = 3;
    public static final int GET_OR_PUT_LENGTH = 3;
    public static final int POST_LENGTH = 4;
    public static final int DELETE_LENGTH = 6;

    private final Path path;
    private TestDBData dbData;
    private TestDBSchema dBSchema;

    public String getFileName() {
        return path.getFileName().toString();
    }

    public boolean isGet() {
        return path.getFileName().toString().startsWith("get");
    }

    public boolean isPost() {
        return path.getFileName().toString().startsWith("post");
    }

    public boolean isPut() {
        return path.getFileName().toString().startsWith("put");
    }

    public boolean isDelete() {
        return path.getFileName().toString().startsWith("delete");
    }

    public void ifGet(WithoutParameter withoutParameter) {
        if (isGet()) {
            withoutParameter.run();
        }
    }

    public void ifPost(WithoutParameter withoutParameter) {
        if (isPost()) {
            withoutParameter.run();
        }
    }

    public String getStatus() {
        String status = "";
        int offset = GET_OR_PUT_LENGTH;
        if (isPost()) {
            offset = POST_LENGTH;
        } else if (isDelete()) {
            offset = DELETE_LENGTH;
        }

        status = getFileName().substring(offset, offset + STATUS_CODE_LENGTH);
        return status;
    }

    public boolean isStatus(HttpStatus status) {
        boolean isStatus = false;
        return false;

    }

    @FunctionalInterface
    interface WithoutParameter {
        void run();
    }
}
