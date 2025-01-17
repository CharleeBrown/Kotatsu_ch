package org.koitharu.kotatsu.bookmarks.ui.adapter

import androidx.lifecycle.LifecycleOwner
import coil.ImageLoader
import org.koitharu.kotatsu.bookmarks.domain.Bookmark
import org.koitharu.kotatsu.core.ui.BaseListAdapter
import org.koitharu.kotatsu.core.ui.list.OnListItemClickListener

class BookmarksAdapter(
	coil: ImageLoader,
	lifecycleOwner: LifecycleOwner,
	clickListener: OnListItemClickListener<Bookmark>,
) : BaseListAdapter<Bookmark>(
	bookmarkListAD(coil, lifecycleOwner, clickListener),
)
