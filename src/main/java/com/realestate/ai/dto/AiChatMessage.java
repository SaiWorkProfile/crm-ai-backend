package com.realestate.ai.dto;

import java.util.List;

public class AiChatMessage {

	    private String role;   // user | assistant
	    private String content;
	    public AiChatMessage(String role, String content) {
	        this.role = role;
	        this.content = content;
	    }
		public String getRole() {
			return role;
		}
		public void setRole(String role) {
			this.role = role;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
	    
	}

