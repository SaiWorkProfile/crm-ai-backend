package com.realestate.ai.util;

public class DeviceParser {

public static String getDevice(String ua){

if(ua.contains("Mobile")) return "Mobile";
if(ua.contains("Tablet")) return "Tablet";

return "Desktop";
}

public static String getOS(String ua){

if(ua.contains("Windows")) return "Windows";
if(ua.contains("Android")) return "Android";
if(ua.contains("iPhone")) return "iOS";
if(ua.contains("Mac")) return "MacOS";
if(ua.contains("Linux")) return "Linux";

return "Unknown";
}

public static String getBrowser(String ua){

if(ua.contains("Chrome")) return "Chrome";
if(ua.contains("Firefox")) return "Firefox";
if(ua.contains("Safari")) return "Safari";
if(ua.contains("Edge")) return "Edge";

return "Unknown";
}
}
