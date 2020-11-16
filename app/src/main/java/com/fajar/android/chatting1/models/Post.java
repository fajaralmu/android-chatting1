package com.fajar.android.chatting1.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

import lombok.NoArgsConstructor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Post implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 5563148598862171231L;

	private int id;
	private String
    author,
    title,
    slug,
    content,
    excerpt,
    type,
    date,
    release;
	// author_id
	@JsonProperty("author_id")
	private String authorId;
	@JsonProperty("authorId")
	public void setAuthorIdJson(String id){
		authorId = id;
	}
	private PostImage images;

	public String newsLink(){
		String mainUrl = "https://chatting.sch.id/#!/read/";
		return mainUrl+getSlug();
	}
}
