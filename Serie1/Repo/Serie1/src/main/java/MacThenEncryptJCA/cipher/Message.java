package MacThenEncryptJCA.cipher;

public class Message {
    public byte[] msg;
    public byte[] mark;

    public Message(byte[] msg, byte[] mark) {
        this.msg = msg;
        this.mark = mark;
    }
}
