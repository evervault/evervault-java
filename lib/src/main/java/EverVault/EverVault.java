/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package EverVault;

import EverVault.Exceptions.UndefinedDataException;

import java.util.Dictionary;

/// TODO
public class EverVault {
    public EverVault(String apiKey) {
    }

    public <TDataType> String Encrypt(TDataType data) throws Exception {
        if ( data == null ){
            throw new UndefinedDataException();
        }
        return "Test";
    }

    public void Run(String cageName, Dictionary<String, String> data, Dictionary<String, String> options) {
        /// TODO
    }
}