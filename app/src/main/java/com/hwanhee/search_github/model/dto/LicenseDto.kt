package com.hwanhee.search_github.model.dto

import com.google.gson.annotations.SerializedName


data class LicenseDto (

  @SerializedName("key"     ) var key    : String? = null,
  @SerializedName("name"    ) var name   : String? = null,
  @SerializedName("spdx_id" ) var spdxId : String? = null,
  @SerializedName("url"     ) var url    : String? = null,
  @SerializedName("node_id" ) var nodeId : String? = null

)