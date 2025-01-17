package org.koitharu.kotatsu.list.ui.adapter

import androidx.lifecycle.LifecycleOwner
import coil.ImageLoader
import com.google.android.material.badge.BadgeDrawable
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import org.koitharu.kotatsu.R
import org.koitharu.kotatsu.core.ui.image.CoverSizeResolver
import org.koitharu.kotatsu.core.ui.image.TrimTransformation
import org.koitharu.kotatsu.core.ui.list.OnListItemClickListener
import org.koitharu.kotatsu.core.util.ext.disposeImageRequest
import org.koitharu.kotatsu.core.util.ext.enqueueWith
import org.koitharu.kotatsu.core.util.ext.newImageRequest
import org.koitharu.kotatsu.core.util.ext.source
import org.koitharu.kotatsu.databinding.ItemMangaGridBinding
import org.koitharu.kotatsu.history.data.PROGRESS_NONE
import org.koitharu.kotatsu.list.ui.ListModelDiffCallback
import org.koitharu.kotatsu.list.ui.model.ListModel
import org.koitharu.kotatsu.list.ui.model.MangaGridModel
import org.koitharu.kotatsu.list.ui.size.ItemSizeResolver
import org.koitharu.kotatsu.parsers.model.Manga

fun mangaGridItemAD(
	coil: ImageLoader,
	lifecycleOwner: LifecycleOwner,
	sizeResolver: ItemSizeResolver?,
	clickListener: OnListItemClickListener<Manga>,
) = adapterDelegateViewBinding<MangaGridModel, ListModel, ItemMangaGridBinding>(
	{ inflater, parent -> ItemMangaGridBinding.inflate(inflater, parent, false) },
) {
	var badge: BadgeDrawable? = null

	itemView.setOnClickListener {
		clickListener.onItemClick(item.manga, it)
	}
	itemView.setOnLongClickListener {
		clickListener.onItemLongClick(item.manga, it)
	}
	sizeResolver?.attachToView(lifecycleOwner, itemView, binding.textViewTitle, binding.progressView)

	bind { payloads ->
		binding.textViewTitle.text = item.title
		binding.progressView.setPercent(item.progress, ListModelDiffCallback.PAYLOAD_PROGRESS_CHANGED in payloads)
		binding.imageViewCover.newImageRequest(lifecycleOwner, item.coverUrl)?.run {
			size(CoverSizeResolver(binding.imageViewCover))
			placeholder(R.drawable.ic_placeholder)
			fallback(R.drawable.ic_placeholder)
			error(R.drawable.ic_error_placeholder)
			transformations(TrimTransformation())
			allowRgb565(true)
			source(item.source)
			enqueueWith(coil)
		}
		badge = itemView.bindBadge(badge, item.counter)
	}

	onViewRecycled {
		itemView.clearBadge(badge)
		binding.progressView.percent = PROGRESS_NONE
		badge = null
		binding.imageViewCover.disposeImageRequest()
	}
}
