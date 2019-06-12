package com.foxridge.towerfox.viewmodels;

public enum  StatusEmun {
    REJECTED(0),
    TAKEPIC(1),
    PICTAKEN(2),
    UPLOADED(3),
    APPROVED(4),
    OUTOFSCOPE(5),
    DELETEDPHOTO(6),
    RESETPHOTO(7),
    MISCELLANEOUS(999);
    private int code;

    StatusEmun(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
