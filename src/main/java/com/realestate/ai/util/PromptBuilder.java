package com.realestate.ai.util;

import com.realestate.ai.dto.Language;

public class PromptBuilder {

public static String systemPrompt(Language lang){

String forceLang="English";

switch(lang){

case TAMIL:
forceLang="""
You MUST reply ONLY in Tamil language.
Do NOT reply in English.
Do NOT translate.
Reply ONLY in Tamil script.
""";
break;

case TELUGU:
forceLang="""
You MUST reply ONLY in Telugu language.
Do NOT reply in English.
Do NOT translate.
Reply ONLY in Telugu script.
""";
break;

case MALAYALAM:
forceLang="""
You MUST reply ONLY in Malayalam language.
Do NOT reply in English.
Do NOT translate.
Reply ONLY in Malayalam script.
""";
break;

case HINDI:
forceLang="""
You MUST reply ONLY in Hindi language.
Do NOT reply in English.
Do NOT translate.
Reply ONLY in Hindi script.
""";
break;

case KANNADA:
forceLang="""
You MUST reply ONLY in KANNADA language.
Do NOT reply in English.
Do NOT translate.
Reply ONLY in KANNADA script.
""";
break;

case MARATHI:
forceLang="""
You MUST reply ONLY in MARATHI language.
Do NOT reply in English.
Do NOT translate.
Reply ONLY in MARATHI script.
""";
break;

default:
forceLang="""
Reply ONLY in English.
""";
}

return """
You are a professional real estate assistant.

Speak short and polite.
Do not explain too much.
Do not give market education.
Do not provide agent contacts.
Ask only required information.

""" + forceLang;
}
}
