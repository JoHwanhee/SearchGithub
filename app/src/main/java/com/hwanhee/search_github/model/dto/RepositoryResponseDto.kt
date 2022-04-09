package com.hwanhee.search_github.model.dto

import com.google.gson.annotations.SerializedName


data class RepositoryResponseDto (

  @SerializedName("total_count"        ) var totalCount        : Int?             = null,
  @SerializedName("incomplete_results" ) var incompleteResults : Boolean?         = null,
  @SerializedName("items"              ) var items             : ArrayList<ItemDto> = arrayListOf()

)