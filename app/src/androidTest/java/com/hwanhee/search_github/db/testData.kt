package com.hwanhee.search_github.db

import com.hwanhee.search_github.model.entity.GithubRepositoryItemEntity
import com.hwanhee.search_github.model.entity.GithubRepositoryOwnerEntity
import com.hwanhee.search_github.model.entity.GithubTopicEntity

fun createTestItem(ownerId: Long): GithubRepositoryItemEntity {
    return GithubRepositoryItemEntity(
        0,
        "테스트 레포",
        "테스트 레포 풀 네임",
        false,
        ownerId,
        "설명",
        "https://github.com",
        "https://github.com",
        10,
        10,
        10,
        "assembly",
        false
    )
}

fun createTestItem2(ownerId: Long): GithubRepositoryItemEntity {
    return GithubRepositoryItemEntity(
        0,
        "테스트 레포2",
        "테스트 레포 풀 네임2",
        false,
        ownerId,
        "설명",
        "https://github.com",
        "https://github.com",
        10,
        10,
        10,
        "assembly",
        false
    )
}


fun createTestOwners(id:Long): GithubRepositoryOwnerEntity {
    return GithubRepositoryOwnerEntity(
        id,
        "https://avatars.githubusercontent.com/u/20634838?v=4",
        "test",
        "https://github.com",
        "assembly",
    )
}

fun createTestTopic(repositoryId:Long): GithubTopicEntity {
    return GithubTopicEntity(
        repositoryId = repositoryId,
        "테스트 토픽 $repositoryId"
    )
}
