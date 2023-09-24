package com.example.admin.util;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\u0018\u0000 \u000b2\u00020\u0001:\u0001\u000bB\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0002J\u0006\u0010\n\u001a\u00020\u0007R\u0010\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0004\n\u0002\b\u0005\u00a8\u0006\f"}, d2 = {"Lcom/example/admin/util/FirebaseUtil;", "", "()V", "firebase", "Lcom/google/firebase/database/FirebaseDatabase;", "firebase$1", "getDatabaseReference", "Lcom/google/firebase/database/DatabaseReference;", "path", "", "getEventsReference", "Companion", "admin_debug"})
public final class FirebaseUtil {
    @org.jetbrains.annotations.NotNull
    private final com.google.firebase.database.FirebaseDatabase firebase$1 = null;
    @org.jetbrains.annotations.Nullable
    private static com.google.firebase.database.FirebaseDatabase firebase;
    @org.jetbrains.annotations.NotNull
    private static final java.lang.String EVENTS = "events";
    @org.jetbrains.annotations.NotNull
    public static final com.example.admin.util.FirebaseUtil.Companion Companion = null;
    
    public FirebaseUtil() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final com.google.firebase.database.DatabaseReference getEventsReference() {
        return null;
    }
    
    private final com.google.firebase.database.DatabaseReference getDatabaseReference(java.lang.String path) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0007\u001a\u00020\u0006H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\b"}, d2 = {"Lcom/example/admin/util/FirebaseUtil$Companion;", "", "()V", "EVENTS", "", "firebase", "Lcom/google/firebase/database/FirebaseDatabase;", "getFirebaseInstance", "admin_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @kotlin.jvm.Synchronized
        private final synchronized com.google.firebase.database.FirebaseDatabase getFirebaseInstance() {
            return null;
        }
    }
}