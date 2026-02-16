package com.realestate.ai.util;

public class BudgetParser {

public static Long parseBudgetToNumber(String input){

if(input==null) return null;

input = input.toLowerCase()
.replaceAll(",","")
.trim();

try{

if(input.contains("crore") ||
input.contains("cr")){

double val =
Double.parseDouble(
input.replaceAll("[^0-9.]","")
);

return (long)(val * 10000000);
}

if(input.contains("lakh") ||
input.contains("lac") ||
input.contains("l")){

double val =
Double.parseDouble(
input.replaceAll("[^0-9.]","")
);

return (long)(val * 100000);
}

if(input.contains("k") ||
input.contains("thousand")){

double val =
Double.parseDouble(
input.replaceAll("[^0-9.]","")
);

return (long)(val * 1000);
}

// plain number
return Long.parseLong(
input.replaceAll("[^0-9]","")
);

}catch(Exception e){
return null;
}

}
}
