package EverVault.ReadModels;

import java.io.Serializable;

public class CageRunResult<T extends Serializable> implements Serializable {
    public T result;
    public String runId;
}
