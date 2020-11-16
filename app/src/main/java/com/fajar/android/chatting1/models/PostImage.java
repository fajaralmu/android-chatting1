package com.fajar.android.chatting1.models;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostImage implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1108131248710418042L;
	private String thumbnail;
	private String medium;
	private String large;
	private String full;

}
