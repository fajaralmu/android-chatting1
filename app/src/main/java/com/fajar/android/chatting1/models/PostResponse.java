package com.fajar.android.chatting1.models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.fajar.android.chatting1.util.ComponentUtil;
import com.fajar.android.chatting1.util.Logs;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PostResponse implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = -2328541386817117984L;
	private ResponseCategory category;
	private int total;
	@JsonProperty("currentPage")
	private String currentPage;
	@JsonProperty( "current_page")
	public void setCurrentPageJson(Object currentPage){
        Logs.log("JSON Set Current Page: ", currentPage);
		this.currentPage = String.valueOf(currentPage);
		this.currentPageInt = Integer.valueOf(currentPageInt);
	}

	private int currentPageInt;

	private Date lastUpdated;

	@JsonProperty("per_page")
	private int perPage;
	@JsonProperty("perPage")
	public void setPerPageJson(int perPage){
		this.perPage = perPage;
	}
	
	private Object posts;
	
	//@JsonIgnore
	private List<Post > agendas;
//	@JsonIgnore
	private NewsPost newsPost;

	public List<Integer> displayedNavButtonValues(){
	    Logs.log("per page: ", perPage," total: ", total);
		return ComponentUtil.generateButtonValues(perPage,total,  getCurrentPageInt2());
	}


	@JsonIgnore
	public int getCurrentPageInt2(){

		if(null ==currentPage || currentPage.isEmpty()){
			if(currentPageInt > 1){ return currentPageInt; }
			setCurrentPage("1");
		}

		return Integer.valueOf(currentPage);
	}


}
