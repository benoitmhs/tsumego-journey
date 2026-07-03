package com.mrsanglier.tsumegohero.di

import androidx.compose.runtime.Composable
import com.mrsanglier.tsumegohero.domain.authenticationModule
import com.mrsanglier.tsumegohero.domain.authentication.domainAuthenticationModule
import com.mrsanglier.tsumegohero.dashboard.di.dashboardModule
import com.mrsanglier.tsumegohero.domain.appsettings.domainAppSettingsModule
import com.mrsanglier.tsumegohero.game.domainGameModule
import com.mrsanglier.tsumegohero.game.gameModule
import com.mrsanglier.tsumegohero.localdatasources.di.localDaoModule
import com.mrsanglier.tsumegohero.localdatasources.di.localDatasourceModule
import com.mrsanglier.tsumegohero.localdatasources.di.localPlatformModule
import com.mrsanglier.tsumegohero.dashboardgame.domainDashboardGameModule
import com.mrsanglier.tsumegohero.dashboardgame.domainProfileModule
import com.mrsanglier.tsumegohero.domain.common.domainCommonModule
import com.mrsanglier.tsumegohero.rankestimation.domainRankEstimationModule
import com.mrsanglier.tsumegohero.remotedatasources.di.remoteDatasourcesModule
import com.mrsanglier.tsumegohero.remotedatasources.di.remoteModule
import com.mrsanglier.tsumegohero.repository.di.repositoryModule
import org.koin.compose.KoinApplication
import org.koin.dsl.KoinAppDeclaration

@Composable
fun THKoinApplication(
    config: KoinAppDeclaration? = null,
    content: @Composable () -> Unit,
) {
    KoinApplication(
        application = {
            config?.invoke(this)
            modules(
                appModule,
                appPlatformModule,
                localPlatformModule,
                localDaoModule,
                localDatasourceModule,
                remoteModule,
                remoteDatasourcesModule,
                repositoryModule,
                domainAppSettingsModule,
                domainAuthenticationModule,
                domainCommonModule,
                domainDashboardGameModule,
                domainProfileModule,
                domainGameModule,
                domainRankEstimationModule,
                authenticationModule,
                dashboardModule,
                gameModule,
            )
        },
        content = content,
    )
}
