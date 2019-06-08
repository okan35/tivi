/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.tivi.tvdb

import app.tivi.appinitializers.AppInitializer
import com.uwetrottmann.thetvdb.TheTvdb
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import javax.inject.Named
import javax.inject.Singleton

@Module(includes = [TvdbModuleBinds::class])
class TvdbModule {
    @Singleton
    @Provides
    fun provideTvdb(
        @Named("cache") cacheDir: File,
        interceptor: HttpLoggingInterceptor,
        @Named("tvdb-api") apiKey: String
    ): TheTvdb {
        return object : TheTvdb(apiKey) {
            override fun setOkHttpClientDefaults(builder: OkHttpClient.Builder) {
                super.setOkHttpClientDefaults(builder)
                builder.apply {
                    addInterceptor(interceptor)
                    cache(Cache(File(cacheDir, "tvdb_cache"), 10 * 1024 * 1024))
                }
            }
        }
    }
}

@Module
abstract class TvdbModuleBinds {
    @Binds
    @IntoSet
    abstract fun provideTvdbInitializer(bind: TvdbInitializer): AppInitializer
}