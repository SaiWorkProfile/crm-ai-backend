package com.realestate.ai.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class VoiceSessionService {


// 🔥 THREAD SAFE MULTI USER SESSION
private final Map<String,Map<String,Object>> sessions =
new ConcurrentHashMap<>();


// ================= INTERNAL =================
private Map<String,Object> getSession(String sessionId){

if(sessionId==null || sessionId.isBlank()){
sessionId="VOICE_DEFAULT";
}

return sessions.computeIfAbsent(
sessionId,
k->new ConcurrentHashMap<>()
);
}


// ================= SET =================
public void set(
String sessionId,
String key,
Object value){

getSession(sessionId)
.put(key,value);
}


// ================= STRING GET =================
public String getString(
String sessionId,
String key){

Object v=getSession(sessionId).get(key);

if(v==null) return null;

return String.valueOf(v);
}


// ================= MAP GET =================
@SuppressWarnings("unchecked")
public Map<String,Object> getMap(
String sessionId,
String key){

Object v=getSession(sessionId).get(key);

if(v==null)
return new ConcurrentHashMap<>();

if(v instanceof Map)
return (Map<String,Object>)v;

return new ConcurrentHashMap<>();
}


// ================= HAS =================
public boolean has(
String sessionId,
String key){

return getSession(sessionId)
.containsKey(key);
}


// ================= REMOVE =================
public void remove(
String sessionId,
String key){

getSession(sessionId)
.remove(key);
}


// ================= CLEAR =================
public void clear(
String sessionId){

sessions.remove(sessionId);
}

}
