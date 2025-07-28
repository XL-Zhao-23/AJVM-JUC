package org.example.actor;

/**
 * @author DavyDavyTom Email:a@wk2.cn
 * @since 2025/05/16 16:44
 */
public enum MessageType {
    Product(0, "Product"),
    Consumer(1, "Consumer");

    private int MessageId;
    private String note;
    MessageType(int i, String note) {
        this.note = note;
        this.MessageId = i;
    }

    public int getMessageId() {
        return MessageId;
    }

    public void setMessageId(int messageId) {
        MessageId = messageId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public static MessageType fromMessageId(int i){
        for(MessageType m : MessageType.values()){
            if(m.getMessageId() == i){
                return m;
            }
        }
        throw new IllegalArgumentException("No such MessageType");
    }

    public static MessageType fromNote(String note){
        for(MessageType m : MessageType.values()){
            if(m.getNote().equals(note)){
                return m;
            }
        }
        throw new IllegalArgumentException("No such Note");
    }
}
