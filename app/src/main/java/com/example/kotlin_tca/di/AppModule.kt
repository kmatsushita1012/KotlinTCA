val liveAppModule = module {
    single<ApiRepository> { LiveApiRepository() }
}

val mockAppModule = module {
    single<ApiRepository> { MockApiRepository() }
}
