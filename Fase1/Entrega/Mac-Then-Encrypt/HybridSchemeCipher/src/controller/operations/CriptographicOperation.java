package controller.operations;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;

public interface CriptographicOperation {
    void execute() throws BadPaddingException, IllegalBlockSizeException, IOException;
}
