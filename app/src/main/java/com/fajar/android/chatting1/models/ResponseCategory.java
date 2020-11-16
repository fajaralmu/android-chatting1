package com.fajar.android.chatting1.models;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ResponseCategory implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = 1195345208045522504L;
	private String name, slug;
	@JsonProperty("term_id")
	private String termId;
	@JsonProperty("termId")
	private void setTermIdJson(String t){
		termId = t;
	}

}
