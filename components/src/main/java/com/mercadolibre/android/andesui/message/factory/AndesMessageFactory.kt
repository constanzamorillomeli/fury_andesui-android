package com.mercadolibre.android.andesui.message.factory

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import com.mercadolibre.android.andesui.R
import com.mercadolibre.android.andesui.message.hierarchy.AndesMessageHierarchy
import com.mercadolibre.android.andesui.message.hierarchy.AndesMessageHierarchyInterface
import com.mercadolibre.android.andesui.message.state.AndesMessageState
import com.mercadolibre.android.andesui.message.state.AndesMessageStateInterface

internal data class AndesMessageConfiguration(
        val iconBackgroundColor: Int,
        val backgroundColor: Int,
        val pipeColor: Int,
        val textColor: Int,
        val titleText: String? = null,
        val bodyText: String? = null,
        val titleSize: Float,
        val bodySize: Float,
        val lineHeight: Int,
        val titleTypeface: Typeface?,
        val bodyTypeface: Typeface?,
        val icon: Drawable?,
        val isDismissable: Boolean,
        val dismissableIcon: Drawable?,
        val dismisssableIconColor: Int?,
        val primaryActionText: String?,
        val secondaryActionText: String?
)

internal object AndesMessageFactory {
    private const val ANDES_MESSAGE_HIERARCHY_LOUD = "1000"
    private const val ANDES_MESSAGE_HIERARCHY_QUIET = "1001"

    private const val ANDES_MESSAGE_STATE_HIGHLIGHT = "2000"
    private const val ANDES_MESSAGE_STATE_SUCCESS = "2001"
    private const val ANDES_MESSAGE_STATE_WARNING = "2002"
    private const val ANDES_MESSAGE_STATE_ERROR = "2003"

    fun create(context: Context, attr: AttributeSet?): AndesMessageConfiguration {
        val typedArray = context.obtainStyledAttributes(attr, R.styleable.AndesMessage)

        val hierarchy = when (typedArray.getString(R.styleable.AndesMessage_andesMessageHierarchy)) {
            ANDES_MESSAGE_HIERARCHY_LOUD -> AndesMessageHierarchy.LOUD.hierarchy
            ANDES_MESSAGE_HIERARCHY_QUIET -> AndesMessageHierarchy.QUIET.hierarchy
            else -> AndesMessageHierarchy.LOUD.hierarchy
        }

        val state = when (typedArray.getString(R.styleable.AndesMessage_andesMessageState)) {
            ANDES_MESSAGE_STATE_HIGHLIGHT -> AndesMessageState.HIGHLIGHT.state
            ANDES_MESSAGE_STATE_SUCCESS -> AndesMessageState.SUCCESS.state
            ANDES_MESSAGE_STATE_WARNING -> AndesMessageState.WARNING.state
            ANDES_MESSAGE_STATE_ERROR -> AndesMessageState.ERROR.state
            else -> AndesMessageState.HIGHLIGHT.state
        }

        return AndesMessageConfiguration(
                iconBackgroundColor = resolveIconBackgroundColor(state, context),
                backgroundColor = resolveBackgroundColor(hierarchy, state, context),
                pipeColor = resolvePipeColor(state, context),
                textColor = resolveTextColor(hierarchy, context),
                titleText = typedArray.getString(R.styleable.AndesMessage_andesMessageTitleText),
                bodyText = typedArray.getString(R.styleable.AndesMessage_andesMessageBodyText),
                titleSize = resolveTitleSize(context),
                lineHeight = resolveLineHeight(context),
                bodySize = resolveBodySize(context),
                titleTypeface = resolveTitleTypeface(hierarchy, context),
                bodyTypeface = resolveBodyTypeface(hierarchy, context),
                icon = resolveIcon(state, hierarchy, context),
                isDismissable = typedArray.getBoolean(R.styleable.AndesMessage_andesMessageDismissable, false),
                dismissableIcon = resolveDismissableIcon(hierarchy, context),
                dismisssableIconColor = resolveDismissableIconColor(hierarchy, context),
                primaryActionText = null,
                secondaryActionText = null
        )
                .also { typedArray.recycle() }
    }

    @Override
    fun create(context: Context, andesMessageHierarchy: AndesMessageHierarchy, andesMessageState: AndesMessageState, body: String, title: String?, isDismissable: Boolean): AndesMessageConfiguration {
        val hierarchy = andesMessageHierarchy.hierarchy
        val state = andesMessageState.state

        return AndesMessageConfiguration(
                iconBackgroundColor = resolveIconBackgroundColor(state, context),
                backgroundColor = resolveBackgroundColor(hierarchy, state, context),
                pipeColor = resolvePipeColor(state, context),
                textColor = resolveTextColor(hierarchy, context),
                titleText = title,
                bodyText = body,
                titleSize = resolveTitleSize(context),
                lineHeight = resolveLineHeight(context),
                bodySize = resolveBodySize(context),
                titleTypeface = resolveTitleTypeface(hierarchy, context),
                bodyTypeface = resolveBodyTypeface(hierarchy, context),
                icon = resolveIcon(state, hierarchy, context),
                isDismissable = isDismissable,
                dismissableIcon = resolveDismissableIcon(hierarchy, context),
                dismisssableIconColor = resolveDismissableIconColor(hierarchy, context),
                primaryActionText = null,
                secondaryActionText = null
        )
    }

    private fun resolveIconBackgroundColor(state: AndesMessageStateInterface, context: Context) = state.iconBackgroundColor(context)
    private fun resolveBackgroundColor(hierarchy: AndesMessageHierarchyInterface, state: AndesMessageStateInterface, context: Context) = hierarchy.backgroundColor(context, state)
    private fun resolvePipeColor(state: AndesMessageStateInterface, context: Context) = state.pipeColor(context)
    private fun resolveTextColor(hierarchy: AndesMessageHierarchyInterface, context: Context) = hierarchy.textColor(context)
    private fun resolveTitleSize(context: Context) = context.resources.getDimension(R.dimen.andesui_message_title)
    private fun resolveBodySize(context: Context) = context.resources.getDimension(R.dimen.andesui_message_body)
    private fun resolveLineHeight(context: Context) = context.resources.getDimension(R.dimen.andesui_message_line_height).toInt()
    private fun resolveTitleTypeface(hierarchy: AndesMessageHierarchyInterface, context: Context) = hierarchy.titleTypeface(context)
    private fun resolveBodyTypeface(hierarchy: AndesMessageHierarchyInterface, context: Context) = hierarchy.bodyTypeface(context)
    private fun resolveIcon(state: AndesMessageStateInterface, hierarchy: AndesMessageHierarchyInterface, context: Context) = state.icon(context, hierarchy)
    private fun resolveDismissableIcon(hierarchy: AndesMessageHierarchyInterface, context: Context) = hierarchy.dismissableIcon(hierarchy, context)
    private fun resolveDismissableIconColor(hierarchy: AndesMessageHierarchyInterface, context: Context) = hierarchy.dismissableIconColor(context)
}
