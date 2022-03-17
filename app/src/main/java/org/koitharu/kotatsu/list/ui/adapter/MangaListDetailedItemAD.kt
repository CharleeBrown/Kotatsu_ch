package org.koitharu.kotatsu.list.ui.adapter

import androidx.lifecycle.LifecycleOwner
import coil.ImageLoader
import coil.request.Disposable
import coil.util.CoilUtils
import com.google.android.material.badge.BadgeDrawable
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import org.koitharu.kotatsu.R
import org.koitharu.kotatsu.base.ui.list.OnListItemClickListener
import org.koitharu.kotatsu.databinding.ItemMangaListDetailsBinding
import org.koitharu.kotatsu.list.ui.model.ListModel
import org.koitharu.kotatsu.list.ui.model.MangaListDetailedModel
import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.utils.ext.enqueueWith
import org.koitharu.kotatsu.utils.ext.newImageRequest
import org.koitharu.kotatsu.utils.ext.referer
import org.koitharu.kotatsu.utils.ext.textAndVisible

fun mangaListDetailedItemAD(
	coil: ImageLoader,
	lifecycleOwner: LifecycleOwner,
	clickListener: OnListItemClickListener<Manga>,
	viewFactory: AsyncViewFactory,
) = adapterDelegateViewBinding<MangaListDetailedModel, ListModel, ItemMangaListDetailsBinding>(
	{ inflater, parent ->
		viewFactory[R.layout.item_manga_list_details]?.let {
			ItemMangaListDetailsBinding.bind(it)
		} ?: run {
			ItemMangaListDetailsBinding.inflate(inflater, parent, false)
		}
	}
) {

	var imageRequest: Disposable? = null
	var badge: BadgeDrawable? = null

	itemView.setOnClickListener {
		clickListener.onItemClick(item.manga, it)
	}
	itemView.setOnLongClickListener {
		clickListener.onItemLongClick(item.manga, it)
	}

	bind {
		imageRequest?.dispose()
		binding.textViewTitle.text = item.title
		binding.textViewSubtitle.textAndVisible = item.subtitle
		imageRequest = binding.imageViewCover.newImageRequest(item.coverUrl)
			.referer(item.manga.publicUrl)
			.placeholder(R.drawable.ic_placeholder)
			.fallback(R.drawable.ic_placeholder)
			.error(R.drawable.ic_placeholder)
			.allowRgb565(true)
			.lifecycle(lifecycleOwner)
			.enqueueWith(coil)
		binding.textViewRating.textAndVisible = item.rating
		binding.textViewTags.text = item.tags
		itemView.bindBadge(badge, item.counter)
	}

	onViewRecycled {
		itemView.clearBadge(badge)
		badge = null
		imageRequest?.dispose()
		imageRequest = null
		CoilUtils.clear(binding.imageViewCover)
		binding.imageViewCover.setImageDrawable(null)
	}
}