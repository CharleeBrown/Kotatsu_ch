package org.koitharu.kotatsu.browser.cloudflare

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.PendingIntentCompat
import androidx.core.net.toUri
import coil.request.ErrorResult
import coil.request.ImageRequest
import org.koitharu.kotatsu.R
import org.koitharu.kotatsu.core.exceptions.CloudFlareProtectedException
import org.koitharu.kotatsu.parsers.model.ContentType

class CaptchaNotifier(
	private val context: Context,
) : ImageRequest.Listener {

	@SuppressLint("MissingPermission")
	fun notify(exception: CloudFlareProtectedException) {
		val manager = NotificationManagerCompat.from(context)
		if (!manager.areNotificationsEnabled()) {
			return
		}
		val channel = NotificationChannelCompat.Builder(CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_DEFAULT)
			.setName(context.getString(R.string.captcha_required))
			.setShowBadge(true)
			.setVibrationEnabled(false)
			.setSound(null, null)
			.setLightsEnabled(false)
			.build()
		manager.createNotificationChannel(channel)

		val intent = CloudFlareActivity.newIntent(context, exception.url, exception.headers)
			.setData(exception.url.toUri())
		val notification = NotificationCompat.Builder(context, CHANNEL_ID)
			.setContentTitle(channel.name)
			.setPriority(NotificationCompat.PRIORITY_DEFAULT)
			.setDefaults(NotificationCompat.DEFAULT_SOUND)
			.setSmallIcon(android.R.drawable.stat_notify_error)
			.setAutoCancel(true)
			.setVisibility(
				if (exception.source?.contentType == ContentType.HENTAI) {
					NotificationCompat.VISIBILITY_SECRET
				} else {
					NotificationCompat.VISIBILITY_PUBLIC
				},
			)
			.setContentText(
				context.getString(
					R.string.captcha_required_summary,
					exception.source?.title ?: context.getString(R.string.app_name),
				),
			)
			.setContentIntent(PendingIntentCompat.getActivity(context, 0, intent, 0, false))
			.build()
		manager.notify(TAG, exception.source.hashCode(), notification)
	}

	override fun onError(request: ImageRequest, result: ErrorResult) {
		super.onError(request, result)
		val e = result.throwable
		if (e is CloudFlareProtectedException) {
			notify(e)
		}
	}

	private companion object {

		private const val CHANNEL_ID = "captcha"
		private const val TAG = CHANNEL_ID
	}
}
