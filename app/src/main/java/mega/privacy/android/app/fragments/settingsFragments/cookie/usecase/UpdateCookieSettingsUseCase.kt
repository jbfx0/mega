package mega.privacy.android.app.fragments.settingsFragments.cookie.usecase

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.disposables.Disposable
import mega.privacy.android.app.fragments.settingsFragments.cookie.data.CookieType
import mega.privacy.android.app.utils.ErrorUtils.toThrowable
import mega.privacy.android.app.utils.LogUtil.*
import nz.mega.sdk.*
import java.util.*
import javax.inject.Inject

class UpdateCookieSettingsUseCase @Inject constructor(
    private val megaApi: MegaApiAndroid
) {

    /**
     * Save cookie settings to SDK
     *
     * @param cookies Set of cookies to be enabled
     * @return Observable with the operation result
     */
    fun update(cookies: Set<CookieType>?): Completable =
        Completable.create { emitter ->
            if (cookies.isNullOrEmpty()) {
                emitter.onError(IllegalArgumentException("Cookies are null or empty"))
                return@create
            }

            val listener = object : MegaRequestListenerInterface {
                override fun onRequestStart(api: MegaApiJava, request: MegaRequest) {
                    if (emitter.isDisposed) {
                        megaApi.removeRequestListener(this)
                    }
                }

                override fun onRequestUpdate(api: MegaApiJava, request: MegaRequest) {
                    if (emitter.isDisposed) {
                        megaApi.removeRequestListener(this)
                    }
                }

                override fun onRequestFinish(
                    api: MegaApiJava,
                    request: MegaRequest,
                    error: MegaError
                ) {
                    if (error.errorCode == MegaError.API_OK) {
                        emitter.onComplete()
                    } else {
                        emitter.onError(error.toThrowable())
                    }
                }

                override fun onRequestTemporaryError(
                    api: MegaApiJava,
                    request: MegaRequest,
                    error: MegaError
                ) {
                    logError(error.toThrowable().stackTraceToString())
                }
            }

            val bitSet = BitSet(CookieType.values().size).apply {
                this[CookieType.ESSENTIAL.value] = true // Essential cookies are always enabled
            }

            cookies.forEach { cookie ->
                bitSet[cookie.value] = true
            }

            val bitSetToDecimal = bitSet.toLongArray().first().toInt()
            megaApi.setCookieSettings(bitSetToDecimal, listener)

            emitter.setDisposable(Disposable.fromAction {
                megaApi.removeRequestListener(listener)
            })
        }

    /**
     * Save cookie settings with all the cookies enabled
     *
     * @return Observable with the operation result
     */
    fun acceptAll(): Completable =
        update(CookieType.values().toMutableSet())
}
