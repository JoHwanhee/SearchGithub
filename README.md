# SearchGithub
깃허브 공개 레포지토리를 검색하는 앱 입니다. Jetpack Compose UI와 MVI+MVVM패턴을 사용하여 개발 되었습니다. 로컬 데이터베이스를 활용해 네트워크가 불안정한 상태에선, 로컬에 미리 저장된 데이터 베이스를 통해 데이터를 검색하도록 개발 되었습니다.

# 아키텍쳐
- [MVI와 MVVM이 함께 사용되었습니다](https://jaehochoe.medium.com/%EB%B2%88%EC%97%AD-%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C%EB%A5%BC-%EC%9C%84%ED%95%9C-mvi-model-view-intent-%EC%95%84%ED%82%A4%ED%85%8D%EC%B3%90-%ED%8A%9C%ED%86%A0%EB%A6%AC%EC%96%BC-%EC%8B%9C%EC%9E%91%ED%95%98%EA%B8%B0-165bda9dfbe7). 전체적인 데이터의 흐름은 다음과 같습니다.

![image](https://user-images.githubusercontent.com/12796737/162610759-af339258-05d9-4901-8519-7ec4c7ee95ef.png)

# 디펜던시
- UI
  - [Compose](https://developer.android.com/jetpack/compose)

- Language
  - [Kotlin](https://kotlinlang.org/)
  - [Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html)
  - [Flow](https://developer.android.com/kotlin/flow)

- DI
  - [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)

- Database
  - [Room](https://developer.android.com/topic/libraries/architecture/room)

- [Jetpack](https://developer.android.com/jetpack)
  - [Navigation](https://developer.android.com/topic/libraries/architecture/navigation/)
  - [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel)

- Network
  - [Retrofit](https://square.github.io/retrofit/)
  - [Coil](https://github.com/coil-kt/coil)
