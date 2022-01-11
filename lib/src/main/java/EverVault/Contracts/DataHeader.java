package EverVault.Contracts;

public enum DataHeader {
    String {
        @Override
        public java.lang.String toString() {
            return "string";
        }
    },
    Boolean {
        @Override
        public java.lang.String toString() {
            return "boolean";
        }
    },
    Number {
        @Override
        public java.lang.String toString() {
            return "number";
        }
    },
}
