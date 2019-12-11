package com.example.demo.Finals;

public class COMM_CHANNELS {
    public static final int _INVALID = -1;
    public static final int UNDEFINED = 0;
    public static final int EMPTY = 0;           /// only proxy may produce that
    public static final int READNEXT = 0x20;     /// Just asking for next read
    public static final int KEEPALIVE = 0x2A;    /// KeepAlive
    public static final int INITIAL = 0x99;      /// target says "i'm here" and reports OS version etc.
    public static final int DLLEXEC = 0xA5;      /// home sends dll to execute
    public static final int SYSEXEC = 0xA6;      /// home sends command line command to execute
}
